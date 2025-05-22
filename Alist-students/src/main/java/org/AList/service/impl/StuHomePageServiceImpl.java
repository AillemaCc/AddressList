package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ServiceException;
import org.AList.domain.dao.entity.ClassInfoDO;
import org.AList.domain.dao.entity.ContactDO;
import org.AList.domain.dao.entity.MajorAndAcademyDO;
import org.AList.domain.dao.entity.StudentFrameworkDO;
import org.AList.domain.dao.mapper.ClassInfoMapper;
import org.AList.domain.dao.mapper.ContactMapper;
import org.AList.domain.dao.mapper.MajorAndAcademyMapper;
import org.AList.domain.dao.mapper.StudentFrameWorkMapper;
import org.AList.domain.dto.resp.ContactQueryRespDTO;
import org.AList.domain.dto.resp.HomePageQueryRespDTO;
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

    @Override
    public HomePageQueryRespDTO queryHomepageInfo(String studentId) {

        // 1. 尝试从缓存获取
        ContactQueryRespDTO cachedData = contactCacheService.getContactCache(studentId, studentId);

        if (cachedData != null) {
            // 将 ContactQueryRespDTO 转换为 HomePageQueryRespDTO
            return convertToHomePageDTO(cachedData);
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

        // 3. 构建响应对象
        HomePageQueryRespDTO response = new HomePageQueryRespDTO();
        response.setStudentId(studentId);
        response.setName(student.getName());
        response.setAcademy(academyName);
        response.setMajor(majorName);
        response.setClassName(className);
        response.setEnrollmentYear(student.getEnrollmentYear());
        response.setGraduationYear(student.getGraduationYear());
        response.setEmployer(contact.getEmployer());
        response.setCity(contact.getCity());

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

        return response;
    }

    /**
     * 将 ContactQueryRespDTO 转换为 HomePageQueryRespDTO
     */
    private HomePageQueryRespDTO convertToHomePageDTO(ContactQueryRespDTO source) {
        HomePageQueryRespDTO target = new HomePageQueryRespDTO();
        // 复制相同属性
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
