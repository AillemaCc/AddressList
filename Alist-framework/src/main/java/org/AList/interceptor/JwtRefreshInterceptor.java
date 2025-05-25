package org.AList.interceptor;

import lombok.RequiredArgsConstructor;
import org.AList.utils.JwtUtils;  
import org.springframework.stereotype.Component;  
import org.springframework.web.servlet.HandlerInterceptor;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
@Component  
@RequiredArgsConstructor  
public class JwtRefreshInterceptor implements HandlerInterceptor {  
      
    private final JwtUtils jwtUtils;  
      
    @Override  
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {  
        String token = request.getHeader("Authorization");  
        if (token != null && token.startsWith("Bearer ")) {  
            String jwt = token.substring(7);  
              
            JwtUtils.TokenValidationResult result = jwtUtils.validateToken(jwt);  
            if (result.isValid() && !result.isUsedCurrentKey()) {  
                // 使用了历史密钥，建议客户端刷新Token  
                response.setHeader("X-Token-Refresh-Suggested", "true");  
                response.setHeader("X-Token-Refresh-Reason", "Using historical key");  
            }  
        }  
          
        return true;  
    }  
}