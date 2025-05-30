package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.*;
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
    IPage<ContactQueryRespDTO> queryContactList(ContactQueryAllOwnReqDTO requestParam);

    /**
     * 恢复删除的通讯信息
     * @param requestParam 恢复通讯信息请求体
     */
    void restoreStudentContact(ContactRestoreReqDTO requestParam);

    /**
     * 分页展示已删除的通讯信息
     * @param requestParam 请求体
     * @return 分页返回
     */
    IPage<ContactQueryRespDTO> queryContactListAllDelete(ContactQueryAllOwnReqDTO requestParam);
}
