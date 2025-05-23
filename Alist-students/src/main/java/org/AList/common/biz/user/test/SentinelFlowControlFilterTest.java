package org.AList.common.biz.user.test;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.AList.common.biz.user.SentinelFlowControlFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;  
  
public class SentinelFlowControlFilterTest {  
  
    private static final Logger logger = LoggerFactory.getLogger(SentinelFlowControlFilterTest.class);  
  
    private SentinelFlowControlFilter filter;  
    private MockedStatic<SphU> sphUMock;  
  
    @BeforeEach  
    public void setUp() {  
        logger.info("【初始化测试环境】开始 setup...");  
          
        filter = new SentinelFlowControlFilter();  
          
        // 初始化Sentinel规则  
        initSentinelRules();  
          
        // Mock SphU  
        sphUMock = mockStatic(SphU.class);  
          
        logger.info("【初始化测试环境】setup 完成");  
    }  
  
    @AfterEach  
    public void tearDown() {  
        if (sphUMock != null) {  
            sphUMock.close();  
        }  
        // 清理规则  
        FlowRuleManager.loadRules(new ArrayList<>());  
    }  
  
    private void initSentinelRules() {  
        List<FlowRule> flowRules = new ArrayList<>();  
          
        // 系统级限流规则  
        FlowRule systemRule = new FlowRule();  
        systemRule.setResource("system-request");  
        systemRule.setGrade(RuleConstant.FLOW_GRADE_QPS);  
        systemRule.setCount(1000);  
        systemRule.setStrategy(RuleConstant.STRATEGY_DIRECT);  
        systemRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);  
        flowRules.add(systemRule);  
          
        FlowRuleManager.loadRules(flowRules);  
    }  
  
    @Test  
    public void testNormalRequest_ShouldPass() throws Exception {  
        logger.info("【测试用例】testNormalRequest_ShouldPass - 开始");  
  
        try {  
            // Mock正常的Entry  
            Entry systemEntry = mock(Entry.class);  
            Entry userEntry = mock(Entry.class);  
              
            sphUMock.when(() -> SphU.entry("system-request"))  
                   .thenReturn(systemEntry);  
            sphUMock.when(() -> SphU.entry(eq("user-request"), any(), anyInt(), anyString()))  
                   .thenReturn(userEntry);  
  
            MockHttpServletRequest request = new MockHttpServletRequest();  
            request.addHeader("studentId", "test123");  
            MockHttpServletResponse response = new MockHttpServletResponse();  
            FilterChain chain = new MockFilterChain();  
  
            filter.doFilter(request, response, chain);  
  
            // 验证Entry被正确调用和释放  
            sphUMock.verify(() -> SphU.entry("system-request"), times(1));  
            sphUMock.verify(() -> SphU.entry(eq("user-request"), any(), anyInt(), eq("test123")), times(1));  
              
            verify(systemEntry, times(1)).exit();  
            verify(userEntry, times(1)).exit(1, "test123");  
              
            assertThat(response.getContentAsString()).isEmpty(); // 无输出，表示放行  
            assertThat(response.getStatus()).isEqualTo(200);  
  
            logger.info("【测试用例】testNormalRequest_ShouldPass - 成功通过 ✅");  
        } catch (Exception e) {  
            logger.error("【测试用例】testNormalRequest_ShouldPass - 发生异常 ❌", e);  
            throw e;  
        }  
    }  
  
    @Test  
    public void testFlowLimitExceeded_ShouldReturnError() throws Exception {  
        logger.info("【测试用例】testFlowLimitExceeded_ShouldReturnError - 开始");  
  
        try {  
            // Mock系统级限流异常  
            sphUMock.when(() -> SphU.entry("system-request"))  
                   .thenThrow(new FlowException("system-request"));  
  
            MockHttpServletRequest request = new MockHttpServletRequest();  
            request.addHeader("studentId", "test123");  
            MockHttpServletResponse response = new MockHttpServletResponse();  
            FilterChain chain = new MockFilterChain();  
  
            filter.doFilter(request, response, chain);  
  
            String responseBody = response.getContentAsString();  
            logger.info("【响应内容】{}", responseBody);  
  
            // 验证返回限流错误  
            assertThat(response.getStatus()).isEqualTo(429);  
            assertThat(responseBody).contains("请求过于频繁，请稍后再试");  
            assertThat(responseBody).contains("\"success\":false");  
  
            logger.info("【测试用例】testFlowLimitExceeded_ShouldReturnError - 成功通过 ✅");  
        } catch (Exception e) {  
            logger.error("【测试用例】testFlowLimitExceeded_ShouldReturnError - 发生异常 ❌", e);  
            throw e;  
        }  
    }  
  
    @Test  
    public void testUserLevelFlowLimit_ShouldReturnError() throws Exception {  
        logger.info("【测试用例】testUserLevelFlowLimit_ShouldReturnError - 开始");  
  
        try {  
            // Mock用户级限流异常  
            Entry systemEntry = mock(Entry.class);  
            sphUMock.when(() -> SphU.entry("system-request"))  
                   .thenReturn(systemEntry);  
            sphUMock.when(() -> SphU.entry(eq("user-request"), any(), anyInt(), anyString()))  
                   .thenThrow(new FlowException("user-request"));  
  
            MockHttpServletRequest request = new MockHttpServletRequest();  
            request.addHeader("studentId", "test123");  
            MockHttpServletResponse response = new MockHttpServletResponse();  
            FilterChain chain = new MockFilterChain();  
  
            filter.doFilter(request, response, chain);  
  
            String responseBody = response.getContentAsString();  
            logger.info("【响应内容】{}", responseBody);  
  
            // 验证返回限流错误  
            assertThat(response.getStatus()).isEqualTo(429);  
            assertThat(responseBody).contains("请求过于频繁，请稍后再试");  
              
            // 验证系统Entry被正确释放  
            verify(systemEntry, times(1)).exit();  
  
            logger.info("【测试用例】testUserLevelFlowLimit_ShouldReturnError - 成功通过 ✅");  
        } catch (Exception e) {  
            logger.error("【测试用例】testUserLevelFlowLimit_ShouldReturnError - 发生异常 ❌", e);  
            throw e;  
        }  
    }  
  
    @Test  
    public void testAnonymousUser_ShouldUseDefaultId() throws Exception {  
        logger.info("【测试用例】testAnonymousUser_ShouldUseDefaultId - 开始");  
  
        try {  
            Entry systemEntry = mock(Entry.class);  
            Entry userEntry = mock(Entry.class);  
              
            sphUMock.when(() -> SphU.entry("system-request"))  
                   .thenReturn(systemEntry);  
            sphUMock.when(() -> SphU.entry(eq("user-request"), any(), anyInt(), anyString()))  
                   .thenReturn(userEntry);  
  
            MockHttpServletRequest request = new MockHttpServletRequest();  
            // 不设置studentId header  
            MockHttpServletResponse response = new MockHttpServletResponse();  
            FilterChain chain = new MockFilterChain();  
  
            filter.doFilter(request, response, chain);  
  
            // 验证使用了默认的anonymous用户ID  
            sphUMock.verify(() -> SphU.entry(eq("user-request"), any(), anyInt(), eq("anonymous")), times(1));  
            verify(userEntry, times(1)).exit(1, "anonymous");  
  
            logger.info("【测试用例】testAnonymousUser_ShouldUseDefaultId - 成功通过 ✅");  
        } catch (Exception e) {  
            logger.error("【测试用例】testAnonymousUser_ShouldUseDefaultId - 发生异常 ❌", e);  
            throw e;  
        }  
    }  
}