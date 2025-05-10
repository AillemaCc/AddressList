package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按学号查询通讯信息响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactQueryRespDTO {

    /**
     * 学号
     */
    private String studentId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 所在学院
     */
    private String academy;
    /**
     * 所读专业--以专业号查询
     */
    private String major;

    /**
     * 所在班级
     */
    private String className;

    /**
     * 入学年份
     */
    private String enrollmentYear;

    /**
     * 毕业年份
     */
    private String graduationYear;

    /**
     * 就业单位
     */
    private String employer;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
}
