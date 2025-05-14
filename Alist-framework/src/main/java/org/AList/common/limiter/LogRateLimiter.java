package org.AList.common.limiter;

import com.google.common.util.concurrent.RateLimiter;

public class LogRateLimiter {
    // 正常情况下的限流
    private RateLimiter normalLimiter;
    // 突发情况下的限流
    private RateLimiter burstLimiter;

    public LogRateLimiter(double normalRate, double burstRate) {
        this.normalLimiter = RateLimiter.create(normalRate);
        this.burstLimiter = RateLimiter.create(burstRate);
    }
    
    public boolean tryAcquire() {
        if (normalLimiter.tryAcquire()) {
            return true;
        }
        // 正常限流不通过时，尝试突发限流
        return burstLimiter.tryAcquire();
    }
}