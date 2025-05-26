package org.AList.service.operLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.properties.LogCleanupProperties;
import org.AList.domain.dao.mapper.OperLogMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogCleanupService {  
      
    private final OperLogMapper operLogMapper;
    private final LogCleanupProperties properties;
      
    /**  
     * 执行日志清理  
     */  
    public void cleanupLogs() {  
        if (!properties.isEnabled()) {  
            log.info("日志清理功能未启用");  
            return;  
        }  
          
        Date cutoffTime = new Date(System.currentTimeMillis() -
            properties.getRetentionDays() * 24 * 60 * 60 * 1000L);  
          
        // 统计待删除的日志数量  
        long totalCount = operLogMapper.countLogsBefore(cutoffTime);  
          
        if (totalCount == 0) {  
            log.info("没有需要清理的日志记录");  
            return;  
        }  
          
        log.info("开始清理 {} 天前的日志，预计删除 {} 条记录",   
            properties.getRetentionDays(), totalCount);  
          
        if (properties.isDryRun()) {  
            log.info("测试模式：将删除 {} 条日志记录（实际未删除）", totalCount);  
            return;  
        }  
          
        // 批量删除  
        long deletedCount = 0;  
        int batchDeletedCount;  
          
        do {  
            batchDeletedCount = operLogMapper.deleteLogsBefore(cutoffTime, properties.getBatchSize());  
            deletedCount += batchDeletedCount;  
              
            if (batchDeletedCount > 0) {  
                log.info("已删除 {} 条日志记录，总计删除 {}/{}",   
                    batchDeletedCount, deletedCount, totalCount);  
                  
                // 避免对数据库造成过大压力  
                try {  
                    Thread.sleep(100);  
                } catch (InterruptedException e) {  
                    Thread.currentThread().interrupt();  
                    break;  
                }  
            }  
        } while (batchDeletedCount > 0);  
          
        log.info("日志清理完成，共删除 {} 条记录", deletedCount);  
    }  
}