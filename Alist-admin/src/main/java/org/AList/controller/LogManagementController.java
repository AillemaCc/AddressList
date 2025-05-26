package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.common.properties.LogCleanupProperties;
import org.AList.service.operLogService.LogCleanupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/log")
@RequiredArgsConstructor
public class LogManagementController {  
      
    private final LogCleanupService logCleanupService;
    private final LogCleanupProperties properties;
      
    @PostMapping("/cleanup")
    public Result<Void> manualCleanup() {
        try {  
            logCleanupService.cleanupLogs();  
            return Results.success().setMessage("日志清理任务已执行");
        } catch (Exception e) {  
            return Results.failure().setMessage("日志清理失败: " + e.getMessage());
        }  
    }  
      
    @GetMapping("/config")
    public Result<LogCleanupProperties> getConfig() {  
        return Results.success(properties);  
    }  
}