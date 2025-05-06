package org.AList.common.biz.user.test;

import org.AList.common.biz.user.StuFlowRiskControlFilter;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.biz.user.StuIdInfoDTO;
import org.AList.config.UserFlowRiskControlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StuFlowRiskControlFilterTest {

    private static final Logger logger = LoggerFactory.getLogger(StuFlowRiskControlFilterTest.class);

    private StringRedisTemplate redisTemplate;
    private StuFlowRiskControlFilter filter;

    @BeforeEach
    public void setUp() {
        logger.info("【初始化测试环境】开始 setup...");

        redisTemplate = mock(StringRedisTemplate.class);
        UserFlowRiskControlConfiguration config = new UserFlowRiskControlConfiguration(Boolean.TRUE, "60", 3L); // 时间窗口60秒，最大3次
        filter = new StuFlowRiskControlFilter(redisTemplate, config);

        // 设置当前线程的用户 ID（模拟登录）
        StuIdContext.setStudentId(new StuIdInfoDTO("test9109"));

        logger.info("【初始化测试环境】setup 完成");
    }

    @Test
    // 【测试用例】testAccessWithinLimit_Allowed - 开始
    // 测试在用户访问次数未超过限制时的行为。
    // 预期：过滤器不会阻止请求，并且响应体为空（表示放行）。
    // 模拟 Redis 返回的计数值分别为 1, 2, 和 3（均未超出最大值3），验证是否正确执行 Lua 脚本以及响应是否符合预期。
    public void testAccessWithinLimit_Allowed() throws Exception {
        logger.info("【测试用例】testAccessWithinLimit_Allowed - 开始");

        try {
            when(redisTemplate.execute(Mockito.any(DefaultRedisScript.class), Mockito.anyList(), Mockito.anyLong()))
                    .thenReturn(1L)
                    .thenReturn(2L)
                    .thenReturn(3L);

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain chain = new MockFilterChain();

            filter.doFilter(request, response, chain);
            verify(redisTemplate, times(1)).execute(any(DefaultRedisScript.class), anyList(), eq(60L));
            assertThat(response.getContentAsString()).isEmpty(); // 无输出，表示放行

            logger.info("【测试用例】testAccessWithinLimit_Allowed - 成功通过 ✅");
        } catch (Exception e) {
            logger.error("【测试用例】testAccessWithinLimit_Allowed - 发生异常 ❌", e);
            throw e;
        }
    }

    @Test
    // 【测试用例】testAccessExceedsLimit_Blocked - 开始
    // 测试当用户访问次数超过设定的限流阈值（例如这里是3次/60秒）时的行为。
    // 预期：过滤器会阻止请求，并返回一个包含特定错误码和消息的 JSON 响应。
    // 模拟 Redis 返回的计数值为4（超过最大值3），检查响应内容是否包含了正确的错误信息。
    public void testAccessExceedsLimit_Blocked() throws Exception {
        logger.info("【测试用例】testAccessExceedsLimit_Blocked - 开始");

        try {
            when(redisTemplate.execute(Mockito.any(DefaultRedisScript.class), Mockito.anyList(), Mockito.anyLong()))
                    .thenReturn(4L); // 超过最大值3

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain chain = new MockFilterChain();

            filter.doFilter(request, response, chain);

            String responseBody = response.getContentAsString();
            logger.info("【响应内容】{}", responseBody);
            // 修改断言：匹配 JSON 中的 message 或 code
            assertThat(responseBody).contains("A000300") // 匹配错误码
                    .contains("当前系统繁忙，请稍后再试"); // 匹配错误消息

            logger.info("【测试用例】testAccessExceedsLimit_Blocked - 成功通过 ✅");
        } catch (Exception e) {
            logger.error("【测试用例】testAccessExceedsLimit_Blocked - 发生异常 ❌", e);
            throw e;
        }
    }

    @Test
    // 【测试用例】testLuaScriptExecutionThrowsException_ShouldReturnError - 开始
    // 测试当执行 Redis Lua 脚本出现异常时，过滤器的行为。
    // 预期：过滤器应该捕获到这个异常并返回一个包含特定错误码和消息的 JSON 响应。
    // 使用 Mockito 的 doThrow 方法模拟 Lua 脚本执行抛出异常的情况，并验证响应是否符合预期。
    public void testLuaScriptExecutionThrowsException_ShouldReturnError() throws Exception {
        logger.info("【测试用例】testLuaScriptExecutionThrowsException_ShouldReturnError - 开始");

        try {
            doThrow(new RuntimeException("Redis error"))
                    .when(redisTemplate).execute(Mockito.any(DefaultRedisScript.class), Mockito.anyList(), Mockito.anyLong());

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain chain = new MockFilterChain();

            filter.doFilter(request, response, chain);

            String responseBody = response.getContentAsString();
            logger.info("【响应内容】{}", responseBody);

            // 修改断言以匹配 JSON 内容
            assertThat(responseBody)
                    .contains("\"code\":\"A000300\"")
                    .contains("\"message\":\"当前系统繁忙，请稍后再试\"");

            logger.info("【测试用例】testLuaScriptExecutionThrowsException_ShouldReturnError - 成功通过 ✅");
        } catch (Exception e) {
            logger.error("【测试用例】testLuaScriptExecutionThrowsException_ShouldReturnError - 发生异常 ❌", e);
            throw e;
        }
    }

    @Test
    // 【测试用例】testNoUser_ShouldUseDefaultOtherKey - 开始
    // 测试当没有登录用户时，过滤器使用默认的 "other" 键来跟踪流量。
    // 预期：过滤器会使用 "other" 作为键名来记录访问次数，并且不阻止请求。
    // 清除当前线程中的用户ID（模拟未登录状态），然后模拟 Redis 返回计数值为1，验证 Redis 是否使用了正确的键。
    public void testNoUser_ShouldUseDefaultOtherKey() throws Exception {
        logger.info("【测试用例】testNoUser_ShouldUseDefaultOtherKey - 开始");

        try {
            StuIdContext.setStudentId(null); // 清除用户ID，模拟未登录状态

            when(redisTemplate.execute(Mockito.any(DefaultRedisScript.class), Mockito.anyList(), Mockito.anyLong()))
                    .thenReturn(1L);

            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain chain = new MockFilterChain();

            filter.doFilter(request, response, chain);

            // 使用 ArgumentCaptor 来捕获传递给 Redis 的 key 参数
            ArgumentCaptor<List<String>> keyCaptor = ArgumentCaptor.forClass(List.class);
            verify(redisTemplate).execute(any(), keyCaptor.capture(), anyLong());

            List<String> keysUsed = keyCaptor.getValue();
            String usedKey = keysUsed != null && !keysUsed.isEmpty() ? keysUsed.get(0) : "未知 Key";

            // 输出当前使用的 key
            logger.info("【测试用例】本次请求使用的 Redis Key 为: {}", usedKey);

            // 验证是否使用了 "other" 作为 key
            assertThat(usedKey).isEqualTo("other");

            logger.info("【测试用例】testNoUser_ShouldUseDefaultOtherKey - 成功通过 ✅");
        } catch (Exception e) {
            logger.error("【测试用例】testNoUser_ShouldUseDefaultOtherKey - 发生异常 ❌", e);
            throw e;
        }
    }
}