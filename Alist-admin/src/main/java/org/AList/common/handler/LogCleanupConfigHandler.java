package org.AList.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.AList.common.event.ConfigRefreshCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class LogCleanupConfigHandler {  
      
    @EventListener
    public void handleConfigRefresh(ConfigRefreshCompletedEvent event) {
        // 只处理应用配置变更  
        if (!"nacos-app-refresh".equals(event.getSourceName())) {  
            return;  
        }  
          
        Map<String, Object> configMap = event.getConfigMap();
        Map<String, Object> adminConfig = (Map<String, Object>) configMap.get("admin");  
          
        if (adminConfig != null) {  
            Map<String, Object> logConfig = (Map<String, Object>) adminConfig.get("log");  
            if (logConfig != null) {  
                Map<String, Object> cleanupConfig = (Map<String, Object>) logConfig.get("cleanup");  
                if (cleanupConfig != null) {  
                    log.info("检测到日志清理配置变更: {}", cleanupConfig);  
                    // 这里可以添加特定的处理逻辑，比如重新调度定时任务等  
                }  
            }  
        }  
    }  
}