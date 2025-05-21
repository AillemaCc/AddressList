package org.AList.service.handler.filter.register;

import org.AList.common.convention.exception.UserException;
import org.AList.domain.dto.req.StuRegisterReqDTO;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;

public final class StudentRegisterVerifyPasswordSafe implements StudentRegisterChainFilter<StuRegisterReqDTO>{
    @Override
    public void handler(StuRegisterReqDTO requestParam) {
        String password = requestParam.getPassword();
        // todo 抽象错误码
        if (password == null || password.trim().isEmpty()) {
            throw new UserException(PWD_EMPTY);                                                                         //A0121：密码为空
        }

        if (password.length() < 8) {
            throw new UserException(PWD_LEN_SHORT);                                                                     //A0122：密码长度小于八位
        }

        if (!password.matches(".*[a-z].*")) {
            throw new UserException(PWD_MISS_LOWER);                                                                    //A0123：密码缺少小写字母
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new UserException(PWD_MISS_UPPER);                                                                    //A0124：密码缺少大写字母
        }

        if (!password.matches(".*\\d.*")) {
            throw new UserException(PWD_MISS_DIGIT);                                                                    //A0125：密码缺少数字
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
