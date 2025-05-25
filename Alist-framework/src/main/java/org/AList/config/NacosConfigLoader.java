package org.AList.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;  
import lombok.extern.slf4j.Slf4j;  
import org.springframework.context.ApplicationContextInitializer;  
import org.springframework.context.ConfigurableApplicationContext;  
import org.springframework.core.env.ConfigurableEnvironment;  
import org.springframework.core.env.PropertiesPropertySource;  
import org.yaml.snakeyaml.Yaml;  
  
import java.util.Map;  
import java.util.Properties;  
  
@Slf4j  
public class NacosConfigLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {  
      
    @Override  
    public void initialize(ConfigurableApplicationContext applicationContext) {  
        try {  
            ConfigurableEnvironment environment = applicationContext.getEnvironment();  
              
            // 从application.yml读取Nacos配置  
            String serverAddr = environment.getProperty("nacos.config.server-addr", "127.0.0.1:8848");  
            String namespace = environment.getProperty("nacos.config.namespace", "");  
            String group = environment.getProperty("nacos.config.group", "DEFAULT_GROUP");  
              
            // 创建Nacos配置服务  
            Properties properties = new Properties();  
            properties.put("serverAddr", serverAddr);  
            properties.put("namespace", namespace);  
              
            ConfigService configService = NacosFactory.createConfigService(properties);  
              
            // 加载公共配置  
            String commonConfig = configService.getConfig("alist-common.yml", group, 5000);  
            if (commonConfig != null) {  
                addPropertySource(environment, "nacos-common", commonConfig);  
                log.info("成功加载Nacos公共配置: alist-common.yml");  
            }  
              
            // 加载应用特定配置  
            String appName = environment.getProperty("spring.application.name");  
            if (appName != null) {  
                String appConfig = configService.getConfig(appName + ".yml", group, 5000);  
                if (appConfig != null) {  
                    addPropertySource(environment, "nacos-app", appConfig);  
                    log.info("成功加载Nacos应用配置: {}.yml", appName);  
                }  
            }  
              
        } catch (Exception e) {  
            log.error("Failed to load Nacos configuration", e);  
            // 不抛出异常，允许应用使用本地配置启动  
        }  
    }  
      
    private void addPropertySource(ConfigurableEnvironment environment, String name, String yamlContent) {  
        try {  
            Yaml yaml = new Yaml();  
            Map<String, Object> yamlMap = yaml.load(yamlContent);  
              
            Properties properties = new Properties();  
            flattenMap("", yamlMap, properties);  
              
            PropertiesPropertySource propertySource = new PropertiesPropertySource(name, properties);  
            environment.getPropertySources().addFirst(propertySource);  
              
        } catch (Exception e) {  
            log.error("Failed to parse YAML configuration: {}", name, e);  
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