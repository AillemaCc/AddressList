package org.AList.config;

import lombok.RequiredArgsConstructor;
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

@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {
    private final RedisStreamProperties streamProperties;
    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public void createConsumerGroup() {
        try {
            new StringRedisTemplate(redisConnectionFactory)
                    .opsForStream()
                    .createGroup(streamProperties.getKey(), streamProperties.getGroup());
        } catch (Exception e) {
            // 组已存在时忽略报错
        }
    }

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamContainer(
            StreamEventConsumer streamEventConsumer) {

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(streamProperties.getPollTimeout()))
                .targetType(String.class)
                .build();

        var container = StreamMessageListenerContainer.create(redisConnectionFactory, options);

        container.receive(
                Consumer.from(streamProperties.getGroup(), streamProperties.getConsumerId()),
                StreamOffset.create(streamProperties.getKey(), ReadOffset.lastConsumed()),
                streamEventConsumer);

        container.start();
        return container;
    }
}