package org.AList.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */
@Configuration(value = "rBloomFilterConfiguration")
public class RBloomFilterConfiguration {
    /**
     * 用户注册到不存在默认数据库的学号的情况下，提供快速失败的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> studentIdBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("studentIdBloomFilter");
        bloomFilter.tryInit(500000L,0.001);
        return bloomFilter;
    }
}
