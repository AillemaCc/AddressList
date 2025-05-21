package org.AList.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类 - 支持双Token认证
 */
@Component
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

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // 添加无参构造函数
    public JwtUtils() {
    }

    // 在初始化后验证密钥
    @PostConstruct
    public void init() {
        if (SECRET_KEY == null || SECRET_KEY.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT密钥不能为空");
        }
    }

    // 直接通过构造函数接收密钥
    public JwtUtils(String SECRET_KEY) {
        if (SECRET_KEY == null || SECRET_KEY.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT密钥不能为空");
        }
        this.SECRET_KEY = SECRET_KEY;
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
     * 构建JWT Builder
     */
    private JwtBuilder getJwtBuilder(String subject, long expirationTimeMillis, String id) {
        SecretKey secretKey = generateSecretKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + expirationTimeMillis);

        return Jwts.builder()
                .setId(id)                     // 唯一ID
                .setSubject(subject)           // 主题（JSON数据）
                .setIssuer(ISSUER)             // 签发者
                .setIssuedAt(now)              // 签发时间
                .setExpiration(expirationDate) // 过期时间
                .signWith(SignatureAlgorithm.HS256, secretKey); // 使用HS256算法签名
    }

    /**
     * 生成加密后的密钥
     */
    private SecretKey generateSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    /**
     * 解析JWT
     * @param jwt JWT字符串
     * @return Claims对象
     * @throws Exception 解析异常
     */
    public Claims parseJWT(String jwt) throws Exception {
        return Jwts.parser()
                .setSigningKey(generateSecretKey())
                .parseClaimsJws(jwt)
                .getBody();
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