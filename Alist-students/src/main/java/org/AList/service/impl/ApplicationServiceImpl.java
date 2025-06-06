package org.AList.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.UserException;
import org.AList.common.enums.StudentChainMarkEnum;
import org.AList.designpattern.chain.AbstractChainContext;
import org.AList.domain.dao.entity.*;
import org.AList.domain.dao.mapper.*;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.ApplicationQueryPageRespDTO;
import org.AList.domain.dto.resp.QuerySomeoneRespDTO;
import org.AList.service.ApplicationService;
import org.AList.service.CacheService.StudentSearchCacheService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;
/**
 * 站内信方法实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, ApplicationDO> implements ApplicationService {
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final ApplicationMapper applicationMapper;
    private final ContactGotoMapper contactGotoMapper;
    private final MajorAndAcademyMapper majorAndAcademyMapper;
    private final ClassInfoMapper classInfoMapper;
    private final AbstractChainContext<ApplicationSendMsgReqDTO> abstractChainContext;
    private final StudentSearchCacheService studentSearchCacheService;


    /**
     * 发送站内信申请
     *
     * @param requestParam 发送请求参数，包含接收者ID和消息内容
     * @throws ClientException 如果接收者不存在、发送给自己、或已发送过申请
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendApplication(ApplicationSendMsgReqDTO requestParam) {
        abstractChainContext.handler(StudentChainMarkEnum.APPLICATION_SEND_FILTER.name(), requestParam);
        LambdaQueryWrapper<StudentFrameworkDO> senderWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, StuIdContext.getStudentId());
        StudentFrameworkDO sender = studentFrameWorkMapper.selectOne(senderWrapper);
        String senderName=sender.getName();

        LambdaQueryWrapper<StudentFrameworkDO> receiverWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, requestParam.getReceiver());
        StudentFrameworkDO receiver = studentFrameWorkMapper.selectOne(receiverWrapper);
        String receiverName=receiver.getName();
        ApplicationDO applicationDO =ApplicationDO.builder()
                .sender(StuIdContext.getStudentId())
                .senderName(senderName)
                .receiver(requestParam.getReceiver())
                .receiverName(receiverName)
                .content(requestParam.getContent())
                .status(0)
                .build();
        int insert = applicationMapper.insert(applicationDO);
        if(insert != 1) {
            throw new UserException(APPLY_ERR);                                                                         //A0300：用户发送申请错误
        }
    }

    /**
     * 查询所有有效的（未处理）站内信申请
     *
     * @param requestParam 分页查询参数，包含接收者ID
     * @return 分页查询结果，包含站内信列表
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllValidApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getReceiver());
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<ApplicationDO> queryWrapper = buildBaseQueryWrapper(requestParam.getReceiver(),0);
        IPage<ApplicationDO> resultPage=page(new Page<>(current,size),queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 查询所有已同意的站内信申请
     *
     * @param requestParam 分页查询参数，包含接收者ID
     * @return 分页查询结果，包含站内信列表
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllAcceptedApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getReceiver());
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<ApplicationDO> queryWrapper = buildBaseQueryWrapper(requestParam.getReceiver(),1);
        IPage<ApplicationDO> resultPage=page(new Page<>(current,size),queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 查询所有已拒绝的站内信申请
     *
     * @param requestParam 分页查询参数，包含接收者ID
     * @return 分页查询结果，包含站内信列表
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllRefusedApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getReceiver());
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<ApplicationDO> queryWrapper = buildBaseQueryWrapper(requestParam.getReceiver(),2);
        IPage<ApplicationDO> resultPage=page(new Page<>(current,size),queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 查询所有已删除的站内信申请
     *
     * @param requestParam 分页查询参数，包含接收者ID
     * @return 分页查询结果，包含站内信列表
     * &#064;todo  分页查询功能有待测试
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllDeleteApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getReceiver());
        int current=requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        Page<ApplicationDO> page = new Page<>(current,size);
        IPage<ApplicationDO> resultPage = applicationMapper.selectDeletedApplications(
                page,
                requestParam.getReceiver(),
                1
        );
        return resultPage.convert(each -> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 查询所有已发送的站内信请求
     *
     * @param requestParam 分页查询参数，包含发送者ID
     * @return 分页查询结果，包含站内信列表
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllSendApplication(ApplicationSendQueryPageReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getSender());
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getSender, requestParam.getSender())
                .eq(ApplicationDO::getDelFlag, 0);
        IPage<ApplicationDO> resultPage=baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 同意单个站内信申请
     * <p>
     * 同意后会将接收者的联系信息展示给发送者，也会吧发送者的信息展示给接收者
     *
     * @param requestParam 请求参数，包含发送者和接收者ID
     * @throws ClientException 如果未找到申请记录或更新失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptSingleApplication(ApplicationYONReqDTO requestParam) {
        // 构建查询并更新 application 表的状态为 1（已同意）
        int updated = baseMapper.update(null,
                Wrappers.<ApplicationDO>lambdaUpdate()
                        .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                        .eq(ApplicationDO::getSender, requestParam.getSender())
                        .eq(ApplicationDO::getStatus, 0)
                        .eq(ApplicationDO::getDelFlag, 0)
                        .set(ApplicationDO::getStatus, 1));

        if (updated<=0) {
            throw new UserException(NO_PENDING_APPLY); // A0401: 不存在待处理申请
        }

        // 创建双向联系关系
        ContactGotoDO senderToReceiver = ContactGotoDO.builder()
                .contactId(requestParam.getReceiver())
                .ownerId(requestParam.getSender())
                .build();

        ContactGotoDO receiverToSender = ContactGotoDO.builder()
                .contactId(requestParam.getSender())
                .ownerId(requestParam.getReceiver())
                .build();

        int insert1 = contactGotoMapper.insert(senderToReceiver);
        int insert2 = contactGotoMapper.insert(receiverToSender);

        if (insert1 != 1 || insert2 != 1) {
            throw new UserException(APPROVE_ERR); // 插入失败
        }
    }

    /**
     * 拒绝单个站内信申请
     *
     * @param requestParam 请求参数，包含发送者和接收者ID
     * @throws RuntimeException 如果未找到申请记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuseSingleApplication(ApplicationYONReqDTO requestParam) {
        int updated = baseMapper.update(null,
                Wrappers.<ApplicationDO>lambdaUpdate()
                        .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                        .eq(ApplicationDO::getSender, requestParam.getSender())
                        .eq(ApplicationDO::getStatus, 0)
                        .eq(ApplicationDO::getDelFlag, 0)
                        .set(ApplicationDO::getStatus, 2));

        if (updated<=0) {
            throw new UserException(NO_PENDING_APPLY); // A0401: 不存在待处理申请
        }
    }

    /**
     * 删除单个站内信申请
     * 逻辑删除，将delFlag设置为1
     *
     * @param requestParam 请求参数，包含发送者和接收者ID
     * @throws RuntimeException 如果未找到申请记录
     */
    @Override
    @Transactional
    public void deleteSingleApplication(ApplicationYONReqDTO requestParam) {
        LambdaUpdateWrapper<ApplicationDO> updateWrapper = Wrappers.lambdaUpdate(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getSender, requestParam.getSender())
                .eq(ApplicationDO::getDelFlag, 0)
                .set(ApplicationDO::getDelFlag, 1);
        int updated = applicationMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new UserException(NO_PENDING_APPLY);                                                                  //A0401：不存在待处理申请
        }
    }

    /**
     * 模糊查询学生信息
     */
    @Override
    public IPage<QuerySomeoneRespDTO> querySomeone(QuerySomeoneReqDTO requestParam) {
        log.info("查询的学生姓名为{}", requestParam.getName());
        int current = requestParam.getCurrent() == null ? 0 : requestParam.getCurrent();
        int size = requestParam.getSize() == null ? 10 : requestParam.getSize();
        String cacheKey= studentSearchCacheService.generateCacheKey(requestParam);
        IPage<QuerySomeoneRespDTO> cachedResult = studentSearchCacheService.getCachedSearchResult(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }


        // 1. 先查询学生基础信息（分页）
        Page<StudentFrameworkDO> page = new Page<>(current, size);
        LambdaQueryWrapper<StudentFrameworkDO> queryWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .like(StringUtils.isNotBlank(requestParam.getName()),
                        StudentFrameworkDO::getName, requestParam.getName())
                .eq(StudentFrameworkDO::getDelFlag, 0);
        IPage<StudentFrameworkDO> studentPage = studentFrameWorkMapper.selectPage(page, queryWrapper);

        // 如果没有查询到学生，直接返回空结果
        if (studentPage.getRecords().isEmpty()) {
            return new Page<>(current, size, 0);
        }

        // 2. 批量获取所有需要的 majorNum 和 classNum
        Set<String> majorNums = studentPage.getRecords().stream()
                .map(StudentFrameworkDO::getMajorNum)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        Set<String> classNums = studentPage.getRecords().stream()
                .map(StudentFrameworkDO::getClassNum)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        // 3. 批量查询专业和学院信息
        Map<Integer, MajorAndAcademyDO> majorMap = Collections.emptyMap();
        if (!majorNums.isEmpty()) {
            majorMap = majorAndAcademyMapper.selectList(
                    Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                            .in(MajorAndAcademyDO::getMajorNum, majorNums)
                            .eq(MajorAndAcademyDO::getDelFlag, 0)
            ).stream().collect(Collectors.toMap(
                    MajorAndAcademyDO::getMajorNum,
                    Function.identity(),
                    (existing, replacement) -> existing // 处理重复key
            ));
        }

        // 4. 批量查询班级信息
        Map<Integer, ClassInfoDO> classMap = Collections.emptyMap();
        if (!classNums.isEmpty()) {
            classMap = classInfoMapper.selectList(
                    Wrappers.lambdaQuery(ClassInfoDO.class)
                            .in(ClassInfoDO::getClassNum, classNums)
                            .eq(ClassInfoDO::getDelFlag, 0)
            ).stream().collect(Collectors.toMap(
                    ClassInfoDO::getClassNum,
                    Function.identity(),
                    (existing, replacement) -> existing // 处理重复key
            ));
        }

        final Map<Integer, MajorAndAcademyDO> finalMajorMap = majorMap;
        final Map<Integer, ClassInfoDO> finalClassMap = classMap;

        IPage<QuerySomeoneRespDTO> result = studentPage.convert(student -> {
            QuerySomeoneRespDTO dto = new QuerySomeoneRespDTO();
            BeanUtils.copyProperties(student, dto);

            if (StringUtils.isNotBlank(student.getMajorNum())) {
                MajorAndAcademyDO majorAndAcademy = finalMajorMap.get(Integer.parseInt(student.getMajorNum()));
                if (majorAndAcademy != null) {
                    dto.setMajorName(majorAndAcademy.getMajor());
                    dto.setAcademyName(majorAndAcademy.getAcademy());
                }
            }

            if (StringUtils.isNotBlank(student.getClassNum())) {
                ClassInfoDO classInfo = finalClassMap.get(Integer.parseInt(student.getClassNum()));
                if (classInfo != null) {
                    dto.setClassName(classInfo.getClassName());
                }
            }

            if (StringUtils.isNotBlank(student.getEnrollmentYear())) {
                dto.setEnrollmentYear(student.getEnrollmentYear());
                dto.setGraduationYear(student.getGraduationYear());
            }

            if (StringUtils.isNotBlank(student.getStudentId())) {
                dto.setStudentId(student.getStudentId());
            }

            return dto;
        });

        // 缓存查询结果
        studentSearchCacheService.cacheSearchResult(cacheKey, result);

        return result;
    }


    // 提取公共查询条件
    private LambdaQueryWrapper<ApplicationDO> buildBaseQueryWrapper(String receiver, Integer status) {
        return Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, receiver)
                .eq(status != null, ApplicationDO::getStatus, status)
                .eq(ApplicationDO::getDelFlag, 0);
    }


}
