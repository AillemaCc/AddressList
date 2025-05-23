package org.AList.common.biz.user;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.result.Results;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class SentinelFlowControlFilter implements Filter {
      
    @Override  
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 从请求头获取用户ID（与StuTransmitFilter保持一致）
        String studentId = httpRequest.getHeader("studentId");
        if (studentId == null || studentId.isEmpty()) {
            studentId = "anonymous";
        }

        Entry systemEntry = null;
        Entry userEntry = null;  
          
        try {  
            // 系统级限流检查  
            systemEntry = SphU.entry("system-request");
              
            // 用户级限流检查（热点参数）  
            userEntry = SphU.entry("user-request", EntryType.IN, 1, studentId);
              
            // 继续处理请求  
            chain.doFilter(request, response);  
              
        } catch (BlockException e) {
            handleBlockException(httpResponse, e);  
        } catch (Exception e) {  
            // 记录异常，用于熔断统计  
            Tracer.trace(e);
            throw e;  
        } finally {  
            if (userEntry != null) {  
                userEntry.exit(1, studentId);  
            }  
            if (systemEntry != null) {  
                systemEntry.exit();  
            }  
        }  
    }

    private void handleBlockException(HttpServletResponse response, BlockException e)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(429); // Too Many Requests

        String errorMessage;
        if (e instanceof FlowException) {
            errorMessage = "请求过于频繁，请稍后再试";
        } else if (e instanceof DegradeException) {
            errorMessage = "服务暂时不可用，请稍后再试";
        } else if (e instanceof ParamFlowException) {
            errorMessage = "用户请求过于频繁，请稍后再试";
        } else if (e instanceof SystemBlockException) {
            errorMessage = "系统负载过高，请稍后再试";
        } else {
            errorMessage = "服务限流，请稍后再试";
        }

        String errorResponse = JSON.toJSONString(
                Results.failure(new ClientException(errorMessage))
        );
        response.getWriter().write(errorResponse);

        // 添加空值检查
        Object rule = null;
        try {
            rule = e.getRule();
        } catch (Exception ex) {
            // 忽略获取规则时的异常
        }

        log.warn("Request blocked by Sentinel: {}, rule: {}",
                e.getClass().getSimpleName(), rule);
    }


}