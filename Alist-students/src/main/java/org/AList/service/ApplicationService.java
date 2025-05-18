package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.ApplicationReceiveQueryPageReqDTO;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.AList.domain.dto.req.ApplicationSendQueryPageReqDTO;
import org.AList.domain.dto.req.ApplicationYONReqDTO;
import org.AList.domain.dto.resp.ApplicationQueryPageRespDTO;

/**
 * 站内信服务接口
 * 提供站内信的发送、查询以及审核等功能
 */
public interface ApplicationService {
    /**
     * 发送站内信申请
     */
    void sendApplication(ApplicationSendMsgReqDTO requestParam);

    /**
     * 分页查询已通过的站内信申请（状态为已同意）
     */
    IPage<ApplicationQueryPageRespDTO> listAllValidApplication(ApplicationReceiveQueryPageReqDTO requestParam);

    /**
     * 展示没删除 已通过的站内信请求
     */
    IPage<ApplicationQueryPageRespDTO> listAllAcceptedApplication(ApplicationReceiveQueryPageReqDTO requestParam);

    /**
     * 分页查询已拒绝的站内信申请（状态为已拒绝）
     */
    IPage<ApplicationQueryPageRespDTO> listAllRefusedApplication(ApplicationReceiveQueryPageReqDTO requestParam);

    /**
     * 分页查询已删除的站内信申请（逻辑删除）
     */
    IPage<ApplicationQueryPageRespDTO> listAllDeleteApplication(ApplicationReceiveQueryPageReqDTO requestParam);

    /**
     * 分页查询当前用户发送的所有站内信
     */
    IPage<ApplicationQueryPageRespDTO> listAllSendApplication(ApplicationSendQueryPageReqDTO requestParam);

    /**
     * 同意某个站内信申请 同意后会将接收者的联系方式对发送者可见
     */
    void acceptSingleApplication(ApplicationYONReqDTO requestParam);

    /**
     * 拒绝某个站内信申请
     */
    void refuseSingleApplication(ApplicationYONReqDTO requestParam);

    /**
     * 删除某个站内信申请
     */
    void deleteSingleApplication(ApplicationYONReqDTO requestParam);





}
