package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审核用户返回参数响应实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditUserPageRespDTO {
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
     * 注册状态（0待审核，1通过，2拒绝）
     */
    private Integer status;

    /**
     * 拒绝备注
     */
    private String remark;

    /**
     * 注册凭证
     */
    private String registerToken;
}
