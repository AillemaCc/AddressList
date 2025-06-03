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
    private static final List<String> IGNORE_URL = Lists.newArrayList(
            "/api/admin/login",
            "/api/admin/checkLogin",
            "/api/admin/refreshToken",
            "/api/admin/board/queryAllReleased"
    );

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
            System.out.println("[CORS] 预检请求处理完成");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String requestURI = httpServletRequest.getRequestURI();
        System.out.println("【请求开始】方法：" + httpServletRequest.getMethod() + "，路径：" + requestURI);

        // 如果是白名单路径，则直接放行
        if (IGNORE_URL.contains(requestURI)) {
            System.out.println("✅ 路径在白名单中，跳过鉴权验证");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 获取请求头中的字段
        String username = httpServletRequest.getHeader("username");
        String accessToken = httpServletRequest.getHeader("accessToken");

        System.out.println("🔍 收到请求头：username=" + username + "，accessToken=" + accessToken);

        // 场景1：缺少必要请求头
        if (username == null || accessToken == null) {
            String missingField = username == null ? "username" : "accessToken";
            System.out.println("❌ 认证失败：请求头中缺少字段：" + missingField);
            sendUnauthorizedResponse(httpServletResponse,
                    "认证失败：请求头中缺少" + missingField + "字段");
            return;
        }

        // 构建 Redis Key
        String accessRedisKey = RedisKeyGenerator.genAdministerLoginAccess(username);
        System.out.println("🔑 正在查询 Redis Key：" + accessRedisKey);

        // 场景2：验证 accessToken 是否有效
        Object adminInfoJsonStr = stringRedisTemplate.opsForHash().get(accessRedisKey, accessToken);
        System.out.println("📊 Redis 查询结果：" + adminInfoJsonStr);

        if (adminInfoJsonStr != null) {
            System.out.println("🟢 accessToken 有效，解析用户信息...");
            try {
                AdminInfoDTO adminInfoDTO = JSON.parseObject(adminInfoJsonStr.toString(), AdminInfoDTO.class);
                AdminContext.setUsername(adminInfoDTO);

                Long ttl = stringRedisTemplate.getExpire(accessRedisKey, TimeUnit.MINUTES);
                System.out.println("⏳ accessToken 剩余过期时间：" + (ttl != null ? ttl + "分钟" : "未知"));

                if (ttl != null && ttl < 5) {
                    System.out.println("🔔 accessToken 即将过期，设置刷新标志");
                    httpServletResponse.setHeader("X-Refresh-Required", "true");
                }
            } catch (Exception e) {
                System.err.println("🔴 解析 AdminInfoDTO 异常：" + e.getMessage());
                sendUnauthorizedResponse(httpServletResponse, "系统异常：用户信息解析失败，请重新登录");
                return;
            }
        } else {
            System.out.println("🟠 accessToken 无效，尝试使用 refreshToken 刷新...");

            String refreshToken = httpServletRequest.getHeader("refreshToken");
            System.out.println("🔄 refreshToken=" + refreshToken);

            // 场景3.1：未提供 refreshToken
            if (refreshToken == null) {
                System.out.println("❌ 未提供 refreshToken");
                sendUnauthorizedResponse(httpServletResponse,
                        "会话已过期：accessToken无效且未提供refreshToken，请重新登录");
                return;
            }

            // 场景3.2：refreshToken 在黑名单中
            if (tokenService.isTokenBlacklisted(refreshToken)) {
                System.out.println("❌ refreshToken 已被拉黑");
                sendUnauthorizedResponse(httpServletResponse,
                        "安全警告：该refreshToken已被禁用，可能由于账号在其他设备登录");
                return;
            }

            // 场景3.3：尝试刷新 token
            try {
                System.out.println("🔄 正在调用 tokenService.refreshAdministerAccessToken()");
                String newAccessToken = tokenService.refreshAdministerAccessToken(username, refreshToken);
                System.out.println("🆕 新 accessToken：" + newAccessToken);

                Object newAdminInfoJsonStr = stringRedisTemplate.opsForHash()
                        .get(RedisKeyGenerator.genAdministerLoginAccess(username), newAccessToken);

                if (newAdminInfoJsonStr != null) {
                    AdminInfoDTO adminInfoDTO = JSON.parseObject(newAdminInfoJsonStr.toString(), AdminInfoDTO.class);
                    AdminContext.setUsername(adminInfoDTO);
                    httpServletResponse.setHeader("New-Access-Token", newAccessToken);
                    System.out.println("🟢 Token 刷新成功，设置新 accessToken");
                } else {
                    System.err.println("🔴 刷新后无法获取用户信息");
                    sendUnauthorizedResponse(httpServletResponse,
                            "系统异常：Token刷新成功但用户信息获取失败，请重新登录");
                    return;
                }
            } catch (Exception e) {
                System.err.println("🔴 Token 刷新失败：" + e.getMessage());
                sendUnauthorizedResponse(httpServletResponse,
                        "会话已过期：refreshToken无效或已过期，请重新登录");
                return;
            }
        }

        try {
            System.out.println("✅ 鉴权通过，继续执行后续过滤器链...");
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            AdminContext.removeUsername();
            System.out.println("🧹 清除管理员上下文");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        System.out.println("🚫 返回 401 响应：" + message);
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}