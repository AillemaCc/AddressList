package org.AList.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.AList.common.event.ConfigRefreshCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class EncryptConfigHandler {

    @EventListener
    public void handleConfigRefresh(ConfigRefreshCompletedEvent event){
        // 只处理应用配置变更
        if (!"nacos-app-refresh".equals(event.getSourceName())) {
            return;
        }
        Map<String, Object> configMap = event.getConfigMap();
        Map<String, Object> encryptConfig = (Map<String, Object>) configMap.get("encrypt");

        if (encryptConfig != null) {
            Map<String,Object> prefixConfig = (Map<String, Object>) encryptConfig.get("prefix");
            if (prefixConfig != null) {
                log.info("检测到前缀变更(此操作不建议进行):{}",prefixConfig);
            }
            Map<String,Object> keyConfig = (Map<String, Object>) encryptConfig.get("key");
            if (keyConfig != null) {
                log.info("检测到加密key变更:{}",keyConfig);
            }
        }
    }
}
