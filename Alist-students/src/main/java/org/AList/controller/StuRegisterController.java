package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.annotation.MyLog;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.StuRegisterRemarkReqDTO;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.AList.domain.dto.resp.StuRegisterRemarkRespDTO;
import org.AList.service.StuService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户注册接口层
 */
@RestController
@RequestMapping("/api/stu")
@RequiredArgsConstructor
public class StuRegisterController {
    private final StuService stuService;

    /**
     * 用户注册接口
     * @param requestParam 用户注册请求实体
     * @return 用户注册返回的token
     */
    @MyLog(title = "注册模块", content = "注册操作")
    @PostMapping("/register")
    public Result<String> register(@RequestBody StuRegisterReqDTO requestParam){
        return Results.success(stuService.register(requestParam));
    }

    /**
     * 用户查询注册结果接口
     * @param requestParam 查询请求实体
     * @return 查询响应实体
     */
    @MyLog(title = "注册模块", content = "用户查询注册结果操作")
    @GetMapping("/getRemark")
    public Result<StuRegisterRemarkRespDTO> getRemark(@RequestBody StuRegisterRemarkReqDTO requestParam){
        return Results.success(stuService.getRemark(requestParam));
    }
}
