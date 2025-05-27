package org.AList.common.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.event.ConfigRefreshCompletedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;  
  
@Component  
@Slf4j  
public class NacosConfigRefreshListener {  
      
    @Value("${nacos.config.server-addr:127.0.0.1:8848}")  
    private String serverAddr;  
      
    @Value("${nacos.config.namespace:}")  
    private String namespace;  
      
    @Value("${nacos.config.group:DEFAULT_GROUP}")  
    private String group;  
      
    private final ApplicationContext applicationContext;  
      
    public NacosConfigRefreshListener(ApplicationContext applicationContext) {  
        this.applicationContext = applicationContext;  
    }  
      
    @PostConstruct  
    public void init() {  
        try {  
            Properties properties = new Properties();  
            properties.put("serverAddr", serverAddr);  
            properties.put("namespace", namespace);  
              
            ConfigService configService = NacosFactory.createConfigService(properties);  
              
            // 监听公共配置变化  
            configService.addListener("alist-common.yml", group, new Listener() {  
                @Override  
                public void receiveConfigInfo(String configInfo) {  
                    log.info("检测到公共配置变更，正在刷新...");  
                    refreshConfig("nacos-common-refresh", configInfo);  
                }  
                  
                @Override  
                public Executor getExecutor() {  
                    return null;  
                }  
            });  
              
            // 监听应用特定配置变化  
            String appName = applicationContext.getEnvironment().getProperty("spring.application.name");  
            if (appName != null) {  
                configService.addListener(appName + ".yml", group, new Listener() {  
                    @Override  
                    public void receiveConfigInfo(String configInfo) {  
                        log.info("检测到应用配置变更，正在刷新...");  
                        refreshConfig("nacos-app-refresh", configInfo);  
                    }  
                      
                    @Override  
                    public Executor getExecutor() {  
                        return null;  
                    }  
                });  
            }  
              
        } catch (Exception e) {  
            log.error("Failed to initialize Nacos config listener", e);  
        }  
    }  
      
    private void refreshConfig(String sourceName, String yamlContent) {  
        try {  
            ConfigurableEnvironment environment =   
                ((ConfigurableApplicationContext) applicationContext).getEnvironment();  
              
            Yaml yaml = new Yaml();  
            Map<String, Object> yamlMap = yaml.load(yamlContent);  
              
            Properties properties = new Properties();  
            flattenMap("", yamlMap, properties);  
              
            PropertiesPropertySource propertySource =   
                new PropertiesPropertySource(sourceName, properties);  
            environment.getPropertySources().addFirst(propertySource);  
              
            log.info("配置刷新成功: {}", sourceName);
            // 发布配置刷新完成事件
            applicationContext.publishEvent(new ConfigRefreshCompletedEvent(sourceName, yamlMap));
        } catch (Exception e) {  
            log.error("配置刷新失败: {}", sourceName, e);  
        }  
    }  
      
    private void flattenMap(String prefix, Map<String, Object> map, Properties properties) {  
        for (Map.Entry<String, Object> entry : map.entrySet()) {  
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();  
            Object value = entry.getValue();  
              
            if (value instanceof Map) {  
                flattenMap(key, (Map<String, Object>) value, properties);  
            } else if (value != null) {  
                properties.setProperty(key, String.valueOf(value));  
            }  
        }  
    }
}