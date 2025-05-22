package org.AList.domain.dto.resp;

import lombok.Data;

@Data
public class HomePageQueryRespDTO {

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
     * 所读专业
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
}
