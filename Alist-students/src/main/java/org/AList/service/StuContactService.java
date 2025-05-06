package org.AList.service;

import org.AList.domain.dto.req.AddContactReqDTO;

/**
 * 通讯信息服务层
 */
public interface StuContactService {
    /**
     * 新增通讯信息
     * @param requestParam 新增通讯信息请求体
     */
    void addStudentContact(AddContactReqDTO requestParam);
}
