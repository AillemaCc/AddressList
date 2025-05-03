package org.AList.service;

import org.AList.domain.dto.req.AdminLoginReqDTO;
import org.AList.domain.dto.resp.AdminLoginRespDTO;

/**
 * 管理员登录登出服务类接口层
 */
public interface AdministerService {
    /**
     * 管理员登录服务接口
     * @param requestParam 管理员登录请求实体
     * @return 管理员登录响应实体
     */
    AdminLoginRespDTO login(AdminLoginReqDTO requestParam);

    /**
     * 检查管理员登录状态接口
     * @param username 管理员username
     * @param token 管理员登录返回token
     * @return 管理员是否登录
     */
    Boolean checkLogin(String username, String token);

    /**
     * 管理员登出接口
     * @param username 管理员username
     * @param token 管理员登录返回token
     */
    void logout(String username, String token);
}
