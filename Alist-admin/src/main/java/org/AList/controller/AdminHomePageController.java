package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.AdminHomePageReqDTO;
import org.AList.domain.dto.resp.AdminHomePageDataDTO;
import org.AList.service.AdminHomePageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/homepage")
@RequiredArgsConstructor
public class AdminHomePageController {  
    private final AdminHomePageService adminHomePageService;
      
    /**
     * 获取管理员主页数据
     *
     * @param requestParam@return 管理员主页数据
     */  
    @GetMapping("/data")
    public Result<AdminHomePageDataDTO> getAdminHomePageData(@RequestBody AdminHomePageReqDTO requestParam) {
        return Results.success(adminHomePageService.getAdminHomePageData(requestParam));
    }  
}