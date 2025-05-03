package org.AList.domain.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.common.database.BaseDO;

/**
 * 登录日志实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_student_login_log")
public class LoginLogDO extends BaseDO{
    @TableId(type = IdType.AUTO)
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 登录的IP地址
     */
    private String ip;

    /**
     * 登录的浏览器
     */
    private String browser;

    /**
     * 登录的操作系统
     */
    private String os;

    /**
     * 登录的次数
     */
    private Integer frequency;

}
