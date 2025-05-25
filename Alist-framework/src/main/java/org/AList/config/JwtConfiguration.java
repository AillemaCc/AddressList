package org.AList.config;

import lombok.extern.slf4j.Slf4j;
import org.AList.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class JwtConfiguration {  
      
    @Bean
    public JwtUtils jwtUtils(JwtProperties jwtProperties) {
        String secret = jwtProperties.getSecret();
        log.info("加载JWT密钥: {}", secret != null ? secret.substring(0, Math.min(10, secret.length())) + "..." : "null");
        log.info("JWT密钥长度: {}", secret != null ? secret.length() : 0);
        return new JwtUtils(secret);
    }  
}