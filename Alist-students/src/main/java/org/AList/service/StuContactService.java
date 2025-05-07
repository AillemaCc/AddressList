package org.AList.service;

import org.AList.domain.dto.req.AddContactReqDTO;
import org.AList.domain.dto.req.DeleteContactReqDTO;
import org.AList.domain.dto.req.QueryContactByIdReqDTO;
import org.AList.domain.dto.req.UpdateContactReqDTO;
import org.AList.domain.dto.resp.QueryContactRespDTO;

/**
 * 通讯信息服务层
 */
public interface StuContactService {
    /**
     * 新增通讯信息
     * @param requestParam 新增通讯信息请求体
     */
    void addStudentContact(AddContactReqDTO requestParam);

    /**
     * 删除通讯信息
     * @param requestParam 删除通讯信息请求体
     */
    void deleteStudentContact(DeleteContactReqDTO requestParam);

    /**
     * 修改通讯信息
     * @param requestParam 修改通讯信息请求体
     */
    void updateStudentContact(UpdateContactReqDTO requestParam);

    /**
     * 按学号查询通讯信息
     * @param requestParam 查询通讯信息请求体
     * @return 单个学生的通讯信息
     */
    QueryContactRespDTO queryContactById(QueryContactByIdReqDTO requestParam);
}
