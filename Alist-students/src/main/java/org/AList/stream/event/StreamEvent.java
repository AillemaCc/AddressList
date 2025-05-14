package org.AList.stream.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Redis Stream消息体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamEvent {
    // 事件类型（CACHE_REBUILD/OTHER）
    private String eventType; 
    
    // 目标学生ID
    private String studentId;
    
    // 关联所有者ID列表（JSON数组格式）
    private String ownerIdsJson;
    
    // 时间戳
    private long timestamp;
}