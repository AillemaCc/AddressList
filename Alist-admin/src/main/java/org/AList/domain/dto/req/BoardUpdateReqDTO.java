package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.domain.dto.baseDTO.BoardBaseDTO;

/**
 * 修改公告请求体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateReqDTO implements BoardBaseDTO {
    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告标识号
     */
    private Integer boardId;

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
