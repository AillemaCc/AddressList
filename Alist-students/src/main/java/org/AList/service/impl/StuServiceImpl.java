package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.annotation.Idempotent;
import org.AList.common.convention.errorcode.BaseErrorCode;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.ServiceException;
import org.AList.common.convention.exception.UserException;
import org.AList.common.enums.StudentChainMarkEnum;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.designpattern.chain.AbstractChainContext;
import org.AList.domain.dao.entity.LoginLogDO;
import org.AList.domain.dao.entity.RegisterDO;
import org.AList.domain.dao.entity.StudentFrameworkDO;
import org.AList.domain.dao.mapper.LoginLogMapper;
import org.AList.domain.dao.mapper.RegisterMapper;
import org.AList.domain.dao.mapper.StudentFrameWorkMapper;
import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.req.StuRegisterRemarkReqDTO;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;
import org.AList.domain.dto.resp.StuRegisterRemarkRespDTO;
import org.AList.service.StuService;
import org.AList.service.StuToken.TokenPair;
import org.AList.service.StuToken.TokenService;
import org.AList.utils.LinkUtil;
import org.AList.utils.SentinelUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;
/**
 * 学生服务接口实现类
 * <p>
 * 该类实现了学生相关的核心业务逻辑，包括登录、注册、登出等功能，
 * 使用了布隆过滤器、分布式锁等机制保证系统的高性能和安全性。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class StuServiceImpl extends ServiceImpl<StudentFrameWorkMapper, StudentFrameworkDO> implements StuService {
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final RegisterMapper registerMapper;
    private final LoginLogMapper loginLogMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final AbstractChainContext<StuRegisterReqDTO> abstractChainContext;
    private final TokenService tokenService;

    @Override
    public StuLoginRespDTO login(StuLoginReqDTO requestParam, HttpServletRequest request) {
        return SentinelUtils.executeWithSentinel("stu-login",
                () -> performLogin(requestParam, request),
                BaseErrorCode.FLOW_LIMIT_ERR,
                "登录请求过于频繁，请稍后再试");
    }
    /**
     * 学生登录接口实现
     * <p>
     * 处理学生登录请求，验证学号和密码，生成登录token并记录登录日志。
     * </p>
     *
     * @param requestParam 包含学生登录请求参数的DTO对象，必须有学号和密码
     * @param request HTTP请求对象，用于获取客户端信息
     * @return 包含登录token的响应DTO
     * @throws ClientException 如果学生不存在、密码错误或已登录
     * @see StuLoginReqDTO
     * @see StuLoginRespDTO
     */
    public StuLoginRespDTO performLogin(StuLoginReqDTO requestParam, HttpServletRequest request) {
        LambdaQueryWrapper<StudentFrameworkDO> queryWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, requestParam.getStudentId())
                .eq(StudentFrameworkDO::getPassword, requestParam.getPassword())
                .eq(StudentFrameworkDO::getDelFlag, 0)
                .eq(StudentFrameworkDO::getStatus, 1);
        StudentFrameworkDO studentFrameworkDO = studentFrameWorkMapper.selectOne(queryWrapper);
        if(studentFrameworkDO ==null){
            throw new UserException(USER_NOT_FOUND);                                                                     //A0201：用户不存在
        }
        String accessKey=RedisKeyGenerator.genStudentLoginAccess(requestParam.getStudentId());
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(accessKey))){
            throw new UserException(USER_LOGGED_IN);                                                                     //A0202：用户已登录
        }
        // 生成双Token
        TokenPair tokenPair = tokenService.generateStudentTokens(studentFrameworkDO);
        recordLoginLog(request, requestParam.getStudentId());
        return new StuLoginRespDTO(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
    }

    /**
     * 检查学生登录状态
     * <p>
     * 根据学号和token验证学生是否处于登录状态。
     * </p>
     *
     * @param studentId 学生学号
     * @param token 登录时生成的token
     * @return true表示已登录，false表示未登录
     */
    @Override
    public Boolean checkLogin(String studentId, String token) {
        return stringRedisTemplate.opsForHash().get(RedisKeyGenerator.genStudentLoginAccess(studentId),token)!=null;
    }

    /**
     * 学生登出接口
     * <p>
     * 处理学生登出请求，清除Redis中的登录状态。
     * </p>
     */
    @Override
    public void logout(String studentId, String accessToken, String refreshToken) {
        // 验证Access Token
        boolean accessTokenValid = checkLogin(studentId, accessToken);

        // 验证Refresh Token
        String refreshKey = RedisKeyGenerator.genStudentLoginRefresh(studentId);
        String storedRefreshToken = stringRedisTemplate.opsForValue().get(refreshKey);
        boolean refreshTokenValid = (storedRefreshToken != null && storedRefreshToken.equals(refreshToken));

        // 只要有一个Token有效，就执行登出操作
        if (accessTokenValid || refreshTokenValid) {
            // 删除Access Token
            String accessKey = RedisKeyGenerator.genStudentLoginAccess(studentId);
            stringRedisTemplate.delete(accessKey);

            // 删除Refresh Token
            stringRedisTemplate.delete(refreshKey);

            // 删除用户信息
            String studentInfoKey = RedisKeyGenerator.genStudentLoginInfo(studentId);
            stringRedisTemplate.delete(studentInfoKey);

            // 将Refresh Token加入黑名单
            if (refreshToken != null && !refreshToken.isEmpty()) {
                tokenService.blacklistToken(refreshToken);
            }

            return;
        }

        throw new UserException(USER_NOT_LOGGED);                                                                          //A0203：用户未登录或用户token不存在
    }

    /**
     * 学生注册接口
     * <p>
     * 处理学生注册请求，使用布隆过滤器和分布式锁保证注册过程的正确性和安全性。
     * 注册后需要管理员审核才能生效。
     * </p>
     *
     * @param requestParam 包含学生注册信息的DTO对象
     * @return 注册token，用于后续查询注册状态
     * @throws ClientException 如果学号不存在、重复注册或保存失败
     * @see StuRegisterReqDTO
     */
    @Idempotent(prefix = "student:register", key = "#requestParam.studentId")
    @Override
    public String register(StuRegisterReqDTO requestParam) {
        abstractChainContext.handler(StudentChainMarkEnum.STUDENT_REGISTER.name(), requestParam);
        // 排除了误判的情况，创建分布式锁，防止并发情况。尽管在我们的场景当中，并发情况是很难出现的，但这种边界情况仍然需要考虑。
        RLock rLock=redissonClient.getLock(RedisKeyGenerator.genStudentRegisterLockKey(requestParam.getStudentId()));
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
                        throw new UserException(REGISTER_FAIL);                                                         //A0100：用户注册失败
                    }
                    // 插入成功之后，向注册的用户返回一个key，他可以用这个key查询自己的注册单审核的状态
                    // 插入成功之后 管理员端审核
                    return uuid;
                }catch (DuplicateKeyException e){
                    // 防止重复注册
                    throw new UserException(ADDR_BOOK_EXIST);                                                           //A0103：通讯录已存在
                }

            }else{
                // 获取不到学号的分布式锁，自然是重复注册了
                throw new UserException(ADDR_BOOK_EXIST);                                                           //A0103：通讯录已存在
            }

        }
        finally {
            rLock.unlock();
        }
    }

    /**
     * 查询注册结果
     * <p>
     * 学生使用注册时获得的token查询注册审核结果和备注信息。
     * </p>
     *
     * @param requestParam 包含学号和注册token的请求DTO
     * @return 包含注册状态和备注信息的响应DTO
     * @throws ClientException 如果注册记录不存在
     * @see StuRegisterRemarkReqDTO
     * @see StuRegisterRemarkRespDTO
     */
    @Override
    public StuRegisterRemarkRespDTO getRemark(StuRegisterRemarkReqDTO requestParam) {
        String studentId=requestParam.getStudentId();
        String password =requestParam.getPassword();
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStudentId, studentId)
                .eq(RegisterDO::getPassword, password)
                .eq(RegisterDO::getDelFlag, 0);
        RegisterDO registerDO = registerMapper.selectOne(queryWrapper);
        if(Objects.isNull(registerDO)){
            throw new ServiceException(REG_REQ_NOT_FOUND);                                                               //C0361：处理的注册请求不存在
        }
        return StuRegisterRemarkRespDTO.builder()
                .studentId(studentId)
                .name(registerDO.getName())
                .remark(registerDO.getRemark())
                .status(registerDO.getStatus())
                .build();
    }

    /**
     * 记录学生登录日志
     * <p>
     * 记录学生登录的IP、操作系统、浏览器等信息，并统计登录次数。
     * 如果是首次登录，创建新记录；否则更新现有记录。
     * </p>
     *
     * @param request HTTP请求对象，用于获取客户端信息
     * @param studentId 登录的学生学号
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