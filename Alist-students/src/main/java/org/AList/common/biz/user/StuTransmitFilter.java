package org.AList.common.biz.user;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class StuTransmitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;
    private static final List<String> IGNORE_URL= Lists.newArrayList(
            "/api/stu/register",
            "/api/stu/login",
            "/api/stu/checkLogin"
    );
    /**
     * 拦截请求验证token Filter
     * @param servletRequest HTTP请求
     * @param servletResponse HTTP响应
     * @param filterChain 过滤连
     * @throws IOException 异常
     * @throws ServletException 异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String requestURI = httpServletRequest.getRequestURI();
        if(!IGNORE_URL.contains(requestURI)){
            String studentId = httpServletRequest.getHeader("studentId");
            String token=httpServletRequest.getHeader("token");
            if (studentId == null || token == null) {
                // 返回 401 错误（未授权）
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.setContentType("application/json;charset=utf-8");
                httpServletResponse.getWriter().write("{\"code\":401,\"message\":\"请提供 studentId 和 token 请求头\"}");
                return; // 终止请求，不再继续执行
            }
            Object stuInfoJsonStr=stringRedisTemplate.opsForHash().get("login:student:"+studentId,token);
            if(stuInfoJsonStr!=null){
                StuIdInfoDTO stuIdInfoDTO = JSON.parseObject(stuInfoJsonStr.toString(), StuIdInfoDTO.class);
                StuIdContext.setStudentId(stuIdInfoDTO);
            }
            if(stuInfoJsonStr==null){
                httpServletResponse.setContentType("application/json;charset=utf-8");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
                return;
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }finally {
            StuIdContext.removeStudentId();
        }
    }
}
