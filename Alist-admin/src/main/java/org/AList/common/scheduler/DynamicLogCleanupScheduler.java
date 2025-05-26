package org.AList.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.properties.LogCleanupProperties;
import org.AList.service.operLogService.LogCleanupService;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DynamicLogCleanupScheduler implements SchedulingConfigurer {
      
    private final LogCleanupService logCleanupService;
    private final LogCleanupProperties properties;
      
    @Override  
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(  
            () -> {  
                try {  
                    if (properties.isEnabled()) {  
                        log.info("开始执行定时日志清理任务");  
                        logCleanupService.cleanupLogs();  
                    }  
                } catch (Exception e) {  
                    log.error("定时日志清理任务执行失败", e);  
                }  
            },  
            triggerContext -> {  
                String cron = properties.getSchedule();  
                return new CronTrigger(cron).nextExecutionTime(triggerContext);
            }  
        );  
    }  
}