package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.UserException;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.domain.dao.entity.AdministerDO;
import org.AList.domain.dao.mapper.AdministerMapper;
import org.AList.domain.dto.req.AdminLoginReqDTO;
import org.AList.domain.dto.resp.AdminLoginRespDTO;
import org.AList.service.AdminToken.TokenPair;
import org.AList.service.AdminToken.TokenService;
import org.AList.service.AdministerService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;

/**
 * 管理员登录登出服务接口实现层
 */
@Service
@RequiredArgsConstructor
public class AdministerServiceImpl extends ServiceImpl<AdministerMapper, AdministerDO> implements AdministerService {
    private final StringRedisTemplate stringRedisTemplate;
    private final TokenService tokenService;
    /**
     * 管理员登录实现方法
     * @param requestParam 管理员登录请求实体
     * @return 管理员登录响应实体
     */
    @Override
    public AdminLoginRespDTO login(AdminLoginReqDTO requestParam) {
        LambdaQueryWrapper<AdministerDO> queryWrapper = Wrappers.lambdaQuery(AdministerDO.class)
                .eq(AdministerDO::getUsername, requestParam.getUsername())
                .eq(AdministerDO::getPassword, requestParam.getPassword())
                .eq(AdministerDO::getDelFlag, 0);
        AdministerDO administerDO=baseMapper.selectOne(queryWrapper);
        if(administerDO==null){
            throw new UserException(ADMIN_NOT_FOUND);                                                                   //"A0211", "管理员账户不存在"
        }
        // 检查是否已登录
        String accessKey = RedisKeyGenerator.genAdministerLoginAccess(requestParam.getUsername());
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(accessKey))) {
            throw new UserException(ADMIN_LOGGED_IN);                                                                   //"A0212", "管理员已登录"
        }
        TokenPair tokenPair = tokenService.generateAdministerTokens(administerDO);
        return new AdminLoginRespDTO(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
    }

    /**
     * 检查管理员登录状态
     * @param username 管理员用户名
     * @param token 访问令牌
     * @return true表示已登录，false表示未登录
     */
    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get(
                RedisKeyGenerator.genAdministerLoginAccess(username), token) != null;
    }

    /**
     * 管理员登出实现方法
     * @param username 管理员用户名
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     */
    @Override
    public void logout(String username, String accessToken, String refreshToken) {
        // 验证Access Token
        boolean accessTokenValid = checkLogin(username, accessToken);

        // 验证Refresh Token
        String refreshKey = RedisKeyGenerator.genAdministerRefresh(username);
        String storedRefreshToken = stringRedisTemplate.opsForValue().get(refreshKey);
        boolean refreshTokenValid = (storedRefreshToken != null && storedRefreshToken.equals(refreshToken));

        // 只要有一个Token有效，就执行登出操作
        if (accessTokenValid || refreshTokenValid) {
            // 删除Access Token
            String accessKey = RedisKeyGenerator.genAdministerLoginAccess(username);
            stringRedisTemplate.delete(accessKey);

            // 删除Refresh Token
            stringRedisTemplate.delete(refreshKey);

            // 删除用户信息
            String userInfoKey = RedisKeyGenerator.genAdministerLoginInfo(username);
            stringRedisTemplate.delete(userInfoKey);

            // 将Refresh Token加入黑名单
            if (refreshToken != null && !refreshToken.isEmpty()) {
                tokenService.blacklistToken(refreshToken);
            }

            return;
        }

        throw new UserException(USER_NOT_LOGGED);                                                                       // A0203：用户未登录或用户token不存在
    }
}
