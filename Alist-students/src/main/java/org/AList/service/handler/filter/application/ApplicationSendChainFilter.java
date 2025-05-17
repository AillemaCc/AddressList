package org.AList.service.handler.filter.application;

import org.AList.common.enums.StudentChainMarkEnum;
import org.AList.designpattern.chain.AbstractChainHandler;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;

public interface ApplicationSendChainFilter <T extends ApplicationSendMsgReqDTO> extends AbstractChainHandler<ApplicationSendMsgReqDTO> {
    @Override
    default String mark() {
        return StudentChainMarkEnum.APPLICATION_SEND_FILTER.name();
    }
}
