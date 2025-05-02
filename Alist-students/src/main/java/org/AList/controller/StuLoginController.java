package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;
import org.AList.service.StuService;
import org.springframework.web.bind.annotation.*;

/**
 * 学生用户端登录接口Controller层
 */
@RestController
@RequestMapping("/api/stu")
@RequiredArgsConstructor
public class StuLoginController {
    private final StuService stuService;

    /**
     * 学生登录接口
     * @param requestParam 学生登录请求体
     * @return 学生登录响应体
     */
    @PostMapping("/login")
    public Result<StuLoginRespDTO> login(@RequestBody StuLoginReqDTO requestParam) {
        StuLoginRespDTO userLoginRespDTO= stuService.login(requestParam);
        return Results.success(userLoginRespDTO).setMessage("登录成功");
    }

    /**
     * 检查学生登录状态接口
     * @param studentId 学号
     * @param token 登录分配的token
     * @return 用户是否登录
     */
    @GetMapping("/checkLogin")
    public Result<Boolean> checkLogin(@RequestParam("studentId") String studentId,@RequestParam("token") String token) {
        return Results.success(stuService.checkLogin(studentId,token));
    }

    /**
     * 学生登出接口
     * @param studentId 学号
     * @param token 登录分配的token
     * @return 无--直接进行存在判断并且删除
     */
    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestParam("studentId") String studentId,@RequestParam("token") String token) {
        stuService.logout(studentId,token);
        return Results.success().setMessage("用户已登出");
    }

}
