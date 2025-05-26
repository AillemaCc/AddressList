import io.jsonwebtoken.Claims;  
import org.AList.common.monitor.JwtKeyRotationMonitor;  
import org.AList.utils.JwtUtils;  
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;  
import org.mockito.Mock;  
import org.mockito.MockitoAnnotations;  
  
import java.util.Arrays;  
import java.util.Date;  
import java.util.List;  
  
import static org.junit.jupiter.api.Assertions.*;  
import static org.mockito.Mockito.*;  
  
/**  
 * JWT历史密钥功能完整测试类  
 */  
public class JwtHistoricalKeyTest {

    // 测试用的密钥（Base64编码）  
    private static final String CURRENT_SECRET = "VGhpcyBpcyBhIGN1cnJlbnQgc2VjcmV0IGtleSEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEh";
    private static final String HISTORICAL_SECRET_1 = "VGhpcyBpcyBoaXN0b3JpY2FsIHNlY3JldCBrZXkgMSEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEh";
    private static final String HISTORICAL_SECRET_2 = "VGhpcyBpcyBoaXN0b3JpY2FsIHNlY3JldCBrZXkgMiEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEh";

    @Mock
    private JwtKeyRotationMonitor monitor;

    private JwtUtils jwtUtils;
    private List<String> historicalSecrets;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        historicalSecrets = Arrays.asList(HISTORICAL_SECRET_1, HISTORICAL_SECRET_2);
        // 24小时宽限期  
        jwtUtils = new JwtUtils(CURRENT_SECRET, historicalSecrets, 24 * 60 * 60 * 1000L, monitor);
    }

    /**
     * 测试当前密钥正常工作
     */
    @Test
    void testCurrentKeyValidation() throws Exception {
        String testSubject = "currentKeyUser";

        // 使用当前密钥生成Token  
        String token = jwtUtils.generateJWT(testSubject);

        // 解析Token  
        Claims claims = jwtUtils.parseJWT(token);

        // 验证结果  
        assertEquals(testSubject, claims.getSubject());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));

        // 验证监控记录  
        verify(monitor, times(1)).recordCurrentKeyUsage();
        verify(monitor, never()).recordHistoricalKeyUsage();

        System.out.println("✓ 当前密钥验证成功: " + claims.getSubject());
    }

    /**
     * 测试历史密钥验证功能
     */
    @Test
    void testHistoricalKeyValidation() throws Exception {
        String testSubject = "historicalKeyUser";

        // 使用历史密钥生成Token  
        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);
        String tokenWithOldKey = oldJwtUtils.generateJWT(testSubject);

        // 使用包含历史密钥的JwtUtils解析  
        Claims claims = jwtUtils.parseJWT(tokenWithOldKey);

        // 验证结果  
        assertEquals(testSubject, claims.getSubject());
        assertNotNull(claims.getExpiration());

        // 验证监控记录  
        verify(monitor, times(1)).recordHistoricalKeyUsage();
        verify(monitor, never()).recordCurrentKeyUsage();

        System.out.println("✓ 历史密钥验证成功: " + claims.getSubject());
    }

    /**
     * 测试多个历史密钥的验证
     */
    @Test
    void testMultipleHistoricalKeys() throws Exception {
        String testSubject1 = "user1";
        String testSubject2 = "user2";

        // 使用第一个历史密钥生成Token  
        JwtUtils oldJwtUtils1 = new JwtUtils(HISTORICAL_SECRET_1);
        String token1 = oldJwtUtils1.generateJWT(testSubject1);

        // 使用第二个历史密钥生成Token  
        JwtUtils oldJwtUtils2 = new JwtUtils(HISTORICAL_SECRET_2);
        String token2 = oldJwtUtils2.generateJWT(testSubject2);

        // 验证两个Token都能被正确解析  
        Claims claims1 = jwtUtils.parseJWT(token1);
        Claims claims2 = jwtUtils.parseJWT(token2);

        assertEquals(testSubject1, claims1.getSubject());
        assertEquals(testSubject2, claims2.getSubject());

        // 验证监控记录  
        verify(monitor, times(2)).recordHistoricalKeyUsage();

        System.out.println("✓ 多个历史密钥验证成功");
    }

    /**
     * 测试Token验证结果包装类
     */
    @Test
    void testTokenValidationResult() {
        String testSubject = "validationTestUser";

        // 测试当前密钥Token  
        String currentToken = jwtUtils.generateJWT(testSubject);
        JwtUtils.TokenValidationResult currentResult = jwtUtils.validateToken(currentToken);

        assertTrue(currentResult.isValid());
        assertTrue(currentResult.isUsedCurrentKey());
        assertNotNull(currentResult.getClaims());
        assertNull(currentResult.getErrorMessage());

        // 测试历史密钥Token  
        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);
        String historicalToken = oldJwtUtils.generateJWT(testSubject);
        JwtUtils.TokenValidationResult historicalResult = jwtUtils.validateToken(historicalToken);

        assertTrue(historicalResult.isValid());
        assertFalse(historicalResult.isUsedCurrentKey());
        assertNotNull(historicalResult.getClaims());
        assertNull(historicalResult.getErrorMessage());

        System.out.println("✓ Token验证结果测试成功");
    }

    /**
     * 测试Access Token和Refresh Token类型识别
     */
    @Test
    void testTokenTypeIdentification() throws Exception {
        String testSubject = "tokenTypeUser";

        // 生成不同类型的Token  
        String accessToken = jwtUtils.generateAccessToken(testSubject);
        String refreshToken = jwtUtils.generateRefreshToken(testSubject);

        // 解析并验证类型  
        Claims accessClaims = jwtUtils.parseJWT(accessToken);
        Claims refreshClaims = jwtUtils.parseJWT(refreshToken);

        assertTrue(jwtUtils.isAccessToken(accessClaims));
        assertFalse(jwtUtils.isRefreshToken(accessClaims));

        assertTrue(jwtUtils.isRefreshToken(refreshClaims));
        assertFalse(jwtUtils.isAccessToken(refreshClaims));

        System.out.println("✓ Token类型识别测试成功");
    }

    /**
     * 测试Token对生成
     */
    @Test
    void testTokenPairGeneration() throws Exception {
        String testSubject = "tokenPairUser";

        JwtUtils.TokenPair tokenPair = jwtUtils.generateTokenPair(testSubject);

        assertNotNull(tokenPair.getAccessToken());
        assertNotNull(tokenPair.getRefreshToken());

        // 验证两个Token都能正确解析  
        Claims accessClaims = jwtUtils.parseJWT(tokenPair.getAccessToken());
        Claims refreshClaims = jwtUtils.parseJWT(tokenPair.getRefreshToken());

        assertEquals(testSubject, accessClaims.getSubject());
        assertEquals(testSubject, refreshClaims.getSubject());

        assertTrue(jwtUtils.isAccessToken(accessClaims));
        assertTrue(jwtUtils.isRefreshToken(refreshClaims));

        System.out.println("✓ Token对生成测试成功");
    }

    /**
     * 测试宽限期机制
     */
    @Test
    void testGracePeriodMechanism() throws Exception {
        String testSubject = "gracePeriodUser";

        // 创建一个较短宽限期的JwtUtils（1秒）  
        JwtUtils shortGraceJwtUtils = new JwtUtils(CURRENT_SECRET, historicalSecrets, 1000L, monitor);

        // 使用历史密钥生成Token  
        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);
        String oldToken = oldJwtUtils.generateJWT(testSubject);

        // 立即验证应该成功  
        Claims claims = shortGraceJwtUtils.parseJWT(oldToken);
        assertEquals(testSubject, claims.getSubject());

        // 等待超过宽限期  
        Thread.sleep(1100);

        // 再次验证应该失败  
        Exception exception = assertThrows(Exception.class, () -> {
            shortGraceJwtUtils.parseJWT(oldToken);
        });

        assertTrue(exception.getMessage().contains("所有密钥验证失败"));

        System.out.println("✓ 宽限期机制测试成功");
    }

    /**
     * 测试无效Token处理
     */
    @Test
    void testInvalidTokenHandling() {
        String invalidToken = "invalid.jwt.token";

        JwtUtils.TokenValidationResult result = jwtUtils.validateToken(invalidToken);

        assertFalse(result.isValid());
        assertFalse(result.isUsedCurrentKey());
        assertNull(result.getClaims());
        assertNotNull(result.getErrorMessage());

        System.out.println("✓ 无效Token处理测试成功");
    }

    /**
     * 测试未知密钥Token处理
     */
    @Test
    void testUnknownKeyToken() {
        String unknownSecret = "VW5rbm93biBzZWNyZXQga2V5ISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEh";
        JwtUtils unknownJwtUtils = new JwtUtils(unknownSecret);
        String unknownToken = unknownJwtUtils.generateJWT("unknownUser");

        Exception exception = assertThrows(Exception.class, () -> {
            jwtUtils.parseJWT(unknownToken);
        });

        assertTrue(exception.getMessage().contains("所有密钥验证失败"));

        System.out.println("✓ 未知密钥Token处理测试成功");
    }

    /**
     * 综合测试：模拟密钥轮换场景
     */
    @Test
    void testKeyRotationScenario() throws Exception {
        String testSubject = "rotationUser";

        // 1. 使用旧密钥生成Token（模拟轮换前）  
        JwtUtils oldSystemJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);
        String oldToken = oldSystemJwtUtils.generateJWT(testSubject);

        // 2. 系统升级，新的JwtUtils包含历史密钥  
        // 3. 验证旧Token仍然有效  
        Claims claims = jwtUtils.parseJWT(oldToken);
        assertEquals(testSubject, claims.getSubject());

        // 4. 生成新Token使用当前密钥  
        String newToken = jwtUtils.generateJWT(testSubject);
        Claims newClaims = jwtUtils.parseJWT(newToken);
        assertEquals(testSubject, newClaims.getSubject());

        // 5. 验证监控记录  
        verify(monitor, times(1)).recordHistoricalKeyUsage(); // 解析旧Token  
        verify(monitor, times(1)).recordCurrentKeyUsage();    // 解析新Token  

        System.out.println("✓ 密钥轮换场景测试成功");
    }
}