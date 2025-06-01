package org.AList.service.handler.filter.register;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.domain.dao.mapper.StudentDefaultInfoMapper;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.AList.common.convention.errorcode.BaseErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Component
public final class StudentRegisterVerifyExistsByDB implements StudentRegisterChainFilter<StuRegisterReqDTO>{
    private final StudentDefaultInfoMapper studentDefaultInfoMapper;
    @Override
    public void handler(StuRegisterReqDTO requestParam) {
        // 布隆过滤器存在误判率，被误判为存在的请求，需要再去数据库查询一下。这时候需要查询的仍然是学籍库，也就是全量默认数据的查询
        LambdaQueryWrapper<StudentDefaultInfoDO> queryWrapper = Wrappers.lambdaQuery(StudentDefaultInfoDO.class)
                .eq(StudentDefaultInfoDO::getStudentId, requestParam.getStudentId())
                .eq(StudentDefaultInfoDO::getName,requestParam.getName())
                .eq(StudentDefaultInfoDO::getDelFlag, 0);
        if(Objects.isNull(studentDefaultInfoMapper.selectOne(queryWrapper))){
            throw new UserException(USER_NOT_FOUND);                                                                    //A0201：用户不存在
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
