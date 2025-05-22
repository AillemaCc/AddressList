package org.AList.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.UserException;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.AdminLoginRefreshToken;
import org.AList.domain.dto.req.AdminLoginReqDTO;
import org.AList.domain.dto.resp.AdminLoginRespDTO;
import org.AList.domain.dto.resp.RefreshTokenRespDTO;
import org.AList.service.AdminToken.TokenService;
import org.AList.service.AdministerService;
import org.springframework.web.bind.annotation.*;

import static org.AList.common.convention.errorcode.BaseErrorCode.USER_NOT_LOGGED;

/**
 * 管理员控制层
 */
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdministerController {
    private final AdministerService administerService;
    private final TokenService tokenService;

    /**
     * 管理员登录接口
     * @param requestParam 管理员登录请求实体
     * @return 管理员登录响应实体
     */
    @PostMapping("/login")
    public Result<AdminLoginRespDTO> login(@RequestBody AdminLoginReqDTO requestParam) {
        AdminLoginRespDTO adminLoginRespDTO = administerService.login(requestParam);
        return Results.success(adminLoginRespDTO).setMessage("管理员登录成功");
    }

    /**
     * 检查管理员登录状态接口
     * @param username 管理员username
     * @param token 管理员登录返回token
     * @return 管理员是否登录
     */
    @GetMapping("/checkLogin")
    public Result<Boolean> checkLogin(@RequestParam String username,@RequestParam String token) {
        return Results.success(administerService.checkLogin(username,token));
    }

    /**
     * 管理员登出接口
     */
    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestParam String username,@RequestParam String accessToken,@RequestParam String refreshToken){
        administerService.logout(username,accessToken,refreshToken);
        return Results.success().setMessage("已登出");
    }

    /**
     * 刷新管理员访问令牌
     */
    @PostMapping("/refreshToken")
    public Result<RefreshTokenRespDTO> refreshToken(@RequestBody AdminLoginRefreshToken requestParam) {
        try {
            String newAccessToken = tokenService.refreshAdministerAccessToken(requestParam.getUsername(), requestParam.getRefreshToken());
            return Results.success(new RefreshTokenRespDTO(newAccessToken));
        } catch (Exception e) {
            throw new UserException(USER_NOT_LOGGED);
        }
    }
}
