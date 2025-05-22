package org.AList.config;

import org.AList.common.biz.user.AdminTransmitFilter;
import org.AList.service.AdminToken.TokenService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 注册过滤器到容器当中
 */
@Configuration
public class AdminConfiguration {
    @Bean
    public FilterRegistrationBean<AdminTransmitFilter> globalAdminTransmitFilter(StringRedisTemplate stringRedisTemplate, TokenService tokenService) {
        FilterRegistrationBean<AdminTransmitFilter> registration=new FilterRegistrationBean<>();
        registration.setFilter(new AdminTransmitFilter(stringRedisTemplate,tokenService));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }
}
