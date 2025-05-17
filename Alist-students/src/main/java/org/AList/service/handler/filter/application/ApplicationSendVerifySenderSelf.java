package org.AList.service.handler.filter.application;

import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.springframework.stereotype.Component;
import static org.AList.common.convention.errorcode.BaseErrorCode.*;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ApplicationSendVerifySenderSelf implements ApplicationSendChainFilter<ApplicationSendMsgReqDTO>{
    @Override
    public void handler(ApplicationSendMsgReqDTO requestParam) {
        if(Objects.equals(requestParam.getReceiver(), StuIdContext.getStudentId())){
            throw new UserException(TARGET_IS_SELF);                                                                     //A0302：申请对象不能为申请者
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
