package org.AList.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class AdminCorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // 匹配你需要支持跨域的路径
                        .allowedOriginPatterns("http://localhost:5173") // 允许的源
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("New-Access-Token", "X-Refresh-Required") // 暴露给前端的自定义头
                        .allowCredentials(true)
                        .maxAge(3600); // 预检缓存时间
            }
        };
    }
}
