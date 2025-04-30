package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;
import org.AList.service.StuService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生用户端Controller层
 */
@RestController
@RequestMapping("/api/stu")
@RequiredArgsConstructor
public class StuLoginController {
    private final StuService stuService;

    /**
     * 学生登录接口
     * @param requestParam
     * @return
     */
    @PostMapping("/login")
    public Result<StuLoginRespDTO> login(@RequestBody StuLoginReqDTO requestParam) {
        StuLoginRespDTO userLoginRespDTO= stuService.login(requestParam);
        return Results.success(userLoginRespDTO).setMessage("登录成功");
    }

}
