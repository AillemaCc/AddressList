package org.AList.domain.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.annotation.EncryptField;
import org.AList.common.database.BaseDO;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("t_student_register")
/**
 * 注册表实体类
 */
public class RegisterDO extends BaseDO {
    @TableId(type = IdType.AUTO)
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
    @EncryptField
    private String password;

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
