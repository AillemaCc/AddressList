package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询注册审核结果请求实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StuRegisterRemarkReqDTO {
    /**
     * 学号
     */
    private String studentId;

    /**
     * 注册时生成的唯一token
     */
    private String registerToken;
}
