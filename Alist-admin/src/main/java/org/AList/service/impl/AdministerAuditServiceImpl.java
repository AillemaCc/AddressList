package org.AList.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.annotation.Idempotent;
import org.AList.common.convention.exception.UserException;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.ServiceException;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.domain.dao.entity.RegisterDO;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.domain.dao.entity.StudentFrameworkDO;
import org.AList.domain.dao.mapper.RegisterMapper;
import org.AList.domain.dao.mapper.StudentDefaultInfoMapper;
import org.AList.domain.dao.mapper.StudentFrameWorkMapper;
import org.AList.domain.dto.req.AccpetRegistrationReqDTO;
import org.AList.domain.dto.req.AuditListReqDTO;
import org.AList.domain.dto.req.BanStudentReqDTO;
import org.AList.domain.dto.req.RefuseRegistrationReqDTO;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;
import org.AList.service.AdministerAuditService;
import org.AList.service.idempotent.IdempotencyService;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.AList.common.convention.errorcode.BaseErrorCode.*;

import java.util.Objects;

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
    private final IdempotencyService idempotencyService;

    /**
     * @return 待审核用户列表
     */
    @Override
    public IPage<AuditUserPageRespDTO> listAuditRegister(AuditListReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStatus, 0)
                .eq(RegisterDO::getDelFlag, 0)
                .orderByDesc(RegisterDO::getCreateTime);
        IPage<RegisterDO> resultPage=baseMapper.selectPage(new Page<>(current,size), queryWrapper);
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
            throw new ClientException(REVIEW_DATA_ABNORMAL);                                                            //"B0222", "管理员审核失败,数据异常"
        }
        RegisterDO registerDO=RegisterDO.builder()
                .status(1)
                .build();
        int update = baseMapper.update(registerDO, updateWrapper);
        if(update<1){
            throw new ClientException(REVIEW_DATA_ABNORMAL);                                                            //"B0222", "管理员审核失败,数据异常"
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
            throw new ServiceException(REG_REQ_NOT_FOUND);                                                              //"C0361", "处理的注册请求不存在"
        }
        else if(registerDO.getStatus()==1){
            throw new ServiceException(REG_REQ_APPROVED);                                                               //"C0362", "处理的注册请求已通过"
        }
        else if(registerDO.getStatus()==2){
            throw new ServiceException(REG_REQ_REJECTED);                                                               //"C0363", "处理的注册请求已拒绝"
        }
        else if(registerDO.getDelFlag()==1){
            throw new ServiceException(REG_REQ_DELETED);                                                                //"C0364", "处理的注册请求已删除"
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
            throw new ClientException(REVIEW_DATA_MISSING);                                                             //"B0223", "管理员审核失败，数据缺失"
        }
        RegisterDO registerDO=RegisterDO.builder()
                .remark(requestParam.getRemark())
                .status(2)
                .build();
        int update = baseMapper.update(registerDO, updateWrapper);
        if(update<1){
            throw new ClientException(REVIEW_DATA_UPDATE_FAIL);                                                           //"B0224", "管理员审核失败，数据未更新"
        }
    }

    /**
     * 分页查询所有已经通过审核的合法用户
     *
     * @return 合法用户分页信息
     */
    @Override
    public IPage<AuditUserPageRespDTO> listAuditRegisterValid(AuditListReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStatus, 1)
                .eq(RegisterDO::getDelFlag, 0)
                .orderByDesc(RegisterDO::getCreateTime);
        IPage<RegisterDO> resultPage=baseMapper.selectPage(new Page<>(current,size), queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each,AuditUserPageRespDTO.class));
    }

    /**
     * 分页查询所有已被拒绝的用户
     *
     * @return 被拒绝用户分页信息
     */
    @Override
    public IPage<AuditUserPageRespDTO> listAuditRegisterRefuse(AuditListReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStatus, 2)
                .eq(RegisterDO::getDelFlag, 0)
                .orderByDesc(RegisterDO::getCreateTime);
        IPage<RegisterDO> resultPage=baseMapper.selectPage(new Page<>(current,size), queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each,AuditUserPageRespDTO.class));
    }

    /**
     * 根据学号ban学生
     *
     * @param requestParam 学号请求体
     */
    @Idempotent(prefix = "admin:ban", key = "#requestParam.studentId")
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
                throw new ServiceException(ADDR_NOT_FOUND);                                                             //C0351：处理的通讯录记录不存在
        }
        StudentFrameworkDO updateStudentFrameworkDO = StudentFrameworkDO.builder()
                .status(3)
                .build();
        RegisterDO updateRegisterDO=RegisterDO.builder()
                .status(3)
                .build();
        BanOrUnbanUpdate(queryWrapper, registerDOLambdaQueryWrapper, updateStudentFrameworkDO, updateRegisterDO, RedisKeyGenerator.genAdminBanLockKey(requestParam.getStudentId()));
        idempotencyService.removeIdempotencyKey("admin:ban:"+requestParam.getStudentId());
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
                throw new ServiceException(ADDR_NOT_FOUND);                                                             //C0351：处理的通讯录记录不存在
        }

        StudentFrameworkDO updateStudentFrameworkDO = StudentFrameworkDO.builder()
                .status(1)
                .build();
        RegisterDO updateRegisterDO=RegisterDO.builder()
                .status(1)
                .build();
        BanOrUnbanUpdate(queryWrapper, registerDOLambdaQueryWrapper, updateStudentFrameworkDO, updateRegisterDO, RedisKeyGenerator.genAdminUnlockKey(requestParam.getStudentId()));
    }

    /**
     * 更新禁用和解禁的用户状态
     * @param queryWrapper                 审核通过的学生信息查询参数
     * @param registerDOLambdaQueryWrapper 注册类学生信息查询参数
     * @param updateStudentFrameworkDO     更新学生信息实体
     * @param updateRegisterDO             更新注册信息实体
     */
    private void BanOrUnbanUpdate(LambdaQueryWrapper<StudentFrameworkDO> queryWrapper, LambdaQueryWrapper<RegisterDO> registerDOLambdaQueryWrapper, StudentFrameworkDO updateStudentFrameworkDO, RegisterDO updateRegisterDO, String lockKey) {
        RReadWriteLock readWriteLock=redissonClient.getReadWriteLock(lockKey);
        RLock rLock=readWriteLock.writeLock();
        if(!rLock.tryLock()){
            throw new ClientException(PERM_EDIT_USER_CONFLICT);                                                         //"B0322", "系统缺乏权限修改正在被其他管理员修改的用户信息"
        }
        try{
            int updateStudentInfo = studentFrameWorkMapper.update(updateStudentFrameworkDO, queryWrapper);
            if(updateStudentInfo<1){
                throw new ClientException(ADMIN_BAN_USER_FAIL);                                                         //"B0221", "管理员禁用用户失败"
            }
            int updateRegister=registerMapper.update(updateRegisterDO,registerDOLambdaQueryWrapper);
            if(updateRegister<1){
                throw new ClientException(ADMIN_BAN_USER_FAIL);                                                         //"B0221", "管理员禁用用户失败"
            }
        }finally {
            rLock.unlock();
        }
    }
}
