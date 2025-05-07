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
 * 通讯站内信实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_contact_application")
public class ApplicationDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 发送者-学号
     */
    private String sender;

    /**
     * 发送者姓名
     */
    private String senderName;


    /**
     * 接收者-学号
     */
    private String receiver;

    /**
     * 接收者姓名
     */
    private String receiverName;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 验证状态 0待审核 1通过 2拒绝
     */
    private Integer status;
}
