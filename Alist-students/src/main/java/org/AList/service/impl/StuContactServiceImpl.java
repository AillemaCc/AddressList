package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.ServiceException;
import org.AList.common.convention.exception.UserException;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.domain.dao.entity.*;
import org.AList.domain.dao.mapper.*;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.ContactQueryRespDTO;
import org.AList.service.CacheService.ContactCacheService;
import org.AList.service.StuContactService;
import org.AList.stream.producer.StreamEventProducer;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;
/**
 * 通讯信息服务实现层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StuContactServiceImpl extends ServiceImpl<ContactMapper, ContactDO> implements StuContactService {
    private final ContactMapper contactMapper;
    private final ContactGotoMapper contactGotoMapper;
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final MajorAndAcademyMapper majorAndAcademyMapper;
    private final ClassInfoMapper classInfoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final StreamEventProducer streamEventProducer;
    private final ContactCacheService contactCacheService;


    /**
     * 新增个人通讯信息
     * 该方法用于为指定学生添加通讯信息，包括工作单位和所在城市等
     * 操作前会验证用户身份，并检查该学生是否已存在通讯信息
     * 操作成功后会触发缓存重建事件
     *
     * @param requestParam 新增通讯信息请求参数，包含学生ID、工作单位和城市等信息
     * @throws ClientException 当用户身份验证失败、通讯信息已存在或操作异常时抛出
     */
    @Override
    public void addStudentContact(ContactAddReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        if (checkContactExist(requestParam.getStudentId())) {
            throw new UserException(ADDR_BOOK_EXIST);                                                                   //A0103：通讯录已存在
        }
        ContactDO contact =ContactDO.builder()
                .studentId(requestParam.getStudentId())
                .employer(requestParam.getEmployer())
                .city(requestParam.getCity())
                .build();
        contactMapper.insert(contact);
        sendCacheRebuildEvent(requestParam.getStudentId());
    }

    /**
     * 删除个人通讯信息
     * 该方法用于删除指定学生的通讯信息，支持逻辑删除
     * 操作前会验证用户权限，确保用户有权限删除该通讯信息
     * 操作成功后会触发缓存清除事件
     *
     * @param requestParam 删除通讯信息请求体，包含所有者ID和要删除的联系人ID
     * @throws ClientException 当用户权限验证失败、记录不存在或删除操作异常时抛出
     */
    @Override
    public void deleteStudentContact(ContactDeleteReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());
        LambdaQueryWrapper<ContactGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, requestParam.getOwnerId())
                .eq(ContactGotoDO::getContactId, requestParam.getContactId())
                .eq(ContactGotoDO::getDelFlag, 0);
        ContactGotoDO contactGoto = contactGotoMapper.selectOne(gotoQueryWrapper);
        if (Objects.isNull(contactGoto)) {
            throw new ServiceException(ADDR_NOT_FOUND);                                                                   // C0351：处理的通讯录记录不存在
        }
        LambdaUpdateWrapper<ContactDO> updateWrapper = Wrappers.lambdaUpdate(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getContactId())
                .eq(ContactDO::getDelFlag, 0)
                .set(ContactDO::getDelFlag, 1);
        int updated = contactMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new ServiceException(DEL_ADDR_ERR);                                                                    //C0311：删除个人通讯信息错误
        }
        LambdaUpdateWrapper<ContactGotoDO> gotoUpdateWrapper = Wrappers.lambdaUpdate(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, requestParam.getOwnerId())
                .eq(ContactGotoDO::getContactId, requestParam.getContactId())
                .eq(ContactGotoDO::getDelFlag, 0)
                .set(ContactGotoDO::getDelFlag, 1);
        contactGotoMapper.update(null, gotoUpdateWrapper);
        sendCacheClearEvent(requestParam.getContactId());
    }

    /**
     * 修改个人通讯信息
     * 该方法用于更新指定学生的通讯信息，如工作单位和城市等
     * 操作前会验证用户身份，并检查记录是否存在
     * 操作成功后会触发缓存重建事件
     *
     * @param requestParam 修改通讯信息请求参数，包含学生ID、新的工作单位和城市等信息
     * @throws ClientException 当用户身份验证失败、记录不存在或更新操作异常时抛出
     */
    @Override
    public void updateStudentContact(ContactUpdateReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        if (!checkContactExist(requestParam.getStudentId())) {
            throw new ServiceException(ADDR_NOT_FOUND);                                                                 //C0351：处理的通讯录记录不存在
        }
        LambdaUpdateWrapper<ContactDO> updateWrapper = Wrappers.lambdaUpdate(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getStudentId())
                .eq(ContactDO::getDelFlag, 0)
                .set(ContactDO::getEmployer, requestParam.getEmployer())
                .set(ContactDO::getCity, requestParam.getCity());
        int updated = contactMapper.update(null, updateWrapper);
        if (updated != 1) {
            throw new ServiceException(DB_UPDATE_ERR);                                                                   //C0320：数据库修改错误
        }
        sendCacheRebuildEvent(requestParam.getStudentId());
    }

    /**
     * 按学号查询通讯录信息
     * 该方法用于查询指定学生的详细通讯信息，包括基本信息和联系方式
     * 查询结果会优先从缓存获取，若缓存不存在则从数据库获取并更新缓存
     * 操作前会验证用户权限，确保用户有权限查看该通讯信息
     *
     * @param requestParam 查询通讯信息请求体，包含所有者ID和要查询的联系人ID
     * @return 返回包含学生通讯信息的响应DTO，包括学生基本信息、学院、专业、班级等
     * @throws ClientException 当用户权限验证失败、记录不存在或查询操作异常时抛出
     */
    @Override
    public ContactQueryRespDTO queryContactById(ContactQueryByIdReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());
        String redisKey = RedisKeyGenerator.genContactKey(requestParam.getOwnerId(), requestParam.getContactId());
        // 尝试从Redis获取缓存
        try {
            String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
            if (cachedData != null) {
                return objectMapper.readValue(cachedData, ContactQueryRespDTO.class);
            }
        } catch (JsonProcessingException e) {
            log.warn("反序列化缓存数据失败，将查询数据库。key: {}", redisKey, e);
        }

        // 2. 首先检查goto表中是否存在该ownerId和contactId的记录，验证用户是否拥有该通讯录
        LambdaQueryWrapper<ContactGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, requestParam.getOwnerId())
                .eq(ContactGotoDO::getContactId, requestParam.getContactId())
                .eq(ContactGotoDO::getDelFlag, 0);
        ContactGotoDO contactGoto = contactGotoMapper.selectOne(gotoQueryWrapper);

        if (Objects.isNull(contactGoto)) {
            throw new ServiceException(ADDR_NOT_FOUND);                                                                   //C0351：处理的通讯录记录不存在
        }
        String studentId=requestParam.getContactId();
        LambdaQueryWrapper<ContactDO> contactQueryWrapper = Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, studentId)
                .eq(ContactDO::getDelFlag, 0);
        ContactDO contact = contactMapper.selectOne(contactQueryWrapper);

        if (Objects.isNull(contact)) {
            throw new ServiceException(ADDR_CONTACT_MISSING);                                                           //C0352：处理的通讯录联系人不存在
        }
        String employer=contact.getEmployer();
        String city=contact.getCity();

        LambdaQueryWrapper<StudentFrameworkDO> studentInfoWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, studentId)
                .eq(StudentFrameworkDO::getDelFlag, 0);
        StudentFrameworkDO student= studentFrameWorkMapper.selectOne(studentInfoWrapper);
        if (Objects.isNull(student)) {
            throw new ServiceException(ADDR_STUDENT_MISSING);                                                           //C0353：处理的通讯录学生信息不存在
        }
        String name=student.getName();
        String enrollmentYear=student.getEnrollmentYear();
        String graduationYear=student.getGraduationYear();
        String phone=student.getPhone();
        String email=student.getEmail();

        String majorNum=student.getMajorNum();

        LambdaQueryWrapper<MajorAndAcademyDO> majorAndAcademyWrapper = Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                .eq(MajorAndAcademyDO::getMajorNum, majorNum)
                .eq(MajorAndAcademyDO::getDelFlag, 0);
        MajorAndAcademyDO majorAndAcademy=majorAndAcademyMapper.selectOne(majorAndAcademyWrapper);
        if (Objects.isNull(majorAndAcademy)) {
            throw new ServiceException(ADDR_MAJOR_MISSING);                                                             //C0354：处理的通讯录专业学院不存在
        }
        String majorName=majorAndAcademy.getMajor();
        String academyName=majorAndAcademy.getAcademy();


        String classNum=student.getClassNum();
        LambdaQueryWrapper<ClassInfoDO> classInfoWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getClassNum, classNum)
                .eq(ClassInfoDO::getDelFlag, 0);
        ClassInfoDO classInfo = classInfoMapper.selectOne(classInfoWrapper);
        if (Objects.isNull(classInfo)) {
            throw new ServiceException(ADDR_CLASS_MISSING);                                                             //C0355：处理的通讯录班级信息不存在
        }
        String className=classInfo.getClassName();

        ContactQueryRespDTO response = ContactQueryRespDTO.builder()
                .studentId(studentId)
                .name(name)
                .academy(academyName)
                .major(majorName)
                .className(className)
                .enrollmentYear(enrollmentYear)
                .graduationYear(graduationYear)
                .employer(employer)
                .city(city)
                .phone(phone)
                .email(email)
                .build();

        try {
            String jsonResponse = objectMapper.writeValueAsString(response);
            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    jsonResponse,
                    1, // 缓存时间1小时
                    TimeUnit.HOURS
            );
        } catch (JsonProcessingException e) {
            log.error("序列化联系人数据失败，无法缓存。studentId: {}", requestParam.getContactId(), e);
        }
        return response;
    }

    /**
     * 分页查询个人全量通讯信息
     * 该方法用于分页获取指定用户的所有通讯信息
     * 查询结果会根据用户ID关联的ContactGoto表进行过滤
     *
     * @param requestParam 分页查询请求参数，包含所有者ID、当前页码和每页大小等信息
     * @return 返回分页包装的通讯信息响应DTO列表，包含分页信息和通讯记录
     */
    @Override
    public IPage<ContactQueryRespDTO> queryContactList(ContactQueryAllOwnReqDTO requestParam) {
        String ownerId=requestParam.getOwnerId();
        StuIdContext.verifyLoginUser(ownerId);
        int current=requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<ContactGotoDO> queryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, ownerId)
                .eq(ContactGotoDO::getDelFlag, 0);
        List<ContactGotoDO> gotoList = contactGotoMapper.selectList(queryWrapper);
        if(Objects.isNull(gotoList)){
            return new Page<>(current,size,0);
        }
        List<String> contactIds=gotoList.stream()
                .map(ContactGotoDO::getContactId)
                .toList();

        List<String> pageContactIds = paginateIds(contactIds, current, size);

        // 3. 先查缓存
        Map<String, ContactQueryRespDTO> cachedResults = getBatchFromCache(ownerId, pageContactIds);

        // 4. 找出未命中的ID
        List<String> missedIds = pageContactIds.stream()
                .filter(id -> !cachedResults.containsKey(id))
                .toList();

        // 5. 回源查询数据库
        if (!missedIds.isEmpty()) {
            Map<String, ContactQueryRespDTO> dbResults = getFromDatabase(ownerId,missedIds);
            dbResults.forEach((id, dto) -> {
                // 6. 异步写回缓存
                CompletableFuture.runAsync(() ->
                        contactCacheService.setContactCache(ownerId, id, dto)
                );
                cachedResults.put(id, dto);
            });
        }

        // 7. 组装最终分页结果
        return buildPageResult(cachedResults, contactIds.size(), current, size);
    }

    /**
     * 恢复删除的通讯信息
     *
     * @param requestParam 恢复通讯信息请求体
     */
    @Override
    public void restoreStudentContact(ContactRestoreReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());
        ContactGotoDO deletedContactGotoDO=contactGotoMapper.selectSingleDeletedContactGoto(
                requestParam.getContactId(),
                requestParam.getOwnerId(),
                1);
        if(Objects.isNull(deletedContactGotoDO)){
            throw new ServiceException(ADDR_NOT_FOUND);                                                                 // C0351：处理的通讯录信息不存在
        }
        ContactDO deletedContact=contactMapper.selectSingleDeletedContact(
                requestParam.getContactId(),
                1
        );
        if(Objects.isNull(deletedContact)){
            throw new ServiceException(ADDR_NOT_FOUND);                                                                 // C0351：处理的通讯录信息不存在
        }
        int restoreGoto= contactGotoMapper.restoreContactGoto(requestParam.getContactId(), requestParam.getOwnerId());
        int restoreContact= contactMapper.restoreContact(requestParam.getContactId());
        if(restoreGoto!=1||restoreContact!=1){
            throw new ServiceException(DB_RESTORE_ERR);                                                                 //C0330：数据库恢复错误
        }
    }

    /**
     * 分页展示自己已删除的通讯信息
     *
     * @param requestParam 请求体
     * @return 分页返回
     */
    @Override
    public IPage<ContactQueryRespDTO> queryContactListAllDelete(ContactQueryAllOwnReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());
        log.info("开始查询已删除联系人列表，请求参数: {}", requestParam);

        int current = requestParam.getCurrent() == null ? 1 : requestParam.getCurrent();
        int size = requestParam.getSize() == null ? 10 : requestParam.getSize();
        log.info("分页参数解析完成，current={}, size={}", current, size);

        // 分页查询已删除的联系人
        Page<ContactGotoDO> page = new Page<>(current, size);
        IPage<ContactGotoDO> gotoResult = contactGotoMapper.selectDeletedContact(page, requestParam.getOwnerId());

        log.info("SQL 查询结果获取完成，共 {} 条记录", gotoResult.getTotal());

        if (gotoResult.getTotal() == 0) {
            log.warn("未查询到任何已删除联系人记录");
        }

        List<ContactQueryRespDTO> dtoList = gotoResult.getRecords().stream()
                .map(gotoRecord -> {
                    log.debug("开始处理单条联系人记录，contactId={}", gotoRecord.getContactId());

                    // 查询联系人基本信息
                    ContactDO contact = contactMapper.selectOne(Wrappers.lambdaQuery(ContactDO.class)
                            .eq(ContactDO::getStudentId, gotoRecord.getContactId())
                            .eq(ContactDO::getDelFlag, 0));
                    if (contact == null) {
                        log.warn("联系人信息不存在或已被删除，contactId={}", gotoRecord.getContactId());
                    }

                    // 查询学生框架信息
                    StudentFrameworkDO student = studentFrameWorkMapper.selectOne(Wrappers.lambdaQuery(StudentFrameworkDO.class)
                            .eq(StudentFrameworkDO::getStudentId, gotoRecord.getContactId())
                            .eq(StudentFrameworkDO::getDelFlag, 0));
                    if (student == null) {
                        log.warn("学生信息不存在或已被删除，studentId={}", gotoRecord.getContactId());
                        return null;
                    }

                    // 查询专业和学院名称
                    String majorName = "";
                    String academyName = "";
                    if (student.getMajorNum() != null) {
                        MajorAndAcademyDO majorAndAcademy = majorAndAcademyMapper.selectOne(
                                Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                                        .eq(MajorAndAcademyDO::getMajorNum, student.getMajorNum())
                                        .eq(MajorAndAcademyDO::getDelFlag, 0));
                        if (majorAndAcademy != null) {
                            majorName = majorAndAcademy.getMajor();
                            academyName = majorAndAcademy.getAcademy();
                            log.debug("成功获取专业和学院信息: major={}, academy={}", majorName, academyName);
                        } else {
                            log.warn("未找到对应的专业信息，majorNum={}", student.getMajorNum());
                        }
                    }

                    // 查询班级名称
                    String className = "";
                    if (student.getClassNum() != null) {
                        ClassInfoDO classInfo = classInfoMapper.selectOne(
                                Wrappers.lambdaQuery(ClassInfoDO.class)
                                        .eq(ClassInfoDO::getClassNum, student.getClassNum())
                                        .eq(ClassInfoDO::getDelFlag, 0));
                        if (classInfo != null) {
                            className = classInfo.getClassName();
                            log.debug("成功获取班级名称: className={}", className);
                        } else {
                            log.warn("未找到对应的班级信息，classNum={}", student.getClassNum());
                        }
                    }

                    // 构建 DTO 返回对象
                    ContactQueryRespDTO dto = ContactQueryRespDTO.builder()
                            .studentId(gotoRecord.getContactId())
                            .name(student.getName())
                            .academy(academyName)
                            .major(majorName)
                            .className(className)
                            .enrollmentYear(student.getEnrollmentYear())
                            .graduationYear(student.getGraduationYear())
                            .employer(contact != null ? contact.getEmployer() : null)
                            .city(contact != null ? contact.getCity() : null)
                            .phone(student.getPhone())
                            .email(student.getEmail())
                            .build();

                    log.debug("成功构建 ContactQueryRespDTO: {}", dto);
                    return dto;

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("最终转换后的有效记录数为: {}", dtoList.size());

        // 手动构造新的分页对象并设置数据
        Page<ContactQueryRespDTO> result = new Page<>();
        result.setCurrent(gotoResult.getCurrent());
        result.setSize(gotoResult.getSize());
        result.setTotal(gotoResult.getTotal());
        result.setPages(gotoResult.getPages());
        result.setRecords(dtoList);

        log.info("分页信息构建完成，当前页码={}, 总页数={}, 总记录数={}",
                result.getCurrent(), result.getPages(), result.getTotal());

        return result;
    }

    /**
     * 发送缓存重建事件（异步）
     */
    private void sendCacheRebuildEvent(String studentId) {
        // 获取所有ownerIds
        List<String> ownerIds = contactGotoMapper.selectOwnerIdsByContactId(studentId);
        try {
            // 发送事件到Redis Stream
            streamEventProducer.sendRebuildEvent(studentId, ownerIds);
            log.info("Sent cache rebuild event for student: {}", studentId);
        } catch (Exception e) {
            log.error("Failed to send cache rebuild event", e);
            // 降级策略：同步执行缓存重建
            contactCacheService.rebuildContactCache(studentId,ownerIds);
        }
    }

    /**
     * 发送缓存删除事件（异步）
     */
    private void sendCacheClearEvent(String studentId) {
        try {
            // 发送事件到Redis Stream
            streamEventProducer.sendClearEvent(studentId);
            log.info("Sent cache clear event for student: {}", studentId);
        } catch (Exception e) {
            log.error("Failed to send cache clear event", e);
            // 降级策略：同步执行缓存清除
            contactCacheService.clearContactCache(studentId);
        }
    }

    /**
     * 检查指定学生的通讯信息是否存在
     * @param studentId 学生ID
     * @return true-存在，false-不存在
     */
    private boolean checkContactExist(String studentId) {
        return contactMapper.selectCount(
                new LambdaQueryWrapper<ContactDO>()
                        .eq(ContactDO::getStudentId, studentId)
                        .eq(ContactDO::getDelFlag, 0)
        ) > 0;
    }

    /**
     * 对ID列表进行内存分页
     * @param allIds 全部ID集合
     * @param current 当前页码（从1开始）
     * @param size 每页大小
     * @return 当前页的ID子集
     */
    private List<String> paginateIds(List<String> allIds, int current, int size) {
        // 参数校验
        if (CollectionUtils.isEmpty(allIds)) {
            return Collections.emptyList();
        }
        if (current < 1 || size < 1) {
            throw new ClientException(PAGE_PARAM_NEGATIVE);                                                             //B0201：分页参数小于0
        }

        // 计算分页偏移量
        int startIndex = (current - 1) * size;
        if (startIndex >= allIds.size()) {
            return Collections.emptyList();
        }

        // 计算结束位置（防止越界）
        int endIndex = Math.min(startIndex + size, allIds.size());

        // 返回分页子集
        return allIds.subList(startIndex, endIndex);
    }

    /**
     * 批量从缓存获取数据
     */
    private Map<String, ContactQueryRespDTO> getBatchFromCache(String ownerId, List<String> contactIds) {
        return contactIds.stream()
                .map(id -> {
                    ContactQueryRespDTO dto = contactCacheService.getContactCache(ownerId, id);
                    boolean isNotNullCached = contactCacheService.isContactNullValueCached(ownerId, id);
                    // 如果是空值缓存，则dto应视为null，避免后续处理该id
                    if (!isNotNullCached) {
                        dto = null;
                    }
                    return Pair.of(id, dto);
                })
                // 过滤掉所有缓存和空值缓存都不存在的情况
                .filter(pair -> pair.getRight() != null)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    /**
     * 从数据库批量查询联系人信息（包含学生框架数据）
     */
    private Map<String, ContactQueryRespDTO> getFromDatabase(String ownerId, List<String> contactIds) {
        // 1. 查询基础联系人信息
        List<ContactDO> dbList = contactMapper.selectList(
                Wrappers.lambdaQuery(ContactDO.class)
                        .in(ContactDO::getStudentId, contactIds)
                        .eq(ContactDO::getDelFlag, 0));

        // 2. 查询学生框架信息
        List<StudentFrameworkDO> students = studentFrameWorkMapper.selectList(
                Wrappers.lambdaQuery(StudentFrameworkDO.class)
                        .in(StudentFrameworkDO::getStudentId, contactIds)
                        .eq(StudentFrameworkDO::getDelFlag, 0));

        // 3. 批量查询专业和学院信息
        Set<String> majorNums = students.stream()
                .map(StudentFrameworkDO::getMajorNum)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, MajorAndAcademyDO> majorMap = majorNums.isEmpty() ? Collections.emptyMap() :
                majorAndAcademyMapper.selectList(
                                Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                                        .in(MajorAndAcademyDO::getMajorNum, majorNums)
                                        .eq(MajorAndAcademyDO::getDelFlag, 0))
                        .stream()
                        .collect(Collectors.toMap(MajorAndAcademyDO::getMajorNum, Function.identity()));

        // 4. 批量查询班级信息
        Set<String> classNums = students.stream()
                .map(StudentFrameworkDO::getClassNum)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, ClassInfoDO> classMap = classNums.isEmpty() ? Collections.emptyMap() :
                classInfoMapper.selectList(
                                Wrappers.lambdaQuery(ClassInfoDO.class)
                                        .in(ClassInfoDO::getClassNum, classNums)
                                        .eq(ClassInfoDO::getDelFlag, 0))
                        .stream()
                        .collect(Collectors.toMap(ClassInfoDO::getClassNum, Function.identity()));

        // 5. 构建结果
        Map<String, ContactQueryRespDTO> result = new HashMap<>();

        // 按contactId组织联系人信息
        Map<String, ContactDO> contactMap = dbList.stream()
                .collect(Collectors.toMap(ContactDO::getStudentId, Function.identity()));

        // 按studentId组织学生信息
        Map<String, StudentFrameworkDO> studentMap = students.stream()
                .collect(Collectors.toMap(StudentFrameworkDO::getStudentId, Function.identity()));

        for (String contactId : contactIds) {
            ContactDO contact = contactMap.get(contactId);
            StudentFrameworkDO student = studentMap.get(contactId);

            if (contact == null || student == null) {
                // 处理空值缓存
                contactCacheService.setContactNullValueCache(ownerId, contactId);
                continue;
            }

            // 获取专业和学院信息
            String majorName = "";
            String academyName = "";
            if (student.getMajorNum() != null) {
                MajorAndAcademyDO majorAndAcademy = majorMap.get(Integer.parseInt(student.getMajorNum()));
                if (majorAndAcademy != null) {
                    majorName = majorAndAcademy.getMajor();
                    academyName = majorAndAcademy.getAcademy();
                }
            }

            // 获取班级信息
            String className = "";
            if (student.getClassNum() != null) {
                ClassInfoDO classInfo = classMap.get(Integer.parseInt(student.getClassNum()));
                if (classInfo != null) {
                    className = classInfo.getClassName();
                }
            }

            // 构建DTO
            ContactQueryRespDTO dto = ContactQueryRespDTO.builder()
                    .studentId(contactId)
                    .name(student.getName())
                    .academy(academyName)
                    .major(majorName)
                    .className(className)
                    .enrollmentYear(student.getEnrollmentYear())
                    .graduationYear(student.getGraduationYear())
                    .employer(contact.getEmployer())
                    .city(contact.getCity())
                    .phone(student.getPhone())
                    .email(student.getEmail())
                    .build();

            result.put(contactId, dto);

            // 异步更新缓存
            CompletableFuture.runAsync(() ->
                    contactCacheService.setContactCache(ownerId, contactId, dto));
        }

        return result;
    }

    /**
     * 构建分页结果
     */
    private IPage<ContactQueryRespDTO> buildPageResult(
            Map<String, ContactQueryRespDTO> dataMap,
            int total,
            int current,
            int size) {

        List<ContactQueryRespDTO> records = new ArrayList<>(dataMap.values());
        Page<ContactQueryRespDTO> page = new Page<>(current, size, total);
        page.setRecords(records);
        return page;
    }

}
