package org.AList.domain.dto.req;

import lombok.Data;

/**
 * 拒绝审核请求实体类
 */
@Data
public class RefuseRegistrationReqDTO {

    /**
     * 注册审核请求的学号
     */
    private String studentId;
    /**
     * 拒绝原因--由前端传入
     */
    private String remark;
}
