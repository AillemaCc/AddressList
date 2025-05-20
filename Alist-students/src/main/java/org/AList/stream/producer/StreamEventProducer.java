package org.AList.stream.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.AList.config.RedisStreamProperties;
import org.AList.stream.event.StreamEvent;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StreamEventProducer {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisStreamProperties streamProperties;

    /**
     * 发送通用事件
     * @param event 事件对象
     * @throws JsonProcessingException 序列化异常
     */
    public void sendEvent(StreamEvent event) throws JsonProcessingException {
        redisTemplate.opsForStream().add(
                StreamRecords.newRecord()
                        .ofObject(objectMapper.writeValueAsString(event))
                        .withStreamKey(streamProperties.getKey())
        );
    }

    /**
     * 发送缓存重建事件
     * @param studentId 学生ID
     * @param ownerIds 拥有者ID列表
     * @throws JsonProcessingException 序列化异常
     */
    public void sendRebuildEvent(String studentId, List<String> ownerIds) throws JsonProcessingException {
        StreamEvent event = StreamEvent.builder()
                .eventType("CACHE_REBUILD")
                .studentId(studentId)
                .ownerIdsJson(objectMapper.writeValueAsString(ownerIds))
                .timestamp(System.currentTimeMillis())
                .build();
        sendEvent(event);
    }

    /**
     * 发送缓存清除事件
     *
     * @param studentId 学生ID
     * @throws JsonProcessingException 序列化异常
     */
    public void sendClearEvent(String studentId) throws JsonProcessingException {
        StreamEvent event = StreamEvent.builder()
                .eventType("CACHE_CLEAR")
                .studentId(studentId)
                .timestamp(System.currentTimeMillis())
                .build();
        sendEvent(event);
    }
}