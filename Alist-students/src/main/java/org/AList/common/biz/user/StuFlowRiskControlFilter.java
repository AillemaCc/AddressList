package org.AList.common.biz.user;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.result.Results;
import org.AList.config.UserFlowRiskControlConfiguration;
import org.assertj.core.util.Lists;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Optional;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;

/**
 * 用户操作流量风控过滤器
 */
@Slf4j
@RequiredArgsConstructor
public class StuFlowRiskControlFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserFlowRiskControlConfiguration userFlowRiskControlConfiguration;

    private static final String USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH = "lua/user_flow_risk_control.lua";


    @PostConstruct
    public void init() {
        Assert.isTrue(new ClassPathResource(USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH).exists(),
                "Lua脚本文件不存在: " + USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH);
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH)));
        redisScript.setResultType(Long.class);
        String username = Optional.ofNullable(StuIdContext.getStudentId()).orElse("other");
        Long result = null;
        try {
            result = stringRedisTemplate.execute(redisScript, Lists.newArrayList(username), Long.parseLong(userFlowRiskControlConfiguration.getTimeWindow()));
        } catch (Throwable ex) {
            log.error("执行用户请求流量限制LUA脚本出错", ex);
            returnJson((HttpServletResponse) response, JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERR))));//B0101:系统繁忙
        }
        if (result == null || result > userFlowRiskControlConfiguration.getMaxAccessCount()) {
            returnJson((HttpServletResponse) response, JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERR))));//B0101:系统繁忙
        }
        filterChain.doFilter(request, response);
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        }
    }
}