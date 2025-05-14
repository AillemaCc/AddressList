package org.AList.config;

import org.AList.common.limiter.LogRateLimiter;
import org.AList.common.limiter.UserLogRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {
    
    @Bean
    public LogRateLimiter logRateLimiter(
        @Value("${log.rate.normal:100}") double normalRate,
        @Value("${log.rate.burst:500}") double burstRate) {
        return new LogRateLimiter(normalRate, burstRate);
    }

    @Bean
    public UserLogRateLimiter userLogRateLimiter(
            @Value("${log.rate.normal:10}") double normalRate,
            @Value("${log.rate.burst:50}") double burstRate) {
        return new UserLogRateLimiter(normalRate, burstRate);
    }
}