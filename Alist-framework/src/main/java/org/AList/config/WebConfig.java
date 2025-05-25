package org.AList.config;

import lombok.RequiredArgsConstructor;
import org.AList.interceptor.JwtRefreshInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;  
  
@Configuration  
@RequiredArgsConstructor  
public class WebConfig implements WebMvcConfigurer {  
      
    private final JwtRefreshInterceptor jwtRefreshInterceptor;
      
    @Override  
    public void addInterceptors(InterceptorRegistry registry) {  
        registry.addInterceptor(jwtRefreshInterceptor)  
                .addPathPatterns("/api/**")  
                .excludePathPatterns("/api/*/login", "/api/*/register");  
    }  
}