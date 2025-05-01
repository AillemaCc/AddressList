package org.AList.service;

import org.AList.domain.dto.req.AdminLoginReqDTO;
import org.AList.domain.dto.resp.AdminLoginRespDTO;

/**
 * 管理员服务类接口层
 */
public interface AdministerService {
    /**
     * 管理员登录服务接口
     * @param requestParam 管理员登录请求实体
     * @return 管理员登录响应实体
     */
    AdminLoginRespDTO login(AdminLoginReqDTO requestParam);
}
