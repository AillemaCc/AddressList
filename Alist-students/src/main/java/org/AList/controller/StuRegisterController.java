package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.AList.service.StuService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/register")
    public Result<String> register(@RequestBody StuRegisterReqDTO requestParam){
        return Results.success(stuService.register(requestParam));
    }
}
