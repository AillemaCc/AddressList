package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.AList.domain.dto.req.QueryApplicationPageReqDTO;
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
     * @param requestParam 传入参数-当前登录的学生学号-接收用户
     * @return 分页结果
     */
    IPage<QueryApplicationPageRespDTO> listAllValidApplication(QueryApplicationPageReqDTO requestParam);

    /**
     * 展示没删除 已通过的站内信请求
     * @param requestParam 传入参数-当前登录的学生学号-接收用户
     * @return 分页结果
     */
    IPage<QueryApplicationPageRespDTO> listAllAcceptedApplication(QueryApplicationPageReqDTO requestParam);

    /**
     * 展示没删除 已拒绝的站内信请求
     * @param requestParam 传入参数-当前登录的学生学号-接收用户
     * @return 分页结果
     */
    IPage<QueryApplicationPageRespDTO> listAllRefusedApplication(QueryApplicationPageReqDTO requestParam);
}
