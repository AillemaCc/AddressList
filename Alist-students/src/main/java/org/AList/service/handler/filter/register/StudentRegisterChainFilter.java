package org.AList.service.handler.filter.register;

import org.AList.common.enums.StudentChainMarkEnum;
import org.AList.designpattern.chain.AbstractChainHandler;
import org.AList.domain.dto.req.StuRegisterReqDTO;

public interface StudentRegisterChainFilter <T extends StuRegisterReqDTO> extends AbstractChainHandler<StuRegisterReqDTO> {
    /**
     * @return 责任链组件标识
     */
    @Override
    default String mark(){
        return StudentChainMarkEnum.STUDENT_REGISTER.name();
    };
}
