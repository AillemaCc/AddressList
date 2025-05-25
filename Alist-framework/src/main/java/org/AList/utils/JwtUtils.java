package org.AList.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.monitor.JwtKeyRotationMonitor;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * JWT工具类 - 支持多密钥验证和密钥轮换
 */
@Slf4j

public class JwtUtils {

    // 默认Access Token有效期 - 30分钟
    private static final long DEFAULT_ACCESS_TOKEN_EXPIRATION = 30 * 60 * 1000L;

    // 默认Refresh Token有效期 - 7天
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    // 签发者
    private static final String ISSUER = "ADMIN";

    // Token类型声明
    private static final String TOKEN_TYPE_KEY = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final String KEY_VERSION_CLAIM = "keyVersion";

    private final String currentSecret;
    private final List<String> historicalSecrets;
    private final Long keyRotationGracePeriod;
    private final JwtKeyRotationMonitor monitor; // 添加 JwtKeyRotationMonitor 成员变量

    // 兼容原有构造函数
    public JwtUtils(String SECRET_KEY) {
        this(SECRET_KEY, new ArrayList<>(), 24 * 60 * 60 * 1000L, null); // 默认 monitor 为 null
    }

    // 多密钥构造函数 (不带 monitor)
    public JwtUtils(String currentSecret, List<String> historicalSecrets, Long keyRotationGracePeriod) {
        this(currentSecret, historicalSecrets, keyRotationGracePeriod, null); // 默认 monitor 为 null
    }

    // 完整构造函数，包含 monitor 注入
    public JwtUtils(String currentSecret, List<String> historicalSecrets, Long keyRotationGracePeriod, JwtKeyRotationMonitor monitor) {
        if (currentSecret == null || currentSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT密钥不能为空");
        }
        this.currentSecret = currentSecret;
        this.historicalSecrets = historicalSecrets != null ? historicalSecrets : new ArrayList<>();
        this.keyRotationGracePeriod = keyRotationGracePeriod != null ? keyRotationGracePeriod : 24 * 60 * 60 * 1000L;
        this.monitor = monitor; // 注入 monitor

        log.info("JWT工具类初始化完成，当前密钥长度: {}, 历史密钥数量: {}",
                currentSecret.length(), this.historicalSecrets.size());
    }

    /**
     * 生成UUID（去除横线）
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成Access Token（使用默认有效期）
     * @param subject token中存放的数据（JSON格式）
     * @return JWT字符串
     */
    public String generateAccessToken(String subject) {
        return generateAccessToken(subject, DEFAULT_ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * 生成Access Token
     * @param subject token中存放的数据（JSON格式）
     * @param expirationTimeMillis 有效期（毫秒）
     * @return JWT字符串
     */
    public String generateAccessToken(String subject, long expirationTimeMillis) {
        return getJwtBuilder(subject, expirationTimeMillis, generateUUID())
                .claim(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE)
                .compact();
    }

    /**
     * 生成Refresh Token（使用默认有效期）
     * @param subject token中存放的数据（JSON格式）
     * @return JWT字符串
     */
    public String generateRefreshToken(String subject) {
        return generateRefreshToken(subject, DEFAULT_REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * 生成Refresh Token
     * @param subject token中存放的数据（JSON格式）
     * @param expirationTimeMillis 有效期（毫秒）
     * @return JWT字符串
     */
    public String generateRefreshToken(String subject, long expirationTimeMillis) {
        return getJwtBuilder(subject, expirationTimeMillis, generateUUID())
                .claim(TOKEN_TYPE_KEY, REFRESH_TOKEN_TYPE)
                .compact();
    }

    /**
     * 生成Token对
     * @param subject token中存放的数据（JSON格式）
     * @return TokenPair对象，包含accessToken和refreshToken
     */
    public TokenPair generateTokenPair(String subject) {
        String accessToken = generateAccessToken(subject);
        String refreshToken = generateRefreshToken(subject);
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * 生成JWT（使用默认有效期）- 兼容旧方法
     * @param subject token中存放的数据（JSON格式）
     * @return JWT字符串
     */
    public String generateJWT(String subject) {
        return generateJWT(subject, DEFAULT_ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * 生成JWT - 兼容旧方法
     * @param subject token中存放的数据（JSON格式）
     * @param expirationTimeMillis 有效期（毫秒）
     * @return JWT字符串
     */
    public String generateJWT(String subject, long expirationTimeMillis) {
        return getJwtBuilder(subject, expirationTimeMillis, generateUUID()).compact();
    }

    /**
     * 生成指定ID的JWT - 兼容旧方法
     * @param id JWT ID
     * @param subject token中存放的数据（JSON格式）
     * @param expirationTimeMillis 有效期（毫秒）
     * @return JWT字符串
     */
    public String generateJWT(String id, String subject, long expirationTimeMillis) {
        return getJwtBuilder(subject, expirationTimeMillis, id).compact();
    }

    /**
     * 构建JWT Builder - 使用当前密钥并添加密钥版本标识
     */
    private JwtBuilder getJwtBuilder(String subject, long expirationTimeMillis, String id) {
        SecretKey secretKey = generateSecretKey(currentSecret);
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + expirationTimeMillis);

        return Jwts.builder()
                .setId(id)                     // 唯一ID
                .setSubject(subject)           // 主题（JSON数据）
                .setIssuer(ISSUER)             // 签发者
                .setIssuedAt(now)              // 签发时间
                .setExpiration(expirationDate) // 过期时间
                .claim(KEY_VERSION_CLAIM, getCurrentKeyVersion()) // 添加密钥版本标识
                .signWith(SignatureAlgorithm.HS256, secretKey); // 使用HS256算法签名
    }

    /**
     * 获取当前密钥版本（基于密钥内容的哈希）
     */
    private String getCurrentKeyVersion() {
        return String.valueOf(currentSecret.hashCode());
    }

    /**
     * 生成加密后的密钥
     */
    private SecretKey generateSecretKey(String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    /**
     * 兼容原有方法的密钥生成
     */
    private SecretKey generateSecretKey() {
        return generateSecretKey(currentSecret);
    }

    /**
     * 多密钥验证解析JWT
     */
    public Claims parseJWT(String jwt) throws Exception {
        Exception lastException = null;

        // 首先尝试使用当前密钥解析
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(generateSecretKey(currentSecret))
                    .parseClaimsJws(jwt)
                    .getBody();
            log.debug("使用当前密钥成功解析JWT");

            // 记录当前密钥使用
            if (monitor != null) {
                monitor.recordCurrentKeyUsage();
            }

            return claims;
        } catch (Exception e) {
            lastException = e;
            log.debug("当前密钥解析失败，尝试历史密钥: {}", e.getMessage());
        }

        // 如果当前密钥失败，尝试历史密钥
        for (int i = 0; i < historicalSecrets.size(); i++) {
            try {
                String historicalSecret = historicalSecrets.get(i);
                Claims claims = Jwts.parser()
                        .setSigningKey(generateSecretKey(historicalSecret))
                        .parseClaimsJws(jwt)
                        .getBody();

                // 检查Token是否在宽限期内
                if (isWithinGracePeriod(claims)) {
                    log.info("使用历史密钥[{}]成功解析JWT，建议客户端刷新Token", i);

                    // 记录历史密钥使用
                    if (monitor != null) {
                        monitor.recordHistoricalKeyUsage();
                    }

                    return claims;
                } else {
                    log.warn("历史密钥[{}]解析的Token已超过宽限期", i);
                }
            } catch (Exception e) {
                log.debug("历史密钥[{}]解析失败: {}", i, e.getMessage());
                lastException = e;
            }
        }

        // 所有密钥都失败，抛出最后一个异常
        throw new Exception("所有密钥验证失败", lastException);
    }

    /**
     * 检查Token是否在宽限期内
     */
    private boolean isWithinGracePeriod(Claims claims) {
        Date issuedAt = claims.getIssuedAt();
        if (issuedAt == null) {
            return false;
        }

        long tokenAge = System.currentTimeMillis() - issuedAt.getTime();
        return tokenAge <= keyRotationGracePeriod;
    }

    /**
     * 验证Token并返回验证结果
     */
    public TokenValidationResult validateToken(String jwt) {
        try {
            Claims claims = parseJWT(jwt);
            String keyVersion = claims.get(KEY_VERSION_CLAIM, String.class);
            boolean isCurrentKey = getCurrentKeyVersion().equals(keyVersion);

            return new TokenValidationResult(true, claims, isCurrentKey, null);
        } catch (Exception e) {
            return new TokenValidationResult(false, null, false, e.getMessage());
        }
    }

    /**
     * 检查是否为Access Token
     * @param claims JWT Claims
     * @return 是否为Access Token
     */
    public boolean isAccessToken(Claims claims) {
        return ACCESS_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_KEY));
    }

    /**
     * 检查是否为Refresh Token
     * @param claims JWT Claims
     * @return 是否为Refresh Token
     */
    public boolean isRefreshToken(Claims claims) {
        return REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_KEY));
    }

    /**
     * Token验证结果类
     */
    @Getter
    public static class TokenValidationResult {
        private final boolean valid;
        private final Claims claims;
        private final boolean usedCurrentKey;
        private final String errorMessage;

        public TokenValidationResult(boolean valid, Claims claims, boolean usedCurrentKey, String errorMessage) {
            this.valid = valid;
            this.claims = claims;
            this.usedCurrentKey = usedCurrentKey;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Token对类，包含Access Token和Refresh Token
     */
    @Getter
    public static class TokenPair {
        private final String accessToken;
        private final String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}