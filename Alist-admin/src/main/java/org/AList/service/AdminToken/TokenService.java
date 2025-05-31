package org.AList.service.AdminToken;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.UserException;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.domain.dao.entity.AdministerDO;
import org.AList.utils.JwtUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.AList.common.convention.errorcode.BaseErrorCode.TOKEN_REFRESH_INVALID;
import static org.AList.common.convention.errorcode.BaseErrorCode.USER_NOT_LOGGED;

@Service
@RequiredArgsConstructor
public class TokenService {  
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;
      
    // Access Token有效期 - 30分钟  
    private static final long ACCESS_TOKEN_EXPIRATION = 30 * 60 * 1000L;  
      
    // Refresh Token有效期 - 7天  
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;  
      
    /**  
     * 生成学生用户的双Token  
     */  
    public TokenPair generateAdministerTokens(AdministerDO administer) {
        // 创建最小化的用户信息对象
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("username", administer.getUsername());

        // 转换为JSON
        String minimalUserJson = JSON.toJSONString(tokenData);

        // 生成Token
        String accessToken = jwtUtils.generateJWT(minimalUserJson, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtUtils.generateJWT(minimalUserJson, REFRESH_TOKEN_EXPIRATION);

        // 在Redis中存储完整用户信息
        String userInfoKey = RedisKeyGenerator.genAdministerLoginInfo(administer.getUsername());
        AdministerDO userInfo = AdministerDO.builder()
                .username(administer.getUsername())
                .build();
        stringRedisTemplate.opsForValue().set(userInfoKey, JSON.toJSONString(userInfo));
        stringRedisTemplate.expire(userInfoKey, 7, TimeUnit.DAYS);

        // 存储Token关联
        String accessKey = RedisKeyGenerator.genAdministerLoginAccess(administer.getUsername());
        String refreshKey = RedisKeyGenerator.genAdministerRefresh(administer.getUsername());

        stringRedisTemplate.opsForHash().put(accessKey, accessToken, minimalUserJson);
        stringRedisTemplate.expire(accessKey, 30, TimeUnit.MINUTES);

        stringRedisTemplate.opsForValue().set(refreshKey, refreshToken);
        stringRedisTemplate.expire(refreshKey, 7, TimeUnit.DAYS);

        return new TokenPair(accessToken, refreshToken);
    }  
      
    /**  
     * 刷新学生用户的Access Token  
     */  
    public String refreshAdministerAccessToken(String username, String refreshToken) throws Exception {
        // 验证Refresh Token
        // 检查Token是否在黑名单中
        if (isTokenBlacklisted(refreshToken)) {
            throw new UserException(USER_NOT_LOGGED);
        }

        String refreshKey = RedisKeyGenerator.genAdministerRefresh(username);
        String storedRefreshToken = stringRedisTemplate.opsForValue().get(refreshKey);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new UserException(USER_NOT_LOGGED);
        }

        // 检查Access Token是否已存在且未过期
        String accessKey = RedisKeyGenerator.genAdministerLoginAccess(username);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(accessKey))) {
            // Access Token仍然存在，说明未过期
            throw new UserException(TOKEN_REFRESH_INVALID);                                                             //"A0204", "用户accessToken未过期,无需刷新"
        }

        // 解析Refresh Token获取用户信息
        Claims claims = jwtUtils.parseJWT(refreshToken);
        String userJson = claims.getSubject();

        // 生成新的Access Token
        String newAccessToken = jwtUtils.generateJWT(userJson, ACCESS_TOKEN_EXPIRATION);

        // 更新Redis中的Access Token
        stringRedisTemplate.opsForHash().put(accessKey, newAccessToken, userJson);
        stringRedisTemplate.expire(accessKey, 30, TimeUnit.MINUTES);

        return newAccessToken;
    }

    /**
     * 检查Token是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("token:blacklist:" + token));
    }

    /**
     * 将Token加入黑名单
     */
    public void blacklistToken(String token) {
        try {
            Claims claims = jwtUtils.parseJWT(token);
            long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (expirationTime > 0) {
                stringRedisTemplate.opsForValue().set(
                        "token:blacklist:" + token,
                        "1",
                        expirationTime,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            // Token解析失败，可能已过期，忽略异常
        }
    }

}  
  
