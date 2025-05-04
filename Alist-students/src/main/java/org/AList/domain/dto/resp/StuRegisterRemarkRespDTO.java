package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生查询审核结果响应实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StuRegisterRemarkRespDTO {
    /**
     * 学号
     */
    private String studentId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 注册时生成的唯一token
     */
    private String registerToken;

    /**
     * 回执
     */
    private String remark;

    /**
     * 审核状态
     */
    private Integer status;
}
