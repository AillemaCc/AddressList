package org.AList.service.CacheService;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.domain.dao.entity.ClassInfoDO;
import org.AList.domain.dao.entity.ContactDO;
import org.AList.domain.dao.entity.MajorAndAcademyDO;
import org.AList.domain.dao.entity.StudentFrameworkDO;
import org.AList.domain.dao.mapper.*;
import org.AList.domain.dto.resp.ContactQueryRespDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 联系人缓存服务类。
 *
 * <p>提供联系人信息的缓存构建、更新、删除等功能，确保数据一致性。</p>
 *
 * <p>主要职责：</p>
 * <ul>
 *     <li>构建联系人缓存</li>
 *     <li>重建联系人缓存</li>
 *     <li>清除指定学生的相关缓存</li>
 *     <li>获取联系人缓存</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContactCacheService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ContactMapper contactMapper;
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final MajorAndAcademyMapper majorAndAcademyMapper;
    private final ClassInfoMapper classInfoMapper;
    private final ContactGotoMapper contactGotoMapper;

    /**
     * 重建指定学生的通讯录缓存。
     *
     * <p>该方法会先查询所有拥有该学生通讯录的用户ID（ownerIds），然后调用实际的缓存重建逻辑。</p>
     *
     * @param studentId 学生唯一标识
     */
    public void rebuildContactCache(String studentId) {
        List<String> ownerIds = contactGotoMapper.selectOwnerIdsByContactId(studentId);
        doRebuildCache(studentId, ownerIds);
    }

    /**
     * 实际执行缓存重建逻辑。
     *
     * <p>步骤如下：</p>
     * <ol>
     *     <li>清除旧缓存</li>
     *     <li>构建完整的联系人响应数据</li>
     *     <li>为每个拥有者设置新缓存</li>
     * </ol>
     *
     * @param studentId 学生唯一标识
     * @param ownerIds 拥有该联系人的用户ID列表
     */
    public void doRebuildCache(String studentId, List<String> ownerIds) {
        clearContactCache(studentId);
        ContactQueryRespDTO response = buildContactResponse(studentId);
        if (response == null) {
            return;
        }
        setContactCacheForOwners(studentId, ownerIds, response);
    }

    /**
     * 清除指定学生的所有相关缓存。
     *
     * <p>匹配格式为 "contact:*:{studentId}" 的 Redis 缓存键并删除。</p>
     *
     * @param studentId 学生唯一标识
     */
    public void clearContactCache(String studentId) {
        String patternKey = RedisKeyGenerator.genContactPatternForStudent(studentId);
        Set<String> keys = redisTemplate.keys(patternKey);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 构建联系人完整响应数据对象。
     *
     * <p>从数据库中查询联系人基础信息、学生信息、专业学院信息，并组装成 {@link ContactQueryRespDTO}。</p>
     *
     * @param studentId 学生唯一标识
     * @return 构建完成的响应对象，若查询失败则返回 null
     */
    private ContactQueryRespDTO buildContactResponse(String studentId) {
        ContactDO contact = contactMapper.selectOne(Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, studentId)
                .eq(ContactDO::getDelFlag, 0));
        if (contact == null) {
            log.warn("联系人不存在或已被删除，studentId: {}", studentId);
            return null;
        }
        StudentFrameworkDO student = studentFrameWorkMapper.selectOne(Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, studentId)
                .eq(StudentFrameworkDO::getDelFlag, 0));
        if (student == null) {
            log.warn("学生信息不存在，studentId: {}", studentId);
            return null;
        }
        MajorAndAcademyDO majorAndAcademy = majorAndAcademyMapper.selectOne(
                Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                        .eq(MajorAndAcademyDO::getMajorNum, student.getClassNum())
                        .eq(MajorAndAcademyDO::getDelFlag, 0));
        ClassInfoDO classInfo = classInfoMapper.selectOne(
                Wrappers.lambdaQuery(ClassInfoDO.class)
                        .eq(ClassInfoDO::getClassNum, student.getClassNum())
                        .eq(ClassInfoDO::getDelFlag, 0));
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
     * 为多个拥有者设置联系人缓存。
     *
     * @param studentId 学生唯一标识
     * @param ownerIds 拥有该联系人的用户ID列表
     * @param response 联系人响应数据
     */
    private void setContactCacheForOwners(String studentId, List<String> ownerIds, ContactQueryRespDTO response) {
        for (String ownerId : ownerIds) {
            setContactCache(ownerId, studentId, response);
        }
    }

    /**
     * 为单个拥有者设置联系人缓存。
     *
     * @param ownerId 用户唯一标识
     * @param studentId 学生唯一标识
     * @param response 联系人响应数据
     */
    private void setContactCache(String ownerId, String studentId, ContactQueryRespDTO response) {
        String redisKey = RedisKeyGenerator.genContactKey(ownerId, studentId);
        try {
            String jsonResponse = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(redisKey, jsonResponse, 1, TimeUnit.HOURS);
            log.debug("Successfully set cache for owner: {}, student: {}", ownerId, studentId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize contact data for caching, owner: {}, student: {}",
                    ownerId, studentId, e);
        }
    }

    /**
     * 获取某个拥有者的联系人缓存。
     *
     * @param ownerId 拥有者用户ID
     * @param studentId 学生唯一标识
     * @return 反序列化后的联系人响应数据，若缓存不存在或反序列化失败则返回 null
     */
    public ContactQueryRespDTO getContactCache(String ownerId, String studentId) {
        String redisKey = RedisKeyGenerator.genContactKey(ownerId, studentId);
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