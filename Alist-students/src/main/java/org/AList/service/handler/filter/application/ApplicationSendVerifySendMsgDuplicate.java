package org.AList.service.handler.filter.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.ApplicationDO;
import org.AList.domain.dao.mapper.ApplicationMapper;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.springframework.stereotype.Component;

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
            throw new ClientException("您已经给对方发送过申请，请等待对方处理请求");
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
