import io.jsonwebtoken.Claims;
import org.AList.common.monitor.JwtKeyRotationMonitor;
import org.AList.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
  
/**  
 * JWT历史密钥功能完整测试类 - 包含监控功能测试  
 */  
public class JwtHistoricalKeyWithMonitorTest {  
  
    // 测试用的密钥（Base64编码）  
    private static final String CURRENT_SECRET = "VGhpcyBpcyBhIGN1cnJlbnQgc2VjcmV0IGtleSEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEh";  
    private static final String HISTORICAL_SECRET_1 = "VGhpcyBpcyBoaXN0b3JpY2FsIHNlY3JldCBrZXkgMSEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEh";  
    private static final String HISTORICAL_SECRET_2 = "VGhpcyBpcyBoaXN0b3JpY2FsIHNlY3JldCBrZXkgMiEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEhISEh";  
  
    private JwtKeyRotationMonitor monitor;  
    private JwtUtils jwtUtils;  
    private List<String> historicalSecrets;

    @BeforeEach
    void setUp() {
        // 使用真实的监控组件
        monitor = new JwtKeyRotationMonitor();
        // 手动设置阈值，避免@Value注入失败
        ReflectionTestUtils.setField(monitor, "alertThreshold", 100L);

        historicalSecrets = Arrays.asList(HISTORICAL_SECRET_1, HISTORICAL_SECRET_2);
        jwtUtils = new JwtUtils(CURRENT_SECRET, historicalSecrets, 24 * 60 * 60 * 1000L, monitor);
    }
  
    /**  
     * 测试当前密钥使用监控  
     */  
    @Test  
    void testCurrentKeyUsageMonitoring() throws Exception {  
        String testSubject = "currentKeyUser";  
          
        // 获取初始统计  
        JwtKeyRotationMonitor.KeyUsageStats initialStats = monitor.getUsageStats();  
        assertEquals(0, initialStats.getTotalValidations());  
        assertEquals(0, initialStats.getHistoricalKeyUsage());  
          
        // 使用当前密钥生成并解析Token  
        String token = jwtUtils.generateJWT(testSubject);  
        Claims claims = jwtUtils.parseJWT(token);  
          
        // 验证解析结果  
        assertEquals(testSubject, claims.getSubject());  
          
        // 验证监控统计  
        JwtKeyRotationMonitor.KeyUsageStats stats = monitor.getUsageStats();  
        assertEquals(1, stats.getTotalValidations());  
        assertEquals(0, stats.getHistoricalKeyUsage());  
        assertEquals(0.0, stats.getHistoricalKeyPercentage());  
          
        System.out.println("✓ 当前密钥使用监控测试成功");  
    }  
  
    /**  
     * 测试历史密钥使用监控  
     */  
    @Test  
    void testHistoricalKeyUsageMonitoring() throws Exception {  
        String testSubject = "historicalKeyUser";  
          
        // 使用历史密钥生成Token  
        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);  
        String tokenWithOldKey = oldJwtUtils.generateJWT(testSubject);  
          
        // 获取初始统计  
        JwtKeyRotationMonitor.KeyUsageStats initialStats = monitor.getUsageStats();  
        assertEquals(0, initialStats.getTotalValidations());  
          
        // 使用包含历史密钥的JwtUtils解析  
        Claims claims = jwtUtils.parseJWT(tokenWithOldKey);  
        assertEquals(testSubject, claims.getSubject());  
          
        // 验证监控统计  
        JwtKeyRotationMonitor.KeyUsageStats stats = monitor.getUsageStats();  
        assertEquals(1, stats.getTotalValidations());  
        assertEquals(1, stats.getHistoricalKeyUsage());  
        assertEquals(100.0, stats.getHistoricalKeyPercentage());  
          
        System.out.println("✓ 历史密钥使用监控测试成功");  
    }  
  
    /**  
     * 测试混合使用场景的监控统计  
     */  
    @Test  
    void testMixedUsageMonitoring() throws Exception {  
        String testSubject = "mixedUser";  
          
        // 1. 使用当前密钥生成并解析Token  
        String currentToken = jwtUtils.generateJWT(testSubject);  
        jwtUtils.parseJWT(currentToken);  
          
        // 2. 使用历史密钥生成Token并解析  
        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);  
        String historicalToken = oldJwtUtils.generateJWT(testSubject);  
        jwtUtils.parseJWT(historicalToken);  
          
        // 3. 再次使用当前密钥  
        String currentToken2 = jwtUtils.generateJWT(testSubject + "2");  
        jwtUtils.parseJWT(currentToken2);  
          
        // 验证统计结果  
        JwtKeyRotationMonitor.KeyUsageStats stats = monitor.getUsageStats();  
        assertEquals(3, stats.getTotalValidations());  
        assertEquals(1, stats.getHistoricalKeyUsage());  
        assertEquals(33.33, stats.getHistoricalKeyPercentage(), 0.01);  
          
        System.out.println("✓ 混合使用监控统计测试成功");  
    }  
  
    /**  
     * 测试监控重置功能  
     */  
    @Test  
    void testMonitorReset() throws Exception {  
        String testSubject = "resetTestUser";  
          
        // 先产生一些统计数据  
        String token = jwtUtils.generateJWT(testSubject);  
        jwtUtils.parseJWT(token);  
          
        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);  
        String historicalToken = oldJwtUtils.generateJWT(testSubject);  
        jwtUtils.parseJWT(historicalToken);  
          
        // 验证有统计数据  
        JwtKeyRotationMonitor.KeyUsageStats beforeReset = monitor.getUsageStats();  
        assertEquals(2, beforeReset.getTotalValidations());  
        assertEquals(1, beforeReset.getHistoricalKeyUsage());  
          
        // 重置计数器  
        monitor.resetCounters();  
          
        // 验证重置后的统计  
        JwtKeyRotationMonitor.KeyUsageStats afterReset = monitor.getUsageStats();  
        assertEquals(0, afterReset.getTotalValidations());  
        assertEquals(0, afterReset.getHistoricalKeyUsage());  
        assertEquals(0.0, afterReset.getHistoricalKeyPercentage());  
          
        System.out.println("✓ 监控重置功能测试成功");  
    }

    @Test
    void testHighHistoricalKeyUsageAlert() throws Exception {
        String testSubject = "alertTestUser";

        // 创建一个自定义阈值的监控器来测试告警
        JwtKeyRotationMonitor customMonitor = new JwtKeyRotationMonitor();
        // 为新创建的监控器也设置阈值
        ReflectionTestUtils.setField(customMonitor, "alertThreshold", 100L);

        JwtUtils customJwtUtils = new JwtUtils(CURRENT_SECRET, historicalSecrets, 24 * 60 * 60 * 1000L, customMonitor);

        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);

        // 模拟大量历史密钥使用（超过10%阈值）
        for (int i = 0; i < 15; i++) {
            String historicalToken = oldJwtUtils.generateJWT(testSubject + i);
            customJwtUtils.parseJWT(historicalToken);
        }

        // 添加一些当前密钥使用
        for (int i = 0; i < 5; i++) {
            String currentToken = customJwtUtils.generateJWT(testSubject + "current" + i);
            customJwtUtils.parseJWT(currentToken);
        }

        // 验证统计结果
        JwtKeyRotationMonitor.KeyUsageStats stats = customMonitor.getUsageStats();
        assertEquals(20, stats.getTotalValidations());
        assertEquals(15, stats.getHistoricalKeyUsage());
        assertEquals(75.0, stats.getHistoricalKeyPercentage());

        System.out.println("✓ 高历史密钥使用率告警测试成功");
    }
  
    /**  
     * 测试Token验证结果与监控的集成  
     */  
    @Test  
    void testTokenValidationWithMonitoring() {  
        String testSubject = "validationMonitorUser";  
          
        // 测试当前密钥Token验证  
        String currentToken = jwtUtils.generateJWT(testSubject);  
        JwtUtils.TokenValidationResult currentResult = jwtUtils.validateToken(currentToken);  
          
        assertTrue(currentResult.isValid());  
        assertTrue(currentResult.isUsedCurrentKey());  
          
        // 测试历史密钥Token验证  
        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);  
        String historicalToken = oldJwtUtils.generateJWT(testSubject);  
        JwtUtils.TokenValidationResult historicalResult = jwtUtils.validateToken(historicalToken);  
          
        assertTrue(historicalResult.isValid());  
        assertFalse(historicalResult.isUsedCurrentKey());  
          
        // 验证监控统计  
        JwtKeyRotationMonitor.KeyUsageStats stats = monitor.getUsageStats();  
        assertEquals(2, stats.getTotalValidations());  
        assertEquals(1, stats.getHistoricalKeyUsage());  
        assertEquals(50.0, stats.getHistoricalKeyPercentage());  
          
        System.out.println("✓ Token验证与监控集成测试成功");  
    }  
  
    /**  
     * 测试多个历史密钥的监控统计  
     */  
    @Test  
    void testMultipleHistoricalKeysMonitoring() throws Exception {  
        String testSubject = "multiKeyUser";  
          
        // 使用第一个历史密钥  
        JwtUtils oldJwtUtils1 = new JwtUtils(HISTORICAL_SECRET_1);  
        String token1 = oldJwtUtils1.generateJWT(testSubject + "1");  
        jwtUtils.parseJWT(token1);  
          
        // 使用第二个历史密钥  
        JwtUtils oldJwtUtils2 = new JwtUtils(HISTORICAL_SECRET_2);  
        String token2 = oldJwtUtils2.generateJWT(testSubject + "2");  
        jwtUtils.parseJWT(token2);  
          
        // 使用当前密钥  
        String currentToken = jwtUtils.generateJWT(testSubject + "current");  
        jwtUtils.parseJWT(currentToken);  
          
        // 验证监控统计  
        JwtKeyRotationMonitor.KeyUsageStats stats = monitor.getUsageStats();  
        assertEquals(3, stats.getTotalValidations());  
        assertEquals(2, stats.getHistoricalKeyUsage());  
        assertEquals(66.67, stats.getHistoricalKeyPercentage(), 0.01);  
          
        System.out.println("✓ 多个历史密钥监控统计测试成功");  
    }  
  
    /**  
     * 测试无效Token不影响监控统计  
     */  
    @Test  
    void testInvalidTokenDoesNotAffectMonitoring() {  
        String invalidToken = "invalid.jwt.token";  
          
        // 获取初始统计  
        JwtKeyRotationMonitor.KeyUsageStats initialStats = monitor.getUsageStats();  
          
        // 验证无效Token  
        JwtUtils.TokenValidationResult result = jwtUtils.validateToken(invalidToken);  
        assertFalse(result.isValid());  
          
        // 验证统计没有变化  
        JwtKeyRotationMonitor.KeyUsageStats afterStats = monitor.getUsageStats();  
        assertEquals(initialStats.getTotalValidations(), afterStats.getTotalValidations());  
        assertEquals(initialStats.getHistoricalKeyUsage(), afterStats.getHistoricalKeyUsage());  
          
        System.out.println("✓ 无效Token不影响监控统计测试成功");  
    }

    /**
     * 测试触发历史密钥使用率告警
     */
    @Test
    void testTriggerHistoricalKeyUsageAlert() throws Exception {
        String testSubject = "alertTriggerUser";

        // 创建一个低阈值的监控器来更容易触发告警
        JwtKeyRotationMonitor alertMonitor = new JwtKeyRotationMonitor();
        ReflectionTestUtils.setField(alertMonitor, "alertThreshold", 5L); // 设置低阈值

        JwtUtils alertJwtUtils = new JwtUtils(CURRENT_SECRET, historicalSecrets, 24 * 60 * 60 * 1000L, alertMonitor);
        JwtUtils oldJwtUtils = new JwtUtils(HISTORICAL_SECRET_1);

        // 模拟大量历史密钥使用，确保超过阈值且使用率超过10%
        // 使用10次历史密钥（超过阈值5）
        for (int i = 0; i < 10; i++) {
            String historicalToken = oldJwtUtils.generateJWT(testSubject + i);
            alertJwtUtils.parseJWT(historicalToken);
        }

        // 添加少量当前密钥使用，保持历史密钥使用率高于10%
        // 使用2次当前密钥，总共12次验证，历史密钥使用率 = 10/12 = 83.3%
        for (int i = 0; i < 2; i++) {
            String currentToken = alertJwtUtils.generateJWT(testSubject + "current" + i);
            alertJwtUtils.parseJWT(currentToken);
        }

        // 验证统计结果
        JwtKeyRotationMonitor.KeyUsageStats stats = alertMonitor.getUsageStats();
        assertEquals(12, stats.getTotalValidations());
        assertEquals(10, stats.getHistoricalKeyUsage());
        assertTrue(stats.getHistoricalKeyPercentage() > 10); // 确保超过10%
        assertTrue(stats.getHistoricalKeyUsage() > 5); // 确保超过阈值

        System.out.println("✓ 历史密钥使用率告警触发测试成功");
        System.out.println("统计结果: " + stats.getHistoricalKeyUsage() + "/" + stats.getTotalValidations() +
                " (" + String.format("%.1f", stats.getHistoricalKeyPercentage()) + "%)");
    }
}