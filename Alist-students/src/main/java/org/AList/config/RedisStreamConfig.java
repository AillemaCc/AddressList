package org.AList.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.stream.consumer.StreamEventConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConfig {
    private final RedisStreamProperties streamProperties;
    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public void createConsumerGroup() {
        try {
            StringRedisTemplate redisTemplate = new StringRedisTemplate(redisConnectionFactory);
            // 检查stream是否存在，如果不存在则创建
            try {
                redisTemplate.opsForStream().info(streamProperties.getKey());
            } catch (Exception e) {
                // Stream不存在，先创建一个空的stream
                redisTemplate.opsForStream().add(streamProperties.getKey(), Collections.singletonMap("init", "true"));
            }

            // 创建消费者组
            redisTemplate.opsForStream().createGroup(streamProperties.getKey(), streamProperties.getGroup());
            log.info("成功创建Redis Stream消费者组: {}", streamProperties.getGroup());
        } catch (Exception e) {
            log.warn("创建消费者组失败或组已存在: {}", e.getMessage());
        }
    }

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamContainer(
            StreamEventConsumer streamEventConsumer) {

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(streamProperties.getPollTimeout()))
                .targetType(String.class)
                .errorHandler((exception) -> {
                    log.error("Redis Stream处理异常: ", exception);
                })
                .build();

        var container = StreamMessageListenerContainer.create(redisConnectionFactory, options);

        try {
            container.receive(
                    Consumer.from(streamProperties.getGroup(), streamProperties.getConsumerId()),
                    StreamOffset.create(streamProperties.getKey(), ReadOffset.lastConsumed()),
                    streamEventConsumer);

            container.start();
            log.info("Redis Stream容器启动成功");
        } catch (Exception e) {
            log.error("Redis Stream容器启动失败: ", e);
        }

        return container;
    }
}