package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.BaseClassInfoAddReqDTO;
import org.AList.domain.dto.req.BaseClassInfoUpdateReqDTO;
import org.AList.service.AdminBaseInfoService;
import org.springframework.web.bind.annotation.*;

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
    /**
     * 更新班级信息
     */
    @PostMapping("/update")
    public Result<Void> updateBaseClassInfo(@RequestBody BaseClassInfoUpdateReqDTO requestParam){
        adminBaseInfoService.updateBaseClassInfo(requestParam);
        return Results.success();
    }
}
