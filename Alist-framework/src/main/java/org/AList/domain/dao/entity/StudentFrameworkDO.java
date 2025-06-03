package org.AList.domain.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.annotation.Sensitive;
import org.AList.common.database.BaseDO;
import org.AList.common.enums.SensitiveType;
import org.AList.common.serializer.SensitiveSerializer;

/**
 * 学生实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_student_info")
@Builder
public class StudentFrameworkDO extends BaseDO {
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
     * 专业编号
     */
    private String majorNum;

    /**
     * 班级编号
     */
    private String classNum;

    /**
     * 入学年份
     */
    private String enrollmentYear;

    /**
     * 毕业年份
     */
    private String graduationYear;

    /**
     * 手机号
     */
    @Sensitive(type= SensitiveType.PHONE)
    @JsonSerialize(using = SensitiveSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    @Sensitive(type= SensitiveType.EMAIL)
    @JsonSerialize(using = SensitiveSerializer.class)
    private String email;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 注册状态（0待审核，1通过，2拒绝）
     */
    private Integer status;

    /**
     * 注册凭证
     */
    private String registerToken;

}
