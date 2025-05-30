package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据公告标识号发布草稿请求体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardReleaseReqDTO {
    /**
     * 公告标识号
     */
    private Integer boardId;
}
