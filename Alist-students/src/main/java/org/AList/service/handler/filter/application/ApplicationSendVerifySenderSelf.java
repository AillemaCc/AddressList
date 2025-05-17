package org.AList.service.handler.filter.application;

import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ApplicationSendVerifySenderSelf implements ApplicationSendChainFilter<ApplicationSendMsgReqDTO>{
    @Override
    public void handler(ApplicationSendMsgReqDTO requestParam) {
        if(Objects.equals(requestParam.getReceiver(), StuIdContext.getStudentId())){
            throw new ClientException("您不能给自己发送申请");
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
