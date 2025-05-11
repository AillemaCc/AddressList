package org.AList.domain.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.AList.common.database.BaseDO;

/**
 * 学生通讯信息表实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_student_contact")
public class ContactDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 就业单位
     */
    private String employer;

    /**
     * 所在城市
     */
    private String city;

}
