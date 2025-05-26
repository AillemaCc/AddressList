package org.AList.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisStreamHealthCheck {  
      
    @Autowired
    private StringRedisTemplate redisTemplate;
      
    @Autowired  
    private RedisStreamProperties streamProperties;  
      
    @EventListener(ApplicationReadyEvent.class)
    public void checkStreamHealth() {  
        try {  
            // 检查stream是否存在  
            redisTemplate.opsForStream().info(streamProperties.getKey());  
            log.info("Redis Stream健康检查通过: {}", streamProperties.getKey());  
        } catch (Exception e) {  
            log.error("Redis Stream健康检查失败: ", e);  
            // 可以触发告警或自动修复  
        }  
    }  
}