package org.AList.service.AdminToken;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.UserException;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.domain.dao.entity.AdministerDO;
import org.AList.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.AList.common.convention.errorcode.BaseErrorCode.TOKEN_REFRESH_INVALID;
import static org.AList.common.convention.errorcode.BaseErrorCode.USER_NOT_LOGGED;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {  
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
      
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
     * 刷新管理员用户的Access Token
     */
    public String refreshAdministerAccessToken(String username, String refreshToken) throws Exception {
        logger.info("🔄 开始刷新管理员用户 Access Token，username: {}", username);

        // 检查 Refresh Token 是否在黑名单中
        if (isTokenBlacklisted(refreshToken)) {
            logger.warn("🚫 Refresh Token 已被拉黑，username: {}, refreshToken: {}", username, refreshToken);
            throw new UserException(USER_NOT_LOGGED); // "用户未登录或 Token 失效"
        }

        // 构建 Redis Key 并获取存储的 refreshToken
        String refreshKey = RedisKeyGenerator.genAdministerRefresh(username);
        logger.debug("🔍 查询 Redis 中的 Refresh Token，key: {}", refreshKey);

        String storedRefreshToken = stringRedisTemplate.opsForValue().get(refreshKey);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            logger.warn("❌ Redis 中未找到匹配的 Refresh Token，username: {}", username);
            throw new UserException(USER_NOT_LOGGED); // "用户未登录或 Token 失效"
        }

        // 检查 Access Token 是否仍然存在（即未过期）
        String accessKey = RedisKeyGenerator.genAdministerLoginAccess(username);
        logger.debug("⏳ 检查 Access Token 是否仍有效，key: {}", accessKey);

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(accessKey))) {
            logger.warn("⚠️ Access Token 未过期，无需刷新，username: {}", username);
            throw new UserException(TOKEN_REFRESH_INVALID); // "用户 accessToken 未过期，无需刷新"
        }

        // 解析 Refresh Token 获取用户信息
        logger.info("🔓 正在解析 Refresh Token 内容，username: {}", username);
        Claims claims;
        try {
            claims = jwtUtils.parseJWT(refreshToken);
        } catch (Exception e) {
            logger.error("🔴 解析 Refresh Token 失败，username: {}, error: {}", username, e.getMessage(), e);
            throw new UserException(USER_NOT_LOGGED); // 可根据异常类型细化错误码
        }

        String userJson = claims.getSubject();
        logger.debug("👤 提取到用户信息：{}", userJson);

        // 生成新的 Access Token
        logger.info("🆕 正在生成新的 Access Token，username: {}", username);
        String newAccessToken = jwtUtils.generateJWT(userJson, ACCESS_TOKEN_EXPIRATION);

        // 更新 Redis 中的 Access Token
        logger.debug("💾 正在更新 Redis 中的 Access Token，key: {}", accessKey);
        stringRedisTemplate.opsForHash().put(accessKey, newAccessToken, userJson);
        stringRedisTemplate.expire(accessKey, 30, TimeUnit.MINUTES);

        logger.info("✅ Access Token 刷新成功，username: {}", username);
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
  
