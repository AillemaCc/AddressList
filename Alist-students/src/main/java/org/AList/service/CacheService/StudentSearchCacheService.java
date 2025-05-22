package org.AList.service.CacheService;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.domain.dto.req.QuerySomeoneReqDTO;
import org.AList.domain.dto.resp.QuerySomeoneRespDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentSearchCacheService {

    private final StringRedisTemplate stringRedisTemplate;
    // 移除 ObjectMapper，统一使用 FastJSON

    private static final int CACHE_TTL = 300; // 5分钟

    public String generateCacheKey(QuerySomeoneReqDTO requestParam) {
        // 使用 RedisKeyGenerator 生成缓存键
        return RedisKeyGenerator.genStudentSearchCacheKey(
                requestParam.getName(),
                requestParam.getCurrent(),
                requestParam.getSize()
        );
    }

    public IPage<QuerySomeoneRespDTO> getCachedSearchResult(String cacheKey) {
        try {
            String cachedResult = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return JSON.parseObject(cachedResult,
                        new com.alibaba.fastjson.TypeReference<Page<QuerySomeoneRespDTO>>() {});
            }
        } catch (Exception e) {
            log.error("获取搜索缓存失败，key: {}", cacheKey, e);
        }
        return null;
    }

    public void cacheSearchResult(String cacheKey, IPage<QuerySomeoneRespDTO> result) {
        try {
            // 统一使用 FastJSON 进行序列化
            String jsonResult = JSON.toJSONString(result);
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    jsonResult,
                    CACHE_TTL,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("缓存搜索结果失败，key: {}", cacheKey, e);
        }
    }
}