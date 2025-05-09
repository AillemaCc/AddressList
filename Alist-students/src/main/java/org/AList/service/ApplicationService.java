package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.ApplicationReceiveQueryPageReqDTO;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.AList.domain.dto.req.ApplicationSendQueryPageReqDTO;
import org.AList.domain.dto.req.ApplicationYONReqDTO;
import org.AList.domain.dto.resp.QueryApplicationPageRespDTO;

/**
 * 站内信Service层
 */
public interface ApplicationService {
    /**
     * 向某人发送站内信
     * @param requestParam 发送的站内信请求内容
     */
    void sendApplication(ApplicationSendMsgReqDTO requestParam);

    /**
     * 展示所有没删除 没审核的站内信请求
     * @param requestParam 传入参数-学生学号-接收用户
     * @return 分页结果
     */
    IPage<QueryApplicationPageRespDTO> listAllValidApplication(ApplicationReceiveQueryPageReqDTO requestParam);

    /**
     * 展示没删除 已通过的站内信请求
     * @param requestParam 传入参数-学生学号-接收用户
     * @return 分页结果
     */
    IPage<QueryApplicationPageRespDTO> listAllAcceptedApplication(ApplicationReceiveQueryPageReqDTO requestParam);

    /**
     * 展示没删除 已拒绝的站内信请求
     * @param requestParam 传入参数-学生学号-接收用户
     * @return 分页结果
     */
    IPage<QueryApplicationPageRespDTO> listAllRefusedApplication(ApplicationReceiveQueryPageReqDTO requestParam);

    /**
     * 展示已删除的站内信申请
     * @param requestParam 传入参数-学生学号-接收用户
     * @return 分页结果
     */
    IPage<QueryApplicationPageRespDTO> listAllDeleteApplication(ApplicationReceiveQueryPageReqDTO requestParam);

    /**
     * 展示已发送的站内信请求
     * @param requestParam 传入参数-消息的sender
     * @return void
     */
    IPage<QueryApplicationPageRespDTO> listAllSendApplication(ApplicationSendQueryPageReqDTO requestParam);

    /**
     * 同意某个站内信申请
     * @param requestParam 同意或者拒绝操作请求体--可以通过发送者和接收者定位某条消息
     */
    void acceptSingleApplication(ApplicationYONReqDTO requestParam);

    /**
     * 拒绝某个站内信申请
     * @param requestParam 同意或者拒绝操作请求体
     */
    void refuseSingleApplication(ApplicationYONReqDTO requestParam);

    /**
     * 删除某个站内信申请
     * @param requestParam 同意或者拒绝操作请求体
     */
    void deleteSingleApplication(ApplicationYONReqDTO requestParam);





}
