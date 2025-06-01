package org.AList.service.handler.filter.register;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.springframework.stereotype.Component;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;
@Component
public final class StudentRegisterVerifyParamNotNull implements StudentRegisterChainFilter<StuRegisterReqDTO>{
    @Override
    public void handler(StuRegisterReqDTO requestParam) {
        if (StringUtils.isBlank(requestParam.getStudentId())) {
            throw new UserException(STU_ID_EMPTY);                                                                      //A0112：学号为空
        } else if (StringUtils.isBlank(requestParam.getName())) {
            throw new UserException(NAME_EMPTY);                                                                        //A0131：姓名为空
        } else if (StringUtils.isBlank(requestParam.getPhone())) {
            throw new UserException(PHONE_EMPTY);                                                                       //A0133：手机号为空
        } else if (StringUtils.isBlank(requestParam.getEmail())) {
            throw new UserException(EMAIL_EMPTY);                                                                       //A0132：邮箱为空
        } else if (StringUtils.isBlank(requestParam.getPassword())) {
            throw new UserException(PWD_EMPTY);                                                                         //A0121：密码为空
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
