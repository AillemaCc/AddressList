package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ServiceException;
import org.AList.domain.dao.entity.*;
import org.AList.domain.dao.mapper.*;
import org.AList.domain.dto.resp.ContactQueryRespDTO;
import org.AList.domain.dto.resp.DataStatisticDTO;
import org.AList.domain.dto.resp.HomePageDataDTO;
import org.AList.domain.dto.resp.StuInfoDTO;
import org.AList.service.CacheService.ContactCacheService;
import org.AList.service.StuHomePageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.AList.common.convention.errorcode.BaseErrorCode.ADDR_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class StuHomePageServiceImpl implements StuHomePageService {
    private final ContactCacheService contactCacheService;
    private final ContactMapper contactMapper;
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final MajorAndAcademyMapper majorAndAcademyMapper;
    private final ClassInfoMapper classInfoMapper;
    private final ApplicationMapper applicationMapper;
    private final ContactGotoMapper contactGotoMapper;

    @Override
    public HomePageDataDTO queryHomepageInfo(String studentId) {
        // 1. 获取学生信息
        StuInfoDTO stuInfo = getStuInfo(studentId);

        // 2. 获取统计数据
        DataStatisticDTO dataStatistic = getDataStatistic(studentId);

        // 3. 构建响应
        return HomePageDataDTO.builder()
                .stuInfo(stuInfo)
                .dataStatistic(dataStatistic)
                .build();
    }

    /**
     * 获取学生信息
     */
    private StuInfoDTO getStuInfo(String studentId) {
        // 1. 尝试从缓存获取
        ContactQueryRespDTO cachedData = contactCacheService.getContactCache(studentId, studentId);

        if (cachedData != null) {
            // 将 ContactQueryRespDTO 转换为 StuInfoDTO
            return convertToStuInfoDTO(cachedData);
        }

        // 2. 缓存未命中，从数据库获取
        // 2.1 获取联系人信息
        ContactDO contact = contactMapper.selectOne(Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, studentId)
                .eq(ContactDO::getDelFlag, 0));

        if (Objects.isNull(contact)) {
            log.warn("学生通讯信息不存在, studentId: {}", studentId);
            throw new ServiceException(ADDR_NOT_FOUND);
        }

        // 2.2 获取学生基本信息
        StudentFrameworkDO student = studentFrameWorkMapper.selectOne(Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, studentId)
                .eq(StudentFrameworkDO::getDelFlag, 0));

        if (Objects.isNull(student)) {
            log.warn("学生基本信息不存在, studentId: {}", studentId);
            throw new ServiceException(ADDR_NOT_FOUND);
        }

        // 2.3 获取专业和学院信息
        String majorName = "";
        String academyName = "";
        if (student.getMajorNum() != null) {
            LambdaQueryWrapper<MajorAndAcademyDO> majorAndAcademyWrapper = Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                    .eq(MajorAndAcademyDO::getMajorNum, student.getMajorNum())
                    .eq(MajorAndAcademyDO::getDelFlag, 0);
            MajorAndAcademyDO majorAndAcademy = majorAndAcademyMapper.selectOne(majorAndAcademyWrapper);
            if (majorAndAcademy != null) {
                majorName = majorAndAcademy.getMajor();
                academyName = majorAndAcademy.getAcademy();
            }
        }

        // 2.4 获取班级信息
        String className = "";
        if (student.getClassNum() != null) {
            LambdaQueryWrapper<ClassInfoDO> classInfoWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                    .eq(ClassInfoDO::getClassNum, student.getClassNum())
                    .eq(ClassInfoDO::getDelFlag, 0);
            ClassInfoDO classInfo = classInfoMapper.selectOne(classInfoWrapper);
            if (classInfo != null) {
                className = classInfo.getClassName();
            }
        }

        // 3. 构建学生信息响应对象
        StuInfoDTO stuInfo = StuInfoDTO.builder()
                .studentId(studentId)
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

        // 4. 构建缓存对象并异步更新缓存
        ContactQueryRespDTO cacheDTO = ContactQueryRespDTO.builder()
                .studentId(studentId)
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

        CompletableFuture.runAsync(() ->
                contactCacheService.setContactCache(studentId, studentId, cacheDTO)
        );

        return stuInfo;
    }

    /**
     * 获取统计数据
     */
    private DataStatisticDTO getDataStatistic(String studentId) {
        // 查询已通过的申请数量 (作为接收者，status = 1)
        Integer approved = Math.toIntExact(applicationMapper.selectCount(
                Wrappers.lambdaQuery(ApplicationDO.class)
                        .eq(ApplicationDO::getReceiver, studentId)
                        .eq(ApplicationDO::getStatus, 1)
                        .eq(ApplicationDO::getDelFlag, 0)
        ));

        // 查询已拒绝的申请数量 (作为接收者，status = 2)
        Integer rejected = Math.toIntExact(applicationMapper.selectCount(
                Wrappers.lambdaQuery(ApplicationDO.class)
                        .eq(ApplicationDO::getReceiver, studentId)
                        .eq(ApplicationDO::getStatus, 2)
                        .eq(ApplicationDO::getDelFlag, 0)
        ));

        // 查询待回复的申请数量 (作为接收者，status = 0)
        Integer pendingReply = Math.toIntExact(applicationMapper.selectCount(
                Wrappers.lambdaQuery(ApplicationDO.class)
                        .eq(ApplicationDO::getReceiver, studentId)
                        .eq(ApplicationDO::getStatus, 0)
                        .eq(ApplicationDO::getDelFlag, 0)
        ));

        // 查询已发送的申请数量 (作为发送者)
        Integer sent = Math.toIntExact(applicationMapper.selectCount(
                Wrappers.lambdaQuery(ApplicationDO.class)
                        .eq(ApplicationDO::getSender, studentId)
                        .eq(ApplicationDO::getDelFlag, 0)
        ));

        // 查询已删除的申请数量 (作为接收者，delFlag = 1)
        Integer deleted = Math.toIntExact(applicationMapper.selectCount(
                Wrappers.lambdaQuery(ApplicationDO.class)
                        .eq(ApplicationDO::getReceiver, studentId)
                        .eq(ApplicationDO::getDelFlag, 1)
        ));

        // 查询总通讯录数量 (该学生可访问的联系人数量)
        Integer totalContacts = Math.toIntExact(contactGotoMapper.selectCount(
                Wrappers.lambdaQuery(ContactGotoDO.class)
                        .eq(ContactGotoDO::getOwnerId, studentId)
                        .eq(ContactGotoDO::getDelFlag, 0)
        ));

        return DataStatisticDTO.builder()
                .approved(approved)
                .rejected(rejected)
                .pendingReply(pendingReply)
                .sent(sent)
                .deleted(deleted)
                .totalContacts(totalContacts)
                .build();
    }

    /**
     * 将 ContactQueryRespDTO 转换为 StuInfoDTO
     */
    private StuInfoDTO convertToStuInfoDTO(ContactQueryRespDTO source) {
        StuInfoDTO target = new StuInfoDTO();
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
