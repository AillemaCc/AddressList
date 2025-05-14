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
 * 专业和学院路由表实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_info_major")
public class MajorAndAcademyDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Integer id;

    /**
     * 专业标识号
     */
    private Integer majorNum;

    /**
     * 专业名称
     */
    private String major;

    /**
     * 学院名称
     */
    private String academy;

    /**
     * 学院标识号
     */
    private Integer academyNum;
}
