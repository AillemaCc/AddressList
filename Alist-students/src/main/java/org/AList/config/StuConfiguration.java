package org.AList.config;

import org.AList.common.biz.user.StuTransmitFilter;
import org.AList.service.StuToken.TokenService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 过滤器配置类 把过滤器注册为Bean
 */
@Configuration
public class StuConfiguration {
    @Bean
    public FilterRegistrationBean<StuTransmitFilter> globalStudentTransmitFilter(StringRedisTemplate stringRedisTemplate, TokenService tokenService) {
        FilterRegistrationBean<StuTransmitFilter> registration=new FilterRegistrationBean<>();
        registration.setFilter(new StuTransmitFilter(stringRedisTemplate,tokenService));
        registration.addUrlPatterns("/*");
        registration.addInitParameter("Login","/api/stu/login");
        registration.setOrder(0);
        return registration;
    }
}
