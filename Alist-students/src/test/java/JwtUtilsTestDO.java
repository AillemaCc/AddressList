import io.jsonwebtoken.Claims;
import org.AList.AListStudentApplication;
import org.AList.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AListStudentApplication.class)  // 加载 Spring 测试上下文
@TestPropertySource("classpath:application.yml")  // 指定测试配置文件
public class JwtUtilsTestDO {
    @Value("${jwt.secret}")  // 自动注入配置值
    private String testKey;

    @Test
    public void testKeyInjection() {
        System.out.println("注入的密钥: " + testKey);
    }

    @Test
    public void testTokenGeneration() {
        JwtUtils jwtUtils = new JwtUtils(testKey);  // 手动传入密钥
        String token = jwtUtils.generateJWT("testUser");
        System.out.println("生成的 Token: " + token);
    }

    @Test
    public void testTokenParsing() throws Exception {
        JwtUtils jwtUtils = new JwtUtils(testKey);
        // 生成测试Token
        String testSubject = "user123";
        String token = jwtUtils.generateJWT(testSubject);
        System.out.println("生成的Token: " + token);
        // 解析Token
        Claims claims = jwtUtils.parseJWT(token);

        // 验证解析结果
        assertEquals(testSubject, claims.getSubject(), "Subject不匹配");
        assertNotNull(claims.getExpiration(), "过期时间未设置");
        assertTrue(claims.getExpiration().after(new Date()), "Token已过期");
        System.out.println("解析结果: " + claims);
    }
}

