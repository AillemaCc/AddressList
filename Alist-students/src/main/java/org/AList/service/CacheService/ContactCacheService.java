package org.AList.service.CacheService;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.domain.dao.entity.ClassInfoDO;
import org.AList.domain.dao.entity.ContactDO;
import org.AList.domain.dao.entity.MajorAndAcademyDO;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.mapper.*;
import org.AList.domain.dto.resp.ContactQueryRespDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactCacheService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ContactMapper contactMapper;
    private final StudentMapper studentMapper;
    private final MajorAndAcademyMapper majorAndAcademyMapper;
    private final ClassInfoMapper classInfoMapper;
    private final ContactGotoMapper contactGotoMapper;

    /**
     * 重建指定学生的缓存
     * @param studentId 学生ID
     */
    public void rebuildContactCache(String studentId) {
        // 1. 获取所有拥有该学生通讯录的ownerIds
        List<String> ownerIds = contactGotoMapper.selectOwnerIdsByContactId(studentId);

        // 2. 执行实际的重建逻辑
        doRebuildCache(studentId, ownerIds);
    }

    /**
     * 实际执行缓存重建逻辑
     * @param studentId 学生ID
     * @param ownerIds 拥有该学生通讯录的所有者ID列表
     */
    public void doRebuildCache(String studentId, List<String> ownerIds) {
        // 1. 清除旧缓存
        clearContactCache(studentId);

        // 2. 构建完整的联系人响应数据
        ContactQueryRespDTO response = buildContactResponse(studentId);
        if (response == null) {
            return;
        }

        // 3. 为每个owner设置新缓存
        setContactCacheForOwners(studentId, ownerIds, response);
    }

    /**
     * 清除指定学生的所有缓存
     */
    public void clearContactCache(String studentId) {
        String patternKey = String.format("contact:*:%s", studentId);
        Set<String> keys = redisTemplate.keys(patternKey);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 构建联系人完整响应数据
     */
    private ContactQueryRespDTO buildContactResponse(String studentId) {
        // 1. 查询联系人基本信息
        ContactDO contact = contactMapper.selectOne(Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, studentId)
                .eq(ContactDO::getDelFlag, 0));

        if (contact == null) {
            log.warn("联系人不存在或已被删除，studentId: {}", studentId);
            return null;
        }

        // 2. 查询学生基本信息
        StudentDO student = studentMapper.selectOne(Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getStudentId, studentId)
                .eq(StudentDO::getDelFlag, 0));

        if (student == null) {
            log.warn("学生信息不存在，studentId: {}", studentId);
            return null;
        }

        // 3. 查询关联信息（专业、学院等）
        MajorAndAcademyDO majorAndAcademy = majorAndAcademyMapper.selectOne(
                Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                        .eq(MajorAndAcademyDO::getMajorNum, student.getMajor())
                        .eq(MajorAndAcademyDO::getDelFlag, 0));

        ClassInfoDO classInfo = classInfoMapper.selectOne(
                Wrappers.lambdaQuery(ClassInfoDO.class)
                        .eq(ClassInfoDO::getClassNum, student.getClassName())
                        .eq(ClassInfoDO::getDelFlag, 0));

        // 4. 构建完整响应DTO
        return ContactQueryRespDTO.builder()
                .studentId(studentId)
                .name(student.getName())
                .academy(majorAndAcademy != null ? majorAndAcademy.getAcademy() : null)
                .major(majorAndAcademy != null ? majorAndAcademy.getMajor() : null)
                .className(classInfo != null ? classInfo.getClassName() : null)
                .enrollmentYear(student.getEnrollmentYear())
                .graduationYear(student.getGraduationYear())
                .employer(contact.getEmployer())
                .city(contact.getCity())
                .phone(student.getPhone())
                .email(student.getEmail())
                .build();
    }

    /**
     * 为多个owner设置联系人缓存
     */
    private void setContactCacheForOwners(String studentId, List<String> ownerIds, ContactQueryRespDTO response) {
        for (String ownerId : ownerIds) {
            setContactCache(ownerId, studentId, response);
        }
    }

    /**
     * 设置单个owner的联系人缓存
     */
    private void setContactCache(String ownerId, String studentId, ContactQueryRespDTO response) {
        String redisKey = String.format("contact:%s:%s", ownerId, studentId);
        try {
            String jsonResponse = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(
                    redisKey,
                    jsonResponse,
                    1, // 1小时过期
                    TimeUnit.HOURS
            );
            log.debug("Successfully set cache for owner: {}, student: {}", ownerId, studentId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize contact data for caching, owner: {}, student: {}",
                    ownerId, studentId, e);
        }
    }

    /**
     * 获取联系人缓存（供查询使用）
     */
    public ContactQueryRespDTO getContactCache(String ownerId, String studentId) {
        String redisKey = String.format("contact:%s:%s", ownerId, studentId);
        try {
            String cachedData = redisTemplate.opsForValue().get(redisKey);
            if (cachedData != null) {
                return objectMapper.readValue(cachedData, ContactQueryRespDTO.class);
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize cached contact data, owner: {}, student: {}",
                    ownerId, studentId, e);
        }
        return null;
    }
}