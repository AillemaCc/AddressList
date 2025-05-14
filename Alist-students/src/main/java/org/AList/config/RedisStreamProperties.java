package org.AList.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.redis-stream")
@Data
public class RedisStreamProperties {
    private String key;          // Stream 键名
    private String group;        // 消费者组名
    private String consumerId;   // 消费者ID
    private long pollTimeout;     // 轮询超时时间(ms)
}