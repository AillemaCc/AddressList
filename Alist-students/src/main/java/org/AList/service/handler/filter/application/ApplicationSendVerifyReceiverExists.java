package org.AList.service.handler.filter.application;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.AList.service.bloom.StudentIdBloomFilterService;
import org.springframework.stereotype.Component;

import static org.AList.common.enums.UserErrorCodeEnum.USER_NULL;
@Component
@RequiredArgsConstructor
public final class ApplicationSendVerifyReceiverExists implements ApplicationSendChainFilter<ApplicationSendMsgReqDTO>{
    private final StudentIdBloomFilterService studentIdBloomFilterService;
    @Override
    public void handler(ApplicationSendMsgReqDTO requestParam) {
        if(!studentIdBloomFilterService.contain(requestParam.getReceiver())){
            throw new ClientException(USER_NULL);
        }
    }
    @Override
    public int getOrder() {
        return 0;
    }
}
