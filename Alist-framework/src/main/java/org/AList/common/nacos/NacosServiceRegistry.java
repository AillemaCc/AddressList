package org.AList.common.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.Properties;

@Component
@Slf4j
public class NacosServiceRegistry implements ApplicationListener<ContextRefreshedEvent> {
      
    @Value("${nacos.discovery.server-addr:127.0.0.1:8848}")
    private String serverAddr;  
      
    @Value("${nacos.discovery.namespace:}")  
    private String namespace;  
      
    @Value("${spring.application.name}")  
    private String serviceName;  
      
    @Value("${server.port}")  
    private int port;  
      
    private NamingService namingService;
      
    @PostConstruct
    public void init() throws Exception {  
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);  
        if (StringUtils.isNotBlank(namespace)) {
            properties.put("namespace", namespace);  
        }  
        namingService = NacosFactory.createNamingService(properties);
    }  
      
    @Override  
    public void onApplicationEvent(ContextRefreshedEvent event) {  
        try {  
            // 创建服务实例  
            Instance instance = new Instance();
            instance.setIp(InetAddress.getLocalHost().getHostAddress());
            instance.setPort(port);  
            instance.setHealthy(true);  
            instance.setWeight(1.0);  
              
            // 注册服务实例  
            namingService.registerInstance(serviceName, instance);  
            log.info("Service {} 已成功注册到 Nacos", serviceName);
              
        } catch (Exception e) {  
            log.error("向 Nacos 注册服务失败", e);
        }  
    }  
      
    @PreDestroy
    public void destroy() {  
        try {  
            if (namingService != null) {  
                namingService.deregisterInstance(serviceName,   
                    InetAddress.getLocalHost().getHostAddress(), port);  
                log.info("已从 Nacos 注销的 Service {}", serviceName);
            }  
        } catch (Exception e) {  
            log.error("取消注册服务失败", e);
        }  
    }  
}