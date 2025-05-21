package org.AList.common.cleaner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistCleaner {  
    private final StringRedisTemplate stringRedisTemplate;
      
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanExpiredBlacklistTokens() {  
        Set<String> keys = stringRedisTemplate.keys("token:blacklist:*");
        if (keys != null && !keys.isEmpty()) {  
            // Redis会自动清理过期键，这里只是记录日志  
            log.info("当前Token黑名单数量: {}", keys.size());  
        }  
    }  
}