package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生注册接口请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuRegisterReqDTO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 学号，唯一标识
     */
    private String studentId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 加密密码
     */
    private String password;
}
