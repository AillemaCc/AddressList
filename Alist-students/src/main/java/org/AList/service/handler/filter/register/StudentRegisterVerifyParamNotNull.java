package org.AList.service.handler.filter.register;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dto.req.StuRegisterReqDTO;

public final class StudentRegisterVerifyParamNotNull implements StudentRegisterChainFilter<StuRegisterReqDTO>{
    @Override
    // todo 抽象错误码
    public void handler(StuRegisterReqDTO requestParam) {
        if (StringUtils.isBlank(requestParam.getStudentId())) {
            throw new ClientException("学号不能为空");
        } else if (StringUtils.isBlank(requestParam.getName())) {
            throw new ClientException("姓名不能为空");
        } else if (StringUtils.isBlank(requestParam.getPhone())) {
            throw new ClientException("手机号不能为空");
        } else if (StringUtils.isBlank(requestParam.getEmail())) {
            throw new ClientException("邮箱不能为空");
        } else if (StringUtils.isBlank(requestParam.getPassword())) {
            throw new ClientException("密码不能为空");
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
