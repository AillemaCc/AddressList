package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.BaseClassInfoAddReqDTO;
import org.AList.service.AdminBaseInfoService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员信息操作控制层
 */
@RestController
@RequestMapping("/api/admin/base")
@RequiredArgsConstructor
public class AdminBaseInfoController {
    private final AdminBaseInfoService adminBaseInfoService;
    /**
     * 新增班级信息
     */
    @PutMapping("/add")
    public Result<Void> addBaseClassInfo(@RequestBody BaseClassInfoAddReqDTO requestParam){
        adminBaseInfoService.addBaseClassInfo(requestParam);
        return Results.success();
    }
}
