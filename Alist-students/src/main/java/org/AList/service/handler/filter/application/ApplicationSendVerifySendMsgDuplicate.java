package org.AList.service.handler.filter.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dao.entity.ApplicationDO;
import org.AList.domain.dao.mapper.ApplicationMapper;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.springframework.stereotype.Component;
import static org.AList.common.convention.errorcode.BaseErrorCode.*;
@Component
@RequiredArgsConstructor
public class ApplicationSendVerifySendMsgDuplicate implements ApplicationSendChainFilter<ApplicationSendMsgReqDTO>{
    private final ApplicationMapper applicationMapper;
    @Override
    public void handler(ApplicationSendMsgReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> uniqueMsgQueryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getSender, StuIdContext.getStudentId())
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .ne(ApplicationDO::getStatus, 2)
                .eq(ApplicationDO::getDelFlag, 0);
        Long selectCount = applicationMapper.selectCount(uniqueMsgQueryWrapper);
        if(selectCount > 1){
            throw new UserException(APPLICANT_ACCEPTED);                                                                 //A0303：申请者已接受过申请
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
