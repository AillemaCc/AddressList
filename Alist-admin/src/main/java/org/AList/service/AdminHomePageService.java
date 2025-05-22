package org.AList.service;

import org.AList.domain.dto.resp.AdminHomePageRespDTO;

// Service接口
public interface AdminHomePageService {  
    /**  
     * 获取管理员主页数据  
     * @param days 最近几天的操作日志  
     * @return 管理员主页数据  
     */  
    AdminHomePageRespDTO getAdminHomePageData(Integer days);
} 