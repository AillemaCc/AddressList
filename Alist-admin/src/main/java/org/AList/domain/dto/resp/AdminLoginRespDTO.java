package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录响应类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginRespDTO {
    /**
     * 分配的token
     */
    private String token;
}
