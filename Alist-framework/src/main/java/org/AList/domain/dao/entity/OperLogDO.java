package org.AList.domain.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 操作日志实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_oper_log")
public class OperLogDO {
    @TableId(type = IdType.AUTO)
    /**
     * 日志主键
     */
    private Long id;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求IP地址
     */
    private String ip;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 方法响应参数
     */
    private String responseResult;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private Date operTime;

    /**
     * 方法执行耗时（单位：毫秒）
     */
    private Long takeTime;

}
