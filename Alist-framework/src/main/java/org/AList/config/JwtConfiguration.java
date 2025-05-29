package org.AList.config;

import lombok.extern.slf4j.Slf4j;
import org.AList.common.monitor.JwtKeyRotationMonitor;
import org.AList.common.properties.JwtProperties;
import org.AList.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class JwtConfiguration {

    @Bean
    public JwtUtils jwtUtils(JwtProperties jwtProperties, JwtKeyRotationMonitor jwtKeyRotationMonitor) { // 注入 JwtKeyRotationMonitor
        String secret = jwtProperties.getSecret();
        log.info("加载JWT密钥: {}", secret != null ? secret.substring(0, Math.min(10, secret.length())) + "..." : "null");
        log.info("JWT密钥长度: {}, 历史密钥数量: {}",
                secret != null ? secret.length() : 0,
                jwtProperties.getHistoricalSecrets().size());

        return new JwtUtils(secret, jwtProperties.getHistoricalSecrets(), jwtProperties.getKeyRotationGracePeriod(), jwtKeyRotationMonitor); // 传递 monitor
    }
}