package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 分页查询公告响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardQueryRespDTO {

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告标识号
     */
    private Integer boardId;

    private String coverImage;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private Date updateTime;

}
