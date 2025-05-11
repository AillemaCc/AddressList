package org.AList.service;

import org.AList.domain.dto.req.BaseClassInfoAddReqDTO;

/**
 * 管理员基础信息操作服务类接口层
 */
public interface AdminBaseInfoService {
    /**
     * 新增班级基础信息
     * @param requestParam 新增班级信息请求体
     */
    void addBaseClassInfo(BaseClassInfoAddReqDTO requestParam);
}
