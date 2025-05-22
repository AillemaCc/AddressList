package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.resp.AdminHomePageRespDTO;
import org.AList.service.AdminHomePageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/homepage")
@RequiredArgsConstructor
public class AdminHomePageController {  
    private final AdminHomePageService adminHomePageService;
      
    /**  
     * 获取管理员主页数据  
     * @param days 最近几天的操作日志，默认3天  
     * @return 管理员主页数据  
     */  
    @GetMapping("/data")
    public Result<AdminHomePageRespDTO> getAdminHomePageData(@RequestParam(required = false, defaultValue = "3") Integer days) {
        return Results.success(adminHomePageService.getAdminHomePageData(days));
    }  
}