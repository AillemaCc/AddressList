package org.AList.common.biz.user;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.service.StuToken.TokenService;
import org.assertj.core.util.Lists;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 学生请求传输过滤器
 */
@RequiredArgsConstructor
public class StuTransmitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;
    private final TokenService tokenService;
    // 学生端接口的放行路径
    private static final List<String> IGNORE_URL= Lists.newArrayList(
            "/api/stu/register",
            "/api/stu/login",
            "/api/stu/checkLogin",
            "/api/stu/getRemark",
            "/api/stu/refreshToken"
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
        // 请求参数化
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String requestURI = httpServletRequest.getRequestURI();
        if(!IGNORE_URL.contains(requestURI)){
            String studentId = httpServletRequest.getHeader("studentId");
            String accessToken =httpServletRequest.getHeader("token");
            // 因为所有的来自未登录或者未携带token的请求，都会被这个拦截器拦截下来。而且这个没办法定义全局拦截器，因为请求没有到达SpringMVC的Controller就被拦截下来了
            // 所以在这个方法当中，我们直接返回错误
            if (studentId == null || accessToken == null) {
                // 返回 401 错误（未授权）
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.setContentType("application/json;charset=utf-8");
                httpServletResponse.getWriter().write("{\"code\":401,\"message\":\"请提供 studentId 和 token 请求头\"}");
                return; // 终止请求，不再继续执行
            }
            Object stuInfoJsonStr=stringRedisTemplate.opsForHash().get(RedisKeyGenerator.genStudentLoginAccess(studentId),accessToken);
            if(stuInfoJsonStr!=null){
                StuIdInfoDTO stuIdInfoDTO = JSON.parseObject(stuInfoJsonStr.toString(), StuIdInfoDTO.class);
                StuIdContext.setStudentId(stuIdInfoDTO);
            }
            // Access Token无效，尝试使用Refresh Token
            else{
                String refreshToken = httpServletRequest.getHeader("refreshToken");
                if (refreshToken != null) {
                    // 检查Refresh Token是否在黑名单中
                    if (tokenService.isTokenBlacklisted(refreshToken)) {
                        sendUnauthorizedResponse(httpServletResponse, "Token已失效");
                        return;
                    }
                    try {
                            String newAccessToken=tokenService.refreshStudentAccessToken(studentId,refreshToken);
                            // 获取新Token对应的用户信息
                            Object newStuInfoJsonStr=stringRedisTemplate.opsForHash().get(RedisKeyGenerator.genStudentLoginAccess(studentId),newAccessToken);
                            if(newStuInfoJsonStr!=null){
                                // 设置用户上下文
                                StuIdInfoDTO stuIdInfoDTO = JSON.parseObject(newStuInfoJsonStr.toString(), StuIdInfoDTO.class);
                                StuIdContext.setStudentId(stuIdInfoDTO);

                                // 在响应头中返回新的Access Token
                                httpServletResponse.setHeader("New-Access-Token", newAccessToken);
                            }else {
                                sendUnauthorizedResponse(httpServletResponse, "未登录");
                                return;
                            }
                        } catch (Exception e) {
                            sendUnauthorizedResponse(httpServletResponse, "未登录");
                            return;
                        }
                }else {
                    sendUnauthorizedResponse(httpServletResponse, "未登录");
                    return;
                }
            }
        }
        try {
            // 这一步是真正执行过滤器链当中的请求，也就是那些需要鉴权的请求
            // 登录的请求也直接被放行，可以不携带token和studentId
            filterChain.doFilter(servletRequest, servletResponse);
        }finally {
            StuIdContext.removeStudentId();
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}


