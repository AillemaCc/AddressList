package org.AList.service.handler.filter.register;

import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dto.req.StuRegisterReqDTO;

public final class StudentRegisterVerifyPasswordSafe implements StudentRegisterChainFilter<StuRegisterReqDTO>{
    @Override
    public void handler(StuRegisterReqDTO requestParam) {
        String password = requestParam.getPassword();
        // todo 抽象错误码
        if (password == null || password.trim().isEmpty()) {
            throw new ClientException("密码不能为空");
        }

        if (password.length() < 8) {
            throw new ClientException("密码长度必须大于等于8位");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ClientException("密码必须包含至少一个小写字母");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ClientException("密码必须包含至少一个大写字母");
        }

        if (!password.matches(".*\\d.*")) {
            throw new ClientException("密码必须包含至少一个数字");
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
