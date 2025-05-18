package org.AList.service.handler.filter.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dao.entity.StudentFrameworkDO;
import org.AList.domain.dao.mapper.StudentFrameWorkMapper;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.springframework.stereotype.Component;
import static org.AList.common.convention.errorcode.BaseErrorCode.*;
@Component
@RequiredArgsConstructor
public class ApplicationSendVerifyReceiverStatus implements ApplicationSendChainFilter<ApplicationSendMsgReqDTO>{
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    @Override
    public void handler(ApplicationSendMsgReqDTO requestParam) {
        LambdaQueryWrapper<StudentFrameworkDO> validQueryWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, requestParam.getReceiver())
                .eq(StudentFrameworkDO::getStatus, 1)
                .eq(StudentFrameworkDO::getDelFlag, 0);
        StudentFrameworkDO receiverDO = studentFrameWorkMapper.selectOne(validQueryWrapper);

        if(receiverDO == null) {
            throw new UserException(RECIPIENT_ABNORMAL);                                                                //A0310：收信者状态异常
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
