package org.AList.common.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;  
  
@Component  
@Slf4j  
public class JwtKeyRotationMonitor {  
      
    @Value("${security.jwt.rotation.alert-threshold:100}")  
    private Long alertThreshold;  
      
    private final AtomicLong historicalKeyUsageCount = new AtomicLong(0);  
    private final AtomicLong totalTokenValidations = new AtomicLong(0);  
      
    /**  
     * 记录历史密钥使用情况  
     */  
    public void recordHistoricalKeyUsage() {  
        long count = historicalKeyUsageCount.incrementAndGet();  
        long total = totalTokenValidations.incrementAndGet();  
          
        // 如果历史密钥使用率超过阈值，发出告警  
        if (count > alertThreshold && count * 100 / total > 10) {  
            log.warn("历史密钥使用率过高: {}/{} ({}%), 建议检查客户端Token刷新机制",   
                    count, total, count * 100 / total);  
        }  
    }  
      
    /**  
     * 记录当前密钥使用情况  
     */  
    public void recordCurrentKeyUsage() {  
        totalTokenValidations.incrementAndGet();  
    }  
      
    /**  
     * 重置统计计数器  
     */  
    public void resetCounters() {  
        historicalKeyUsageCount.set(0);  
        totalTokenValidations.set(0);  
        log.info("密钥使用统计计数器已重置");  
    }  
      
    /**  
     * 获取使用统计  
     */  
    public KeyUsageStats getUsageStats() {  
        long historical = historicalKeyUsageCount.get();  
        long total = totalTokenValidations.get();  
        double percentage = total > 0 ? (double) historical * 100 / total : 0;  
          
        return new KeyUsageStats(historical, total, percentage);  
    }  
      
    public static class KeyUsageStats {  
        private final long historicalKeyUsage;  
        private final long totalValidations;  
        private final double historicalKeyPercentage;  
          
        public KeyUsageStats(long historicalKeyUsage, long totalValidations, double historicalKeyPercentage) {  
            this.historicalKeyUsage = historicalKeyUsage;  
            this.totalValidations = totalValidations;  
            this.historicalKeyPercentage = historicalKeyPercentage;  
        }  
          
        // Getters...  
        public long getHistoricalKeyUsage() { return historicalKeyUsage; }  
        public long getTotalValidations() { return totalValidations; }  
        public double getHistoricalKeyPercentage() { return historicalKeyPercentage; }  
    }  
}