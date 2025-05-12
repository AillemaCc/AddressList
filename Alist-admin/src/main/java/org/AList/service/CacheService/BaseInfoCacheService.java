package org.AList.service.CacheService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.domain.dao.entity.ContactGotoDO;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.mapper.ContactGotoMapper;
import org.AList.domain.dao.mapper.StudentMapper;
import org.AList.domain.dto.resp.BaseClassInfoListStuRespDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 学生通讯录缓存服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BaseInfoCacheService {
    private final StringRedisTemplate stringRedisTemplate;
    private final StudentMapper studentMapper;
    private final ContactGotoMapper contactGotoMapper;


    /**
     * 班级信息变更之后，清理指定班级下所有学生的通讯录相关缓存，以保证数据一致性。
     *
     * <p>该方法的主要逻辑如下：</p>
     * <ol>
     *     <li>根据班级编号查询所有有效学生（未被逻辑删除的学生）</li>
     *     <li>对于每个学生，查询与其关联的所有 通讯录跳转记录 （即谁将其加入通讯录）</li>
     *     <li>根据这些跳转记录，构建出对应的 Redis 缓存键，并逐个删除，实现缓存清理</li>
     * </ol>
     *
     * <p>此方法通常用于在学生或通讯录信息变更时重建缓存，确保其他用户获取到的是最新数据。</p>
     *
     * @param classNum 班级编号，用于定位需要清理缓存的学生群体
     */
    public void clearStudentContactCacheByClass(Integer classNum) {
        // 1. 查询该班级下的所有学生
        LambdaQueryWrapper<StudentDO> studentQueryWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getClassName, classNum)
                .eq(StudentDO::getDelFlag, 0);

        List<StudentDO> students = studentMapper.selectList(studentQueryWrapper);
        if (CollectionUtils.isEmpty(students)) {
            return;
        }

        // 2. 为每个学生清理通讯录缓存
        students.forEach(student -> {
            // 查询所有拥有该学生通讯录的用户
            LambdaQueryWrapper<ContactGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                    .eq(ContactGotoDO::getContactId, student.getStudentId())
                    .eq(ContactGotoDO::getDelFlag, 0);

            List<ContactGotoDO> contactGotos = contactGotoMapper.selectList(gotoQueryWrapper);

            // 删除所有相关的缓存键
            contactGotos.forEach(gotoRecord -> {
                String redisKey = String.format("contact:%s:%s",
                        gotoRecord.getOwnerId(),
                        gotoRecord.getContactId());
                stringRedisTemplate.delete(redisKey);
            });

            evictFullContactCache(student.getStudentId());
        });
    }

    /**
     * 获取完整通讯信息缓存键
     */
    private String getFullContactCacheKey(String studentId) {
        return "student:fullContactInfo:" + studentId;
    }

    /**
     * 清除指定学生的完整通讯信息缓存
     * @param studentId 学生ID
     */
    public void evictFullContactCache(String studentId) {
        String cacheKey = getFullContactCacheKey(studentId);
        try {
            stringRedisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("清除完整通讯信息缓存失败，studentId: {}", studentId, e);
        }
    }

    /**
     * 从缓存中获取完整的通讯信息
     */
    public BaseClassInfoListStuRespDTO getFullContactInfoFromCache(String studentId) {
        String cacheKey = getFullContactCacheKey(studentId);
        try {
            String cachedInfo = stringRedisTemplate.opsForValue().get(cacheKey);
            return cachedInfo != null ? JSON.parseObject(cachedInfo, BaseClassInfoListStuRespDTO.class) : null;
        } catch (Exception e) {
            log.error("获取完整通讯信息缓存失败，studentId: {}", studentId, e);
            return null;
        }
    }

    /**
     * 将完整通讯信息存入缓存
     */
    public void putCacheStuFullContactInfo(String studentId, BaseClassInfoListStuRespDTO response) {
        String cacheKey = getFullContactCacheKey(studentId);
        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    JSON.toJSONString(response),
                    1,
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            log.error("缓存完整通讯信息失败，studentId: {}", studentId, e);
        }
    }


    /**
     * 构造分页缓存Key（格式：classPage:students:{classNum}:{page}:{size}）
     */
    private String getPageCacheKey(Integer classNum, Integer current, Integer size) {
        return String.format("classPage:students:%s:%d:%d",
                classNum,
                current != null ? current : 1,
                size != null ? size : 10);
    }

    /**
     * 从缓存中获取分页数据
     *
     * @param classNum 班级编号
     * @param current 当前页码
     * @param size 分页大小
     * @return 缓存中的分页数据（如果存在）
     */
    public IPage<BaseClassInfoListStuRespDTO> getPageCacheByClass(Integer classNum, Integer current, Integer size) {
        String cacheKey = getPageCacheKey(classNum, current, size);
        try {
            String cachedData = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                return JSON.parseObject(cachedData, new TypeReference<Page<BaseClassInfoListStuRespDTO>>() {});
            }
        } catch (Exception e) {
            log.error("从缓存中获取分页数据失败，cacheKey: {}", cacheKey, e);
        }
        return null;
    }

    /**
     * 将分页数据写入缓存
     *
     * @param classNum 班级编号
     * @param current 当前页码
     * @param size 分页大小
     * @param pageData 分页数据
     */
    public void putPageCacheByClass(Integer classNum, Integer current, Integer size, IPage<BaseClassInfoListStuRespDTO> pageData) {
        String cacheKey = getPageCacheKey(classNum, current, size);
        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    JSON.toJSONString(pageData),
                    30,
                    TimeUnit.MINUTES // 可根据业务需求调整过期时间
            );
        } catch (Exception e) {
            log.error("将分页数据写入缓存失败，cacheKey: {}", cacheKey, e);
        }
    }

    /**
     * 清除指定班级下的所有分页缓存
     *
     * @param classNum 班级编号
     */
    public void evictPageCacheByClass(Integer classNum) {
        // 注意：由于 Redis 的 keys 操作在大数据量下性能较差，建议使用 Lua 脚本或提前维护 key 命名规则。
        // 此处是一个简单示例，实际生产环境应避免使用 KEYS 命令。
        Set<String> keys = stringRedisTemplate.keys("classPage:students:" + classNum + ":*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }
}