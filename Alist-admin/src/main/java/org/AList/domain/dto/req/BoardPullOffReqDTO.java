package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据公告标识号下架公告请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardPullOffReqDTO {

    /**
     * 公告标识号
     */
    private Integer boardId;

}
