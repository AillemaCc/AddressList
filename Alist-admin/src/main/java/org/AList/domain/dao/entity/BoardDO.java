package org.AList.domain.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.AList.common.database.BaseDO;

/**
 * 公告表实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_bulletin_board")
public class BoardDO extends BaseDO{
    @TableId(type = IdType.AUTO)
    /**
     * 公告ID，主键
     */
    private Integer id;

    /**
     * 公告标识号
     */
    private Integer boardId;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告分类ID
     */
    private String category;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 状态：0-草稿，1-已发布，2-已下架
     */
    private Integer status;

    /**
     * 优先级，数字越大优先级越高
     */
    private String priority;

    /**
     * 封面图片URL
     */
    private String coverImage;
}
