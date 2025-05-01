package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.AdminLoginReqDTO;
import org.AList.domain.dto.resp.AdminLoginRespDTO;
import org.AList.service.AdministerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
