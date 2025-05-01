package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.AdminLoginReqDTO;
import org.AList.domain.dto.resp.AdminLoginRespDTO;
import org.AList.service.AdministerService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制层
 */
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdministerController {
    private final AdministerService administerService;

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
     * @param username 管理员username
     * @param token 管理员登录返回token
     */
    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestParam String username,@RequestParam String token){
        administerService.logout(username,token);
        return Results.success().setMessage("已登出");
    }
}
