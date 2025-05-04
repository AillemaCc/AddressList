package org.AList.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.RegisterDO;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.domain.dao.mapper.RegisterMapper;
import org.AList.domain.dao.mapper.StudentDefaultInfoMapper;
import org.AList.domain.dao.mapper.StudentMapper;
import org.AList.domain.dto.req.AccpetRegistrationReqDTO;
import org.AList.domain.dto.req.RefuseRegistrationReqDTO;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;
import org.AList.service.AdministerAuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 管理员审核相关接口实现层
 */
@Service
@RequiredArgsConstructor
public class AdministerAuditServiceImpl extends ServiceImpl<RegisterMapper, RegisterDO> implements AdministerAuditService {
    private final StudentMapper studentMapper;
    private final StudentDefaultInfoMapper studentDefaultInfoMapper;
    /**
     * @return 待审核用户列表
     */
    @Override
    public IPage<AuditUserPageRespDTO> listAuditRegister() {
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStatus, 0)
                .eq(RegisterDO::getDelFlag, 0)
                .orderByDesc(RegisterDO::getCreateTime);
        IPage<RegisterDO> resultPage=baseMapper.selectPage(new Page<>(), queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each,AuditUserPageRespDTO.class));
    }

    /**
     * 通过注册
     * @param requestParam 通过注册请求实体类
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void acceptRegistration(AccpetRegistrationReqDTO requestParam) {
        checkReviewStatus(requestParam.getStudentId());
        // 从注册请求表当中拿一个符合条件的请求出来
        LambdaUpdateWrapper<RegisterDO> updateWrapper = Wrappers.lambdaUpdate(RegisterDO.class)
                .eq(RegisterDO::getStatus, 0)
                .eq(RegisterDO::getDelFlag, 0)
                .eq(RegisterDO::getStudentId, requestParam.getStudentId());
        RegisterDO aDo = baseMapper.selectOne(updateWrapper);
        if(Objects.isNull(aDo)){
            throw new ClientException("审核操作失败，数据记录异常，请检查数据记录或者刷新重试");
        }
        RegisterDO registerDO=RegisterDO.builder()
                .status(1)
                .build();
        int update = baseMapper.update(registerDO, updateWrapper);
        if(update<1){
            throw new ClientException("审核操作失败，数据记录异常，请检查数据记录或者刷新重试");
        }
        LambdaQueryWrapper<StudentDefaultInfoDO> infoDOLambdaQueryWrapper = Wrappers.lambdaQuery(StudentDefaultInfoDO.class)
                .eq(StudentDefaultInfoDO::getStudentId, requestParam.getStudentId());
        StudentDefaultInfoDO studentDefaultInfoDO = studentDefaultInfoMapper.selectOne(infoDOLambdaQueryWrapper);
        StudentDO studentDO=StudentDO.builder()
                .studentId(aDo.getStudentId())
                .name(aDo.getName())
                .major(studentDefaultInfoDO.getMajor())
                .className(studentDefaultInfoDO.getClassName())
                .enrollmentYear(studentDefaultInfoDO.getEnrollmentYear())
                .graduationYear(studentDefaultInfoDO.getGraduationYear())
                .phone(aDo.getPhone())
                .email(aDo.getEmail())
                .password(aDo.getPassword())
                .status(1)
                .registerToken(aDo.getRegisterToken())
                .build();
        studentMapper.insert(studentDO);
    }

    /**
     * 检查学号对应的注册请求的审核状态
     *
     * @param requestParam 注册请求实体类
     */
    @Override
    public void checkReviewStatus(String requestParam) {
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStudentId, requestParam);
        RegisterDO registerDO=baseMapper.selectOne(queryWrapper);
        if(Objects.isNull(registerDO)){
            throw new ClientException("学号对应的注册记录不存在");
        }
        else if(registerDO.getStatus()==1){
            throw new ClientException("学号对应的注册请求已审核通过，无需重复审核");
        }
        else if(registerDO.getStatus()==2){
            throw new ClientException("学号对应的注册请求已审核拒绝，无需重复审核");
        }
        else if(registerDO.getDelFlag()==1){
            throw new ClientException("学号对应的注册请求已删除");
        }

    }

    /**
     * 拒绝注册
     *
     * @param requestParam 拒绝注册请求实体类
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refuseRegistration(RefuseRegistrationReqDTO requestParam) {
        checkReviewStatus(requestParam.getStudentId());
        LambdaUpdateWrapper<RegisterDO> updateWrapper = Wrappers.lambdaUpdate(RegisterDO.class)
                .eq(RegisterDO::getStudentId, requestParam.getStudentId());
        if(Objects.isNull(baseMapper.selectOne(updateWrapper))){
            throw new ClientException("审核操作失败，未能查询到数据，请检查数据记录或者刷新重试");
        }
        RegisterDO registerDO=RegisterDO.builder()
                .remark(requestParam.getRemark())
                .status(2)
                .build();
        int update = baseMapper.update(registerDO, updateWrapper);
        if(update<1){
            throw new ClientException("审核操作失败，未能更新数据，请检查数据记录或者刷新重试");
        }
    }
}
