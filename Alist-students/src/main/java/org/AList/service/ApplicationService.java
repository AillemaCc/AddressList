package org.AList.service;

import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;

/**
 * 站内信Service层
 */
public interface ApplicationService {
    /**
     * 向某人发送
     * @param requestParam
     */
    void sendApplication(ApplicationSendMsgReqDTO requestParam);
}
