package org.AList.common.limiter;

import java.util.concurrent.ConcurrentHashMap;

public class UserLogRateLimiter {
    private final ConcurrentHashMap<String, LogRateLimiter> userLimiters = new ConcurrentHashMap<>();
    private final double normalRate;
    private final double burstRate;

    public UserLogRateLimiter(double normalRate, double burstRate) {
        this.normalRate = normalRate;
        this.burstRate = burstRate;
    }

    public boolean tryAcquire(String userId) {
        // 为每个用户创建独立的限流器（懒加载）
        LogRateLimiter userLimiter = userLimiters.computeIfAbsent(
            userId, k -> new LogRateLimiter(normalRate, burstRate)
        );
        return userLimiter.tryAcquire();
    }
}