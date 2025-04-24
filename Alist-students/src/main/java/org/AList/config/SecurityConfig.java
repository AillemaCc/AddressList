package org.AList.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
           .authorizeRequests()
               .antMatchers("/api/test/list").permitAll() // 允许 /api/test/list 接口匿名访问
               .anyRequest().authenticated()
               .and()
           .formLogin()
               .and()
           .httpBasic();
        return http.build();
    }
}
