package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生登录接口返回响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuLoginRespDTO {
    private String token;
}
