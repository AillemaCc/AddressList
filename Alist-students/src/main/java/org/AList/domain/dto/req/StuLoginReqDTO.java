package org.AList.domain.dto.req;

import lombok.Data;

/**
 * 学生登录接口请求体
 */
@Data
public class StuLoginReqDTO {
    /**
     * 学号
     */
    private String studentId;
    /**
     * 密码
     */
    private String password;
}
