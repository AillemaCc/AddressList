package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.ContactAddReqDTO;
import org.AList.domain.dto.req.ContactDeleteReqDTO;
import org.AList.domain.dto.req.ContactQueryByIdReqDTO;
import org.AList.domain.dto.req.ContactUpdateReqDTO;
import org.AList.domain.dto.resp.ContactQueryRespDTO;

/**
 * 通讯信息服务层
 */
public interface StuContactService {
    /**
     * 新增通讯信息
     * @param requestParam 新增通讯信息请求体
     */
    void addStudentContact(ContactAddReqDTO requestParam);

    /**
     * 删除通讯信息
     * @param requestParam 删除通讯信息请求体
     */
    void deleteStudentContact(ContactDeleteReqDTO requestParam);

    /**
     * 修改通讯信息
     * @param requestParam 修改通讯信息请求体
     */
    void updateStudentContact(ContactUpdateReqDTO requestParam);

    /**
     * 按学号查询通讯信息
     * @param requestParam 查询通讯信息请求体
     * @return 单个学生的通讯信息
     */
    ContactQueryRespDTO queryContactById(ContactQueryByIdReqDTO requestParam);

    /**
     * 分页查询通讯信息
     * @return 分页返回
     */
    IPage<ContactQueryRespDTO> queryContactList();
}
