package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.*;
import org.AList.domain.dao.mapper.*;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.ContactQueryRespDTO;
import org.AList.service.CacheService.ContactCacheService;
import org.AList.service.StuContactService;
import org.AList.stream.event.StreamEvent;
import org.AList.stream.producer.StreamEventProducer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 通讯信息服务实现层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StuContactServiceImpl extends ServiceImpl<ContactMapper, ContactDO> implements StuContactService {
    private final ContactMapper contactMapper;
    private final ContactGotoMapper contactGotoMapper;
    private final StudentMapper studentMapper;
    private final MajorAndAcademyMapper majorAndAcademyMapper;
    private final ClassInfoMapper classInfoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final StreamEventProducer streamEventProducer;
    private final ContactCacheService contactCacheService;

    /**
     * 新增个人通讯信息
     *
     * @param requestParam 新增通讯信息请求体
     */
    @Override
    public void addStudentContact(ContactAddReqDTO requestParam) {

        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        ContactDO contact =ContactDO.builder()
                .studentId(requestParam.getStudentId())
                .employer(requestParam.getEmployer())
                .city(requestParam.getCity())
                .build();
        contactMapper.insert(contact);
        // 新增后清除并重建该学生的缓存
        sendCacheRebuildEvent(requestParam.getStudentId());
    }

    /**
     * 删除个人通讯信息
     *
     * @param requestParam 删除通讯信息请求体
     */
    @Override
    public void deleteStudentContact(ContactDeleteReqDTO requestParam) {
        // 1. 验证当前登录用户
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());

        // 2. 首先检查goto表中是否存在该ownerId和contactId的记录，验证用户是否拥有该通讯录
        LambdaQueryWrapper<ContactGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, requestParam.getOwnerId())
                .eq(ContactGotoDO::getContactId, requestParam.getContactId())
                .eq(ContactGotoDO::getDelFlag, 0);
        ContactGotoDO contactGoto = contactGotoMapper.selectOne(gotoQueryWrapper);

        if (Objects.isNull(contactGoto)) {
            throw new ClientException("您没有权限删除此通讯录信息或记录不存在");
        }

        // 3. 逻辑删除Contact表中的记录（设置del_flag=1）
        LambdaUpdateWrapper<ContactDO> updateWrapper = Wrappers.lambdaUpdate(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getContactId())
                .eq(ContactDO::getDelFlag, 0)
                .set(ContactDO::getDelFlag, 1);
        int updated = contactMapper.update(null, updateWrapper);

        if (updated == 0) {
            throw new ClientException("删除个人通讯信息出现异常，请重试");
        }

        // 4. 同时逻辑删除goto表中的关联记录（根据业务需求决定）
        LambdaUpdateWrapper<ContactGotoDO> gotoUpdateWrapper = Wrappers.lambdaUpdate(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, requestParam.getOwnerId())
                .eq(ContactGotoDO::getContactId, requestParam.getContactId())
                .eq(ContactGotoDO::getDelFlag, 0)
                .set(ContactGotoDO::getDelFlag, 1);
        contactGotoMapper.update(null, gotoUpdateWrapper);

        // 删除后发送缓存清除事件
        try {
            StreamEvent event = StreamEvent.builder()
                    .eventType("CACHE_CLEAR")
                    .studentId(requestParam.getContactId())
                    .timestamp(System.currentTimeMillis())
                    .build();

            streamEventProducer.sendEvent(event);
        } catch (Exception e) {
            log.error("Failed to send cache clear event", e);
            // 降级同步清除
            contactCacheService.clearContactCache(requestParam.getContactId());
        }
    }

    /**
     * 修改个人通讯信息
     *
     * @param requestParam 修改通讯信息请求体
     */
    @Override
    public void updateStudentContact(ContactUpdateReqDTO requestParam) {
        // 1. 验证当前登录用户
        StuIdContext.verifyLoginUser(requestParam.getStudentId());

        // 2. 检查记录是否存在
        LambdaQueryWrapper<ContactDO> queryWrapper = Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getStudentId())
                .eq(ContactDO::getDelFlag, 0);
        if (contactMapper.selectOne(queryWrapper) == null) {
            throw new ClientException("修改的记录不存在");
        }

        // 3. 构建更新内容和条件
        LambdaUpdateWrapper<ContactDO> updateWrapper = Wrappers.lambdaUpdate(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getStudentId())
                .eq(ContactDO::getDelFlag, 0)
                .set(ContactDO::getEmployer, requestParam.getEmployer())  // 设置要更新的字段
                .set(ContactDO::getCity, requestParam.getCity());
        // 4. 执行更新
        int updated = contactMapper.update(null, updateWrapper);
        if (updated != 1) {
            throw new ClientException("修改错误");
        }
        sendCacheRebuildEvent(requestParam.getStudentId());
    }

    /**
     * 按学号查询通讯录信息
     *
     * @param requestParam 查询通讯信息请求体
     * @return 单个学生的通讯信息
     */
    @Override
    public ContactQueryRespDTO queryContactById(ContactQueryByIdReqDTO requestParam) {
        // 1. 验证当前登录用户
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());

        // 构建Redis缓存key
        String redisKey = String.format("contact:%s:%s",
                requestParam.getOwnerId(),
                requestParam.getContactId());

        // 尝试从Redis获取缓存
        try {
            String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
            if (cachedData != null) {
                // 反序列化缓存数据
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
            throw new ClientException("您没有权限查看此通讯录信息或记录不存在");
        }
        String studentId=requestParam.getContactId();
        // 3. 从contact表中查询完整的通讯录信息
        LambdaQueryWrapper<ContactDO> contactQueryWrapper = Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, studentId)
                .eq(ContactDO::getDelFlag, 0);
        ContactDO contact = contactMapper.selectOne(contactQueryWrapper);

        if (Objects.isNull(contact)) {
            throw new ClientException("通讯录信息不存在或已被删除");
        }
        String employer=contact.getEmployer();
        String city=contact.getCity();

        LambdaQueryWrapper<StudentDO> studentInfoWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getStudentId, studentId)
                .eq(StudentDO::getDelFlag, 0);
        StudentDO student=studentMapper.selectOne(studentInfoWrapper);
        if (Objects.isNull(student)) {
            throw new ClientException("通讯录信息不存在或已被删除");
        }
        String name=student.getName();
        String enrollmentYear=student.getEnrollmentYear();
        String graduationYear=student.getGraduationYear();
        String phone=student.getPhone();
        String email=student.getEmail();

        String majorNum=student.getMajor();

        LambdaQueryWrapper<MajorAndAcademyDO> majorAndAcademyWrapper = Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                .eq(MajorAndAcademyDO::getMajorNum, majorNum)
                .eq(MajorAndAcademyDO::getDelFlag, 0);
        MajorAndAcademyDO majorAndAcademy=majorAndAcademyMapper.selectOne(majorAndAcademyWrapper);
        if (Objects.isNull(majorAndAcademy)) {
            throw new ClientException("通讯录信息不存在或已被删除");
        }
        String majorName=majorAndAcademy.getMajor();
        String academyName=majorAndAcademy.getAcademy();


        String classNum=student.getClassName();
        LambdaQueryWrapper<ClassInfoDO> classInfoWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getClassNum, classNum)
                .eq(ClassInfoDO::getDelFlag, 0);
        ClassInfoDO classInfo = classInfoMapper.selectOne(classInfoWrapper);
        if (Objects.isNull(classInfo)) {
            throw new ClientException("通讯录信息不存在或已被删除");
        }
        String className=classInfo.getClassName();

        // 4. 转换为响应DTO

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
     *
     * @return 分页返回
     */
    @Override
    public IPage<ContactQueryRespDTO> queryContactList(ContactQueryAllOwnReqDTO requestParam) {
        String ownerId=requestParam.getOwnerId();
        LambdaQueryWrapper<ContactGotoDO> queryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, ownerId)
                .eq(ContactGotoDO::getDelFlag, 0);
        List<ContactGotoDO> gotoList = contactGotoMapper.selectList(queryWrapper);
        if(Objects.isNull(gotoList)){
            return new Page<>(1,10,0);
        }
        List<String> contactIds=gotoList.stream()
                .map(ContactGotoDO::getContactId)
                .toList();
        LambdaQueryWrapper<ContactDO> contactQueryWrapper = Wrappers.lambdaQuery(ContactDO.class)
                .in(ContactDO::getStudentId, contactIds)
                .eq(ContactDO::getDelFlag, 0);
        Page<ContactDO> page = new Page<>(1,10);
        IPage<ContactDO> contactPage = contactMapper.selectPage(page, contactQueryWrapper);
        return contactPage.convert(contactDO -> {
            ContactQueryRespDTO respDTO = new ContactQueryRespDTO();
            // 这里进行属性拷贝，可以使用BeanUtils或者手动set
            BeanUtils.copyProperties(contactDO, respDTO);
            return respDTO;
        });
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
            throw new ClientException("您没有权限恢复此通讯录信息或记录不存在");
        }
        ContactDO deletedContact=contactMapper.selectSingleDeletedContact(
                requestParam.getContactId(),
                1
        );
        if(Objects.isNull(deletedContact)){
            throw new ClientException("您没有权限恢复此通讯录信息或记录不存在");
        }
        int restoreGoto= contactGotoMapper.restoreContactGoto(requestParam.getContactId(), requestParam.getOwnerId());
        int restoreContact= contactMapper.restoreContact(requestParam.getContactId());
        if(restoreGoto!=1||restoreContact!=1){
            throw new ClientException("恢复失败，记录可能不存在或未被删除");
        }
    }

    /**
     * 发送缓存重建事件（异步）
     */
    private void sendCacheRebuildEvent(String studentId) {
        try {
            // 获取所有ownerIds
            List<String> ownerIds = contactGotoMapper.selectOwnerIdsByContactId(studentId);

            // 发送事件到Redis Stream
            streamEventProducer.sendRebuildEvent(studentId, ownerIds);
            log.info("Sent cache rebuild event for student: {}", studentId);
        } catch (Exception e) {
            log.error("Failed to send cache rebuild event", e);
            // 降级策略：同步执行缓存重建
            contactCacheService.rebuildContactCache(studentId);
        }
    }
}
