package org.AList.domain.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.AList.common.database.BaseDO;

/**
 * 通讯信息路由表实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_contact_goto")
public class ContactGotoDO extends BaseDO {
    @TableId(type = IdType.AUTO)

    private Integer id;

    /**
     * 通讯录信息所属的学生id
     */
    private String contactId;

    /**
     * 拥有该信息的学号
     */
    private String ownerId;
}
