package org.AList.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.constant.RedisCacheConstant;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.LoginLogDO;
import org.AList.domain.dao.entity.RegisterDO;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.domain.dao.mapper.LoginLogMapper;
import org.AList.domain.dao.mapper.RegisterMapper;
import org.AList.domain.dao.mapper.StudentDefaultInfoMapper;
import org.AList.domain.dao.mapper.StudentMapper;
import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.req.StuRegisterRemarkReqDTO;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;
import org.AList.domain.dto.resp.StuRegisterRemarkRespDTO;
import org.AList.service.StuService;
import org.AList.service.bloom.StudentIdBloomFilterService;
import org.AList.utils.LinkUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.AList.common.enums.UserErrorCodeEnum.*;

/**
 * 学生客户端接口服务实现层
 */
@Service
@RequiredArgsConstructor
public class StuServiceImpl extends ServiceImpl<StudentMapper,StudentDO> implements StuService {
    private final StudentMapper studentMapper;
    private final RegisterMapper registerMapper;
    private final LoginLogMapper loginLogMapper;
    private final StudentDefaultInfoMapper studentDefaultInfoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final StudentIdBloomFilterService studentIdBloomFilterService;
    private final RedissonClient redissonClient;
    /**
     * 用户登录接口实现类
     *
     * @param requestParam 用户登录请求实体
     * @param request
     * @return 用户登录响应实体--token
     */
    @Override
    public StuLoginRespDTO login(StuLoginReqDTO requestParam, HttpServletRequest request) {
        LambdaQueryWrapper<StudentDO> queryWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getStudentId, requestParam.getStudentId())
                .eq(StudentDO::getPassword, requestParam.getPassword())
                .eq(StudentDO::getDelFlag, 0)
                .eq(StudentDO::getStatus, 1);
        StudentDO studentDO=studentMapper.selectOne(queryWrapper);
        if(studentDO==null){
            throw new ClientException("学生不存在");
        }
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey("login:student:"+requestParam.getStudentId()))){
            throw new ClientException("学生已登录");
        }
        String uuid= UUID.randomUUID().toString();
        // 生成的uuid作为用户登录信息传入redis
        // 更清晰的键设计（使用冒号分隔）
        String redisKey = "login:student:" + requestParam.getStudentId();
        stringRedisTemplate.opsForHash().put(redisKey, uuid, JSON.toJSONString(studentDO));
        stringRedisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
        recordLoginLog(request, requestParam.getStudentId());
        return new StuLoginRespDTO(uuid);
    }

    /**
     * 检查用户是否登录
     * @param studentId 学号
     * @param token 登录生成的token
     * @return 是否登录的结果
     */
    @Override
    public Boolean checkLogin(String studentId, String token) {
        return stringRedisTemplate.opsForHash().get("login:student:"+studentId,token)!=null;
    }

    /**
     * 用户登出
     * @param studentId 学号
     * @param token 用户登录产生的token
     */
    @Override
    public void logout(String studentId, String token) {
        if(checkLogin(studentId,token)){
            stringRedisTemplate.delete("login:student:"+studentId);
            return;
        }
        throw new ClientException("用户token不存在或者用户未登录");
    }

    /**
     * 用户注册接口
     *
     * @param requestParam 用户注册请求实体
     * @return 用户注册返回的唯一key
     */
    @Override
    public String register(StuRegisterReqDTO requestParam) {
        // 缓存全量学号的布隆过滤器提供快速错误机制 防止缓存穿透
        if(!studentIdBloomFilterService.contain(requestParam.getStudentId())){
            throw new ClientException(USER_NULL);
        }
        // 布隆过滤器存在误判率，被误判为存在的请求，需要再去数据库查询一下。这时候需要查询的仍然是学籍库，也就是全量默认数据的查询
        LambdaQueryWrapper<StudentDefaultInfoDO> queryWrapper = Wrappers.lambdaQuery(StudentDefaultInfoDO.class)
                .eq(StudentDefaultInfoDO::getStudentId, requestParam.getStudentId())
                .eq(StudentDefaultInfoDO::getName,requestParam.getName())
                .eq(StudentDefaultInfoDO::getDelFlag, 0);
        if(Objects.isNull(studentDefaultInfoMapper.selectOne(queryWrapper))){
            throw new ClientException(USER_NULL);
        }
        // 排除了误判的情况，创建分布式锁，防止并发情况。尽管在我们的场景当中，并发情况是很难出现的，但这种边界情况仍然需要考虑。
        RLock rLock=redissonClient.getLock(RedisCacheConstant.LOCK_STUDENT_REGISTER_KEY+requestParam.getStudentId());
        // 无论临界区代码执行成功还是抛出异常，锁最终都会被释放
        try{
            if(rLock.tryLock()){
                // 获取到锁之后才执行业务逻辑
                try{
                    String uuid= UUID.randomUUID().toString();
                    // 构建注册实体
                    RegisterDO registerDO=RegisterDO.builder()
                            .studentId(requestParam.getStudentId())
                            .name(requestParam.getName())
                            .phone(requestParam.getPhone())
                            .email(requestParam.getEmail())
                            .password(requestParam.getPassword())
                            .status(0)
                            .registerToken(uuid)
                            .build();

                    // 注册实体新增到创建的注册表单当中
                    int insert= registerMapper.insert(registerDO);
                    if (insert < 1) {
                        throw new ClientException(USER_SAVE_ERROR);
                    }
                    // 插入成功之后，向注册的用户返回一个key，他可以用这个key查询自己的注册单审核的状态
                    // 插入成功之后 管理员端审核
                    return uuid;
                }catch (DuplicateKeyException e){
                    // 防止重复注册
                    throw new ClientException(USER_EXIST);
                }

            }else{
                // 获取不到学号的分布式锁，自然是重复注册了
                throw new ClientException(USER_EXIST);
            }

        }
        finally {
            rLock.unlock();
        }
    }

    /**
     * 用户查询注册结果
     *
     * @param requestParam 用户查询注册结果请求体
     * @return 用户查询注册结果响应体
     */
    @Override
    public StuRegisterRemarkRespDTO getReamrk(StuRegisterRemarkReqDTO requestParam) {
        String studentId=requestParam.getStudentId();
        String registerToken =requestParam.getRegisterToken();
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStudentId, studentId)
                .eq(RegisterDO::getRegisterToken, registerToken)
                .eq(RegisterDO::getDelFlag, 0);
        RegisterDO registerDO = registerMapper.selectOne(queryWrapper);
        if(Objects.isNull(registerDO)){
            throw new ClientException("注册记录不存在，请检查您输入的学号和token是否正确");
        }
        return StuRegisterRemarkRespDTO.builder()
                .studentId(studentId)
                .name(registerDO.getName())
                .registerToken(registerToken)
                .remark(registerDO.getRemark())
                .status(registerDO.getStatus())
                .build();
    }

    /**
     * 记录登录日志
     * @param request 网络请求
     * @param studentId 登录的学号
     */
    private void recordLoginLog(HttpServletRequest request, String studentId) {
        // 获取客户端信息
        String ip = LinkUtil.getActualIp(request);
        String os = LinkUtil.getOs(request);
        String browser = LinkUtil.getBrowser(request);

        // 查询最后一条记录
        LoginLogDO lastLog = loginLogMapper.selectLastLoginByStudentId(studentId);

        if (lastLog == null) {
            // 首次登录
            LoginLogDO newLog = LoginLogDO.builder()
                    .studentId(studentId)
                    .ip(ip)
                    .os(os)
                    .browser(browser)
                    .frequency(1)
                    .build();
            loginLogMapper.insert(newLog);
        } else {
            // 非首次登录
            lastLog.setFrequency(lastLog.getFrequency() + 1);
            lastLog.setIp(ip);
            lastLog.setOs(os);
            lastLog.setBrowser(browser);

            // 使用自定义的更新方法
            loginLogMapper.updateLoginLog(lastLog);
        }
    }
}
