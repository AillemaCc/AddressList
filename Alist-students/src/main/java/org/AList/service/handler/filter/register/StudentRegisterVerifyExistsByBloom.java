package org.AList.service.handler.filter.register;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.AList.service.bloom.StudentIdBloomFilterService;
import org.springframework.stereotype.Component;

import static org.AList.common.convention.errorcode.BaseErrorCode.USER_NOT_FOUND;
@RequiredArgsConstructor
@Component
@Slf4j
public final class StudentRegisterVerifyExistsByBloom implements StudentRegisterChainFilter<StuRegisterReqDTO>{
    private final StudentIdBloomFilterService studentIdBloomFilterService;
    @Override
    public void handler(StuRegisterReqDTO requestParam) {
        log.info("id{}",requestParam.getStudentId());
        // 缓存全量学号的布隆过滤器提供快速错误机制 防止缓存穿透
        if(studentIdBloomFilterService.contain(requestParam.getStudentId())){
            throw new UserException(USER_NOT_FOUND);                                                                     //A0201：用户不存在
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
