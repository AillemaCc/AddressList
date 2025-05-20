package org.AList.stream.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.service.CacheService.ContactCacheService;
import org.AList.stream.event.StreamEvent;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamEventConsumer implements StreamListener<String, ObjectRecord<String, String>> {
    private final ObjectMapper objectMapper;
    private final ContactCacheService cacheService;

    @Override
    public void onMessage(ObjectRecord<String, String> record) {
        try {
            StreamEvent event = objectMapper.readValue(record.getValue(), StreamEvent.class);
            
            if ("CACHE_REBUILD".equals(event.getEventType())) {
                List<String> ownerIds = objectMapper.readValue(
                    event.getOwnerIdsJson(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
                );
                cacheService.rebuildContactCache(event.getStudentId(),ownerIds);
                log.info("Processed cache rebuild for student: {}", event.getStudentId());
            }
            // todo 没有实现缓存清除
        } catch (Exception e) {
            log.error("处理消息失败: {}", record.getValue(), e);
            // 宝塔环境下可添加告警通知
        }
    }
}