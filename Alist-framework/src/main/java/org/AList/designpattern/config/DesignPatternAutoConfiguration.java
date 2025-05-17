package org.AList.designpattern.config;

import org.AList.common.base.ApplicationBaseAutoConfiguration;
import org.AList.designpattern.chain.AbstractChainContext;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 设计模式自动装配
 */
@Configuration
@ImportAutoConfiguration(ApplicationBaseAutoConfiguration.class)
public class DesignPatternAutoConfiguration {

    /**
     * 责任链上下文
     */
    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }
}