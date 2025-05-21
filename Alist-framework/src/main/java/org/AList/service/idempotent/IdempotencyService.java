package org.AList.service.idempotent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {  
    private final StringRedisTemplate redisTemplate;
    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60; // 24小时（秒）  
      
    /**  
     * 仅在幂等键不存在时执行函数  
     * @param idempotencyKey 操作的唯一键  
     * @param function 如果操作尚未执行则执行的函数  
     * @param <T> 函数返回类型  
     * @return 函数结果或已执行时返回null  
     */  
    public <T> T executeOnce(String idempotencyKey, Supplier<T> function) {
        return executeOnce(idempotencyKey, DEFAULT_EXPIRATION, function);  
    }  
      
    /**  
     * 仅在幂等键不存在时执行函数  
     * @param idempotencyKey 操作的唯一键  
     * @param expirationSeconds 键过期时间（秒）  
     * @param function 如果操作尚未执行则执行的函数  
     * @param <T> 函数返回类型  
     * @return 函数结果或已执行时返回null  
     */  
    public <T> T executeOnce(String idempotencyKey, long expirationSeconds, Supplier<T> function) {  
        Boolean isFirstExecution = redisTemplate.opsForValue().setIfAbsent(  
                "idempotency:" + idempotencyKey,   
                "1",   
                expirationSeconds,   
                TimeUnit.SECONDS
        );  
          
        if (Boolean.TRUE.equals(isFirstExecution)) {  
            try {  
                return function.get();  
            } catch (Exception e) {  
                // 如果执行失败，删除键以允许重试  
                redisTemplate.delete("idempotency:" + idempotencyKey);  
                throw e;  
            }  
        }  
        return null;  
    }  
      
    /**  
     * 仅在幂等键不存在时执行无返回值函数  
     * @param idempotencyKey 操作的唯一键  
     * @param function 如果操作尚未执行则执行的函数  
     */  
    public void executeOnceVoid(String idempotencyKey, Runnable function) {  
        executeOnceVoid(idempotencyKey, DEFAULT_EXPIRATION, function);  
    }  
      
    /**  
     * 仅在幂等键不存在时执行无返回值函数  
     * @param idempotencyKey 操作的唯一键  
     * @param expirationSeconds 键过期时间（秒）  
     * @param function 如果操作尚未执行则执行的函数  
     */  
    public void executeOnceVoid(String idempotencyKey, long expirationSeconds, Runnable function) {  
        Boolean isFirstExecution = redisTemplate.opsForValue().setIfAbsent(  
                "idempotency:" + idempotencyKey,   
                "1",   
                expirationSeconds,   
                TimeUnit.SECONDS  
        );  
          
        if (Boolean.TRUE.equals(isFirstExecution)) {  
            try {  
                function.run();  
            } catch (Exception e) {  
                // 如果执行失败，删除键以允许重试  
                redisTemplate.delete("idempotency:" + idempotencyKey);  
                throw e;  
            }  
        }  
    }

    /**
     * 手动删除幂等键
     * @param idempotencyKey 要删除的幂等键
     * @return 是否成功删除
     */
    public boolean removeIdempotencyKey(String idempotencyKey) {
        String fullKey = "idempotency:" + idempotencyKey;
        log.info("手动删除幂等键: {}", fullKey);
        Boolean result = redisTemplate.delete(fullKey);
        log.info("幂等键删除结果: {} ({})", result, fullKey);
        return Boolean.TRUE.equals(result);
    }
}