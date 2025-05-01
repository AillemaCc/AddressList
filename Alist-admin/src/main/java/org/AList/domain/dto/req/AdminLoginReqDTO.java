package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录请求类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginReqDTO {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
