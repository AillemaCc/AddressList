package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 查询某个班级号下的所有学生响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseClassInfoListStuRespDTO {

    /**
     * 学号
     */
    private String studentId;

    /**
     * 姓名
     */
    private String name;

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

    /**
     * 注册时间
     */
    private Date registrationTime;

    /**
     * 最新登录时间
     */
    private Date lastLoginTime;

}
