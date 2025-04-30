package org.AList.service;

import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;

/**
 * 学生客户端服务接口层
 */
public interface StuService {
    /**
     * 用户登录接口
     * @param requestParam
     * @return
     */
    StuLoginRespDTO login(StuLoginReqDTO requestParam);
}
