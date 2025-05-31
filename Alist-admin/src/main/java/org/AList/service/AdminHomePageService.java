package org.AList.service;

import org.AList.domain.dto.req.AdminHomePageReqDTO;
import org.AList.domain.dto.resp.AdminHomePageDataDTO;

// Service接口
public interface AdminHomePageService {  
    /**
     * 获取管理员主页数据
     *
     * @param requestParam@return 管理员主页数据
     */  
    AdminHomePageDataDTO getAdminHomePageData(AdminHomePageReqDTO requestParam);
} 