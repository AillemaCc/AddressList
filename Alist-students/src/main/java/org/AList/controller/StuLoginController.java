package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.annotation.MyLog;
import org.AList.common.convention.exception.UserException;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.StuLoginRefreshToken;
import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.resp.RefreshTokenRespDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;
import org.AList.service.StuService;
import org.AList.service.StuToken.TokenService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.AList.common.convention.errorcode.BaseErrorCode.USER_NOT_LOGGED;

/**
 * 学生用户端登录接口Controller层
 */
@RestController
@RequestMapping("/api/stu")
@RequiredArgsConstructor
public class StuLoginController {
    private final StuService stuService;
    private final TokenService tokenService;

    /**
     * 学生登录接口
     *
     * @param requestParam 学生登录请求体
     * @return 学生登录响应体
     */
    @PostMapping("/login")
    @MyLog(title = "登录模块", content = "登录操作")
    public Result<StuLoginRespDTO> login(@RequestBody StuLoginReqDTO requestParam, HttpServletRequest request) {
        StuLoginRespDTO userLoginRespDTO= stuService.login(requestParam, request);
        return Results.success(userLoginRespDTO).setMessage("登录成功");
    }

    /**
     * 检查学生登录状态接口
     * @param studentId 学号
     * @param token 登录分配的token
     * @return 用户是否登录
     */
    @MyLog(title = "登录模块", content = "检查学生登录状态")
    @GetMapping("/checkLogin")
    public Result<Boolean> checkLogin(@RequestParam("studentId") String studentId,@RequestParam("token") String token) {
        return Results.success(stuService.checkLogin(studentId,token));
    }

    /**
     * 学生登出接口
     * @param studentId 学号
     * @return 无--直接进行存在判断并且删除
     */
    @MyLog(title = "登录模块", content = "登出操作")
    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestParam("studentId") String studentId,@RequestParam("token") String accessToken,@RequestParam("refreshToken") String refreshToken) {
        stuService.logout(studentId,accessToken,refreshToken);
        return Results.success().setMessage("用户已登出");
    }

    /**
     * 刷新Access Token接口
     * @return 新的Access Token
     */
    @MyLog(title = "登录模块", content = "刷新accessToken")
    @PostMapping("/refreshToken")
    public Result<RefreshTokenRespDTO> refreshToken(@RequestBody StuLoginRefreshToken requestParam) {
        try {
            String newAccessToken = tokenService.refreshStudentAccessToken(requestParam.getStudentId(),requestParam.getRefreshToken());
            return Results.success(new RefreshTokenRespDTO(newAccessToken));
        } catch (Exception e) {
            throw new UserException(USER_NOT_LOGGED);
        }
    }

}
