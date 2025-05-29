package org.AList.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * 抽象出配置变更事件
 */
@Getter
public class ConfigRefreshCompletedEvent extends ApplicationEvent {
    private final String sourceName;  
    private final Map<String, Object> configMap;
      
    public ConfigRefreshCompletedEvent(String sourceName, Map<String, Object> configMap) {  
        super(sourceName);  
        this.sourceName = sourceName;  
        this.configMap = configMap;  
    }

}