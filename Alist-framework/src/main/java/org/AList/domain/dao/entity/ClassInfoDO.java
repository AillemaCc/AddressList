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
 * 班级路由表实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_info_class")
public class ClassInfoDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Integer id;

    /**
     * 班级标识号
     */
    private Integer classNum;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 专业标识号
     */
    private Integer majorNum;
}
