package org.AList.service.handler.filter.register;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.AList.service.bloom.StudentIdBloomFilterService;

import static org.AList.common.convention.errorcode.BaseErrorCode.USER_NOT_FOUND;
@RequiredArgsConstructor
public final class StudentRegisterVerifyExistsByBloom implements StudentRegisterChainFilter<StuRegisterReqDTO>{
    private final StudentIdBloomFilterService studentIdBloomFilterService;
    @Override
    public void handler(StuRegisterReqDTO requestParam) {
        // 缓存全量学号的布隆过滤器提供快速错误机制 防止缓存穿透
        if(!studentIdBloomFilterService.contain(requestParam.getStudentId())){
            throw new UserException(USER_NOT_FOUND);                                                                     //A0201：用户不存在
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
