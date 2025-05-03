package org.AList.domain.dto.req;

import lombok.Data;

/**
 * 通过审核注册请求实体类
 */
@Data
public class AccpetRegistrationReqDTO {
    /**
     * 注册审核请求的学号
     */
    private String studentId;
}
