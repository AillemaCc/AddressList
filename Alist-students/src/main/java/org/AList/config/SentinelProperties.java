package org.AList.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.sentinel")
@Data
@Component
public class SentinelProperties {  
      
    private boolean enabled = true;  
      
    private FlowControl flowControl = new FlowControl();  
      
    private CircuitBreaker circuitBreaker = new CircuitBreaker();

    private SystemProtection systemProtection = new SystemProtection();
      
    @Data  
    public static class FlowControl {  
        private int systemQps = 1000;  
        private int userQps = 50;  
        private int paramQps = 10;  
    }  
      
    @Data  
    public static class CircuitBreaker {  
        private double errorRatio = 0.5;  
        private double slowCallRatio = 0.6;  
        private int timeWindow = 10;  
    }

    @Data
    public static class SystemProtection {
        private double highestCpuUsage = 0.8;
        private double highestSystemLoad = 10.0;
    }


}