package org.AList.domain.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.common.database.BaseDO;

/**
 * 学生学籍信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_student_definfo")
public class StudentDefaultInfoDO extends BaseDO {
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
     * 专业
     */
    private String majorNum;

    /**
     * 班级
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
    private String phone;

    /**
     * 邮箱
     */
    private String email;
}
