package org.AList.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 * 依赖：
 * <dependency>
 *     <groupId>io.jsonwebtoken</groupId>
 *     <artifactId>jjwt</artifactId>
 *     <version>0.9.0</version>
 * </dependency>
 */
public class JwtUtils {

    // 默认有效期24小时
    private static final long DEFAULT_EXPIRATION_TIME = 24 * 60 * 60 * 1000L;

    // 签发者
    private static final String ISSUER = "ADMIN";

    private final String SECRET_KEY;

    // 直接通过构造函数接收密钥
    public JwtUtils(String SECRET_KEY) {
        if (SECRET_KEY == null || SECRET_KEY.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT密钥不能为空");
        }
        System.out.println("加载的密钥: " + SECRET_KEY);
        this.SECRET_KEY = SECRET_KEY;
    }

    /**
     * 生成UUID（去除横线）
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成JWT（使用默认有效期）
     * @param subject token中存放的数据（JSON格式）
     * @return JWT字符串
     */
    public String generateJWT(String subject) {
        return generateJWT(subject, DEFAULT_EXPIRATION_TIME);
    }

    /**
     * 生成JWT
     * @param subject token中存放的数据（JSON格式）
     * @param expirationTimeMillis 有效期（毫秒）
     * @return JWT字符串
     */
    public String generateJWT(String subject, long expirationTimeMillis) {
        return getJwtBuilder(subject, expirationTimeMillis, generateUUID()).compact();
    }

    /**
     * 生成指定ID的JWT
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

}