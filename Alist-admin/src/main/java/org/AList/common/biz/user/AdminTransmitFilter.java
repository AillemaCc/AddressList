package org.AList.common.biz.user;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.AList.common.generator.RedisKeyGenerator;
import org.AList.service.AdminToken.TokenService;
import org.assertj.core.util.Lists;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 管理员请求传输过滤器
 */
@RequiredArgsConstructor
public class AdminTransmitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;
    private final TokenService tokenService;
    // 管理员接口的放行路径
    private static final List<String> IGNORE_URL= Lists.newArrayList(
            "/api/admin/login",
            "/api/admin/checkLogin",
            "/api/admin/refreshToken"
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

        // 添加 CORS headers
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "New-Access-Token, X-Refresh-Required");
        // 处理 OPTIONS 请求（预检）
        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        System.out.println("请求到达过滤器: " + httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI());

        String requestURI = httpServletRequest.getRequestURI();

        if(!IGNORE_URL.contains(requestURI)){
            String username = httpServletRequest.getHeader("username");
            String accessToken = httpServletRequest.getHeader("accessToken");

            // 场景1：缺少必要请求头
            if (username == null || accessToken == null) {
                String missingField = username == null ? "username" : "accessToken";
                sendUnauthorizedResponse(httpServletResponse,
                        "认证失败：请求头中缺少" + missingField + "字段");
                return;
            }

            // 场景2：验证accessToken有效性
            String accessRedisKey = RedisKeyGenerator.genAdministerLoginAccess(username);
            Object adminInfoJsonStr = stringRedisTemplate.opsForHash()
                    .get(accessRedisKey, accessToken);

            if(adminInfoJsonStr != null){
                // Token有效，设置用户上下文
                AdminInfoDTO adminInfoDTO = JSON.parseObject(adminInfoJsonStr.toString(), AdminInfoDTO.class);
                AdminContext.setUsername(adminInfoDTO);
                // 检查token过期时间
                Long ttl = stringRedisTemplate.getExpire(accessRedisKey, TimeUnit.MINUTES);

                // 如果过期时间小于5分钟，设置一个响应头通知前端
                if (ttl != null && ttl < 5) {
                    httpServletResponse.setHeader("X-Refresh-Required", "true");
                }
            } else {
                // 场景3：accessToken无效，尝试刷新
                String refreshToken = httpServletRequest.getHeader("refreshToken");

                // 场景3.1：未提供refreshToken
                if (refreshToken == null) {
                    sendUnauthorizedResponse(httpServletResponse,
                            "会话已过期：accessToken无效且未提供refreshToken，请重新登录");
                    return;
                }

                // 场景3.2：refreshToken在黑名单中
                if (tokenService.isTokenBlacklisted(refreshToken)) {
                    sendUnauthorizedResponse(httpServletResponse,
                            "安全警告：该refreshToken已被禁用，可能由于账号在其他设备登录");
                    return;
                }

                try {
                    // 场景3.3：尝试刷新token
                    String newAccessToken = tokenService.refreshAdministerAccessToken(username, refreshToken);
                    Object newAdminInfoJsonStr = stringRedisTemplate.opsForHash()
                            .get(RedisKeyGenerator.genAdministerLoginAccess(username), newAccessToken);

                    if(newAdminInfoJsonStr != null){
                        AdminInfoDTO adminInfoDTO = JSON.parseObject(newAdminInfoJsonStr.toString(), AdminInfoDTO.class);
                        AdminContext.setUsername(adminInfoDTO);
                        httpServletResponse.setHeader("New-Access-Token", newAccessToken);
                    } else {
                        // 场景3.4：刷新后仍无法获取用户信息
                        sendUnauthorizedResponse(httpServletResponse,
                                "系统异常：Token刷新成功但用户信息获取失败，请重新登录");
                        return;
                    }
                } catch (Exception e) {
                    // 场景3.5：refreshToken无效或过期
                    sendUnauthorizedResponse(httpServletResponse,
                            "会话已过期：refreshToken无效或已过期，请重新登录");
                    return;
                }
            }
        }

        try {
            // 这一步是真正执行过滤器链当中的请求，也就是那些需要鉴权的请求
            // 登录的请求也直接被放行，可以不携带token和username
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            AdminContext.removeUsername();
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}
