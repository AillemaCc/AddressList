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
import org.AList.common.convention.exception.ServiceException;
import org.AList.domain.dao.entity.RegisterDO;
import org.AList.domain.dao.entity.StudentFrameworkDO;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.domain.dao.mapper.RegisterMapper;
import org.AList.domain.dao.mapper.StudentDefaultInfoMapper;
import org.AList.domain.dao.mapper.StudentFrameWorkMapper;
import org.AList.domain.dto.req.AccpetRegistrationReqDTO;
import org.AList.domain.dto.req.BanStudentReqDTO;
import org.AList.domain.dto.req.RefuseRegistrationReqDTO;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;
import org.AList.service.AdministerAuditService;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.AList.common.constant.RedisKeyConstant.LOCK_UPDATE_BAN_KEY;
import static org.AList.common.constant.RedisKeyConstant.LOCK_UPDATE_UNBAN_KEY;

/**
 * 管理员审核相关接口实现层
 */
@Service
@RequiredArgsConstructor
public class AdministerAuditServiceImpl extends ServiceImpl<RegisterMapper, RegisterDO> implements AdministerAuditService {
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final StudentDefaultInfoMapper studentDefaultInfoMapper;
    private final RedissonClient redissonClient;
    private final RegisterMapper registerMapper;

    /**
     * @return 待审核用户列表
     */
    @Override
    public IPage<AuditUserPageRespDTO> listAuditRegister() {
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStatus, 0)
                .eq(RegisterDO::getDelFlag, 0)
                .orderByDesc(RegisterDO::getCreateTime);
        IPage<RegisterDO> resultPage=baseMapper.selectPage(new Page<>(1,10), queryWrapper);
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
        // 拿到的注册请求的实体
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
        StudentFrameworkDO studentFrameworkDO = StudentFrameworkDO.builder()
                .studentId(aDo.getStudentId())
                .name(aDo.getName())
                .majorNum(studentDefaultInfoDO.getMajorNum())
                .classNum(studentDefaultInfoDO.getClassNum())
                .enrollmentYear(studentDefaultInfoDO.getEnrollmentYear())
                .graduationYear(studentDefaultInfoDO.getGraduationYear())
                .phone(studentDefaultInfoDO.getPhone())
                .email(studentDefaultInfoDO.getEmail())
                .password(aDo.getPassword())
                .status(1)
                .registerToken(aDo.getRegisterToken())
                .build();
        studentFrameWorkMapper.insert(studentFrameworkDO);
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

    /**
     * 分页查询所有已经通过审核的合法用户
     *
     * @return 合法用户分页信息
     */
    @Override
    public IPage<AuditUserPageRespDTO> listAuditRegisterValid() {
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStatus, 1)
                .eq(RegisterDO::getDelFlag, 0)
                .orderByDesc(RegisterDO::getCreateTime);
        IPage<RegisterDO> resultPage=baseMapper.selectPage(new Page<>(1,10), queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each,AuditUserPageRespDTO.class));
    }

    /**
     * 分页查询所有已被拒绝的用户
     *
     * @return 被拒绝用户分页信息
     */
    @Override
    public IPage<AuditUserPageRespDTO> listAuditRegisterRefuse() {
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStatus, 2)
                .eq(RegisterDO::getDelFlag, 0)
                .orderByDesc(RegisterDO::getCreateTime);
        IPage<RegisterDO> resultPage=baseMapper.selectPage(new Page<>(1,10), queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each,AuditUserPageRespDTO.class));
    }

    /**
     * 根据学号ban学生
     *
     * @param requestParam 学号请求体
     */
    @Transactional
    @Override
    public void banStudentById(BanStudentReqDTO requestParam) {
        LambdaQueryWrapper<StudentFrameworkDO> queryWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, requestParam.getStudentId())
                .eq(StudentFrameworkDO::getStatus, 1)
                .eq(StudentFrameworkDO::getDelFlag, 0);
        StudentFrameworkDO studentFrameworkDO = studentFrameWorkMapper.selectOne(queryWrapper);

        LambdaQueryWrapper<RegisterDO> registerDOLambdaQueryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStudentId, requestParam.getStudentId())
                .eq(RegisterDO::getStatus, 1)
                .eq(RegisterDO::getDelFlag, 0);
        RegisterDO registerDO=registerMapper.selectOne(registerDOLambdaQueryWrapper);
        // todo: 这里判空逻辑可能有问题，需要修改
        if(Objects.isNull(studentFrameworkDO)||Objects.isNull(registerDO)){
            throw new ClientException("该学生信息不存在，请重新查询");
        }
        StudentFrameworkDO updateStudentFrameworkDO = StudentFrameworkDO.builder()
                .status(3)
                .build();
        RegisterDO updateRegisterDO=RegisterDO.builder()
                .status(3)
                .build();
        BanOrUnbanUpdate(requestParam, queryWrapper, registerDOLambdaQueryWrapper, updateStudentFrameworkDO, updateRegisterDO, LOCK_UPDATE_BAN_KEY);
    }

    /**
     * 根据学号unban学生
     *
     * @param requestParam 学号请求体
     */
    @Override
    public void unBanStudentById(BanStudentReqDTO requestParam) {
        LambdaQueryWrapper<StudentFrameworkDO> queryWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, requestParam.getStudentId())
                .eq(StudentFrameworkDO::getStatus, 3)
                .eq(StudentFrameworkDO::getDelFlag, 0);
        StudentFrameworkDO studentFrameworkDO = studentFrameWorkMapper.selectOne(queryWrapper);

        LambdaQueryWrapper<RegisterDO> registerDOLambdaQueryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStudentId, requestParam.getStudentId())
                .eq(RegisterDO::getStatus, 3)
                .eq(RegisterDO::getDelFlag, 0);
        RegisterDO registerDO=registerMapper.selectOne(registerDOLambdaQueryWrapper);

        if(Objects.isNull(studentFrameworkDO)||Objects.isNull(registerDO)){
            throw new ClientException("该学生信息不存在，请重新查询");
        }

        StudentFrameworkDO updateStudentFrameworkDO = StudentFrameworkDO.builder()
                .status(1)
                .build();
        RegisterDO updateRegisterDO=RegisterDO.builder()
                .status(1)
                .build();
        BanOrUnbanUpdate(requestParam, queryWrapper, registerDOLambdaQueryWrapper, updateStudentFrameworkDO, updateRegisterDO, LOCK_UPDATE_UNBAN_KEY);
    }

    /**
     * 更新禁用和解禁的用户状态
     * @param requestParam 学号请求体
     * @param queryWrapper 审核通过的学生信息查询参数
     * @param registerDOLambdaQueryWrapper 注册类学生信息查询参数
     * @param updateStudentFrameworkDO 更新学生信息实体
     * @param updateRegisterDO 更新注册信息实体
     * @param lockUpdateUnbanKey 读写锁key
     */
    private void BanOrUnbanUpdate(BanStudentReqDTO requestParam, LambdaQueryWrapper<StudentFrameworkDO> queryWrapper, LambdaQueryWrapper<RegisterDO> registerDOLambdaQueryWrapper, StudentFrameworkDO updateStudentFrameworkDO, RegisterDO updateRegisterDO, String lockUpdateUnbanKey) {
        RReadWriteLock readWriteLock=redissonClient.getReadWriteLock(String.format(lockUpdateUnbanKey,requestParam.getStudentId()));
        RLock rLock=readWriteLock.writeLock();
        if(!rLock.tryLock()){
            throw new ServiceException("该用户状态信息正在被其他管理员修改，请稍后再试...");
        }
        try{
            int updateStudentInfo = studentFrameWorkMapper.update(updateStudentFrameworkDO, queryWrapper);
            if(updateStudentInfo<1){
                throw new ClientException("禁用学生账户信息操作失败");
            }
            int updateRegister=registerMapper.update(updateRegisterDO,registerDOLambdaQueryWrapper);
            if(updateRegister<1){
                throw new ClientException("禁用学生账户信息操作失败");
            }
        }finally {
            rLock.unlock();
        }
    }
}
