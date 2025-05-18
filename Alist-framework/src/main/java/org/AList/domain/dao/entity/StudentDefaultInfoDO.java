package org.AList.domain.dao.entity;

import com.alibaba.excel.annotation.ExcelProperty;
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
    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    @ExcelProperty(value = "学号", index = 1)
    private String studentId;

    @ExcelProperty(value = "姓名", index = 2)
    private String name;

    @ExcelProperty(value = "专业代码", index = 3)
    private String majorNum;

    @ExcelProperty(value = "班级", index = 4)
    private String classNum;

    @ExcelProperty(value = "入学年份", index = 5)
    private String enrollmentYear;

    @ExcelProperty(value = "毕业年份", index = 6)
    private String graduationYear;

    @ExcelProperty(value = "手机号", index = 7)
    private String phone;

    @ExcelProperty(value = "邮箱", index = 8)
    private String email;
}
