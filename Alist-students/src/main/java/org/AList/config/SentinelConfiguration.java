package org.AList.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.sentinel.enabled", havingValue = "true", matchIfMissing = true)
public class SentinelConfiguration {  
      
    private final SentinelProperties sentinelProperties;  
      
    @PostConstruct
    public void initSentinelRules() {  
        if (!sentinelProperties.isEnabled()) {  
            log.info("Sentinel is disabled");  
            return;  
        }  
          
        initFlowRules();  
        initDegradeRules();
        initParamFlowRules();
        initSystemRules();  
    }  
      
    private void initFlowRules() {  
        List<FlowRule> flowRules = new ArrayList<>();
          
        // 使用配置的QPS值  
        FlowRule systemRule = new FlowRule();  
        systemRule.setResource("system-request");  
        systemRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        systemRule.setCount(sentinelProperties.getFlowControl().getSystemQps());  
        systemRule.setStrategy(RuleConstant.STRATEGY_DIRECT);  
        systemRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);  
        flowRules.add(systemRule);  
          
        FlowRuleManager.loadRules(flowRules);
        log.info("Sentinel flow rules initialized with system QPS: {}",   
                sentinelProperties.getFlowControl().getSystemQps());  
    }

    /**
     * 初始化熔断降级规则
     */
    private void initDegradeRules() {
        List<DegradeRule> degradeRules = new ArrayList<>();

        // 异常比例熔断
        DegradeRule errorRatioRule = new DegradeRule();
        errorRatioRule.setResource("user-request");
        errorRatioRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        errorRatioRule.setCount(sentinelProperties.getCircuitBreaker().getErrorRatio()); // 使用配置值
        errorRatioRule.setTimeWindow(sentinelProperties.getCircuitBreaker().getTimeWindow()); // 使用配置值
        errorRatioRule.setMinRequestAmount(5); // 最小请求数
        errorRatioRule.setStatIntervalMs(1000); // 统计时长1秒
        degradeRules.add(errorRatioRule);

        // 慢调用比例熔断
        DegradeRule slowCallRule = new DegradeRule();
        slowCallRule.setResource("user-request");
        slowCallRule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
        slowCallRule.setCount(sentinelProperties.getCircuitBreaker().getSlowCallRatio()); // 使用配置值
        slowCallRule.setTimeWindow(sentinelProperties.getCircuitBreaker().getTimeWindow()); // 使用配置值
        slowCallRule.setMinRequestAmount(5); // 最小请求数
        slowCallRule.setSlowRatioThreshold(1000); // 慢调用阈值1秒
        degradeRules.add(slowCallRule);

        DegradeRuleManager.loadRules(degradeRules);
        log.info("Sentinel degrade rules initialized");
    }

    /**
     * 初始化热点参数限流规则
     */
    private void initParamFlowRules() {
        List<ParamFlowRule> paramRules = new ArrayList<>();

        // 基于用户ID的热点参数限流
        ParamFlowRule paramRule = new ParamFlowRule();
        paramRule.setResource("user-request");
        paramRule.setParamIdx(0); // 第一个参数为用户ID
        paramRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        paramRule.setCount(sentinelProperties.getFlowControl().getParamQps()); // 使用配置值
        paramRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        paramRules.add(paramRule);

        ParamFlowRuleManager.loadRules(paramRules);
        log.info("Sentinel param flow rules initialized with param QPS: {}",
                sentinelProperties.getFlowControl().getParamQps());
    }

    /**
     * 初始化系统保护规则
     */
    private void initSystemRules() {
        List<SystemRule> systemRules = new ArrayList<>();

        // CPU使用率保护
        SystemRule cpuRule = new SystemRule();
        cpuRule.setHighestCpuUsage(sentinelProperties.getSystemProtection().getHighestCpuUsage());
        systemRules.add(cpuRule);

        // 系统负载保护
        SystemRule loadRule = new SystemRule();
        loadRule.setHighestSystemLoad(sentinelProperties.getSystemProtection().getHighestSystemLoad()); // 使用配置值
        systemRules.add(loadRule);

        SystemRuleManager.loadRules(systemRules);
        log.info("Sentinel system rules initialized");
    }
}