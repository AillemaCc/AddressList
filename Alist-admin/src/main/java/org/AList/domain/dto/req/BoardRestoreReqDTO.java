package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据公告标识号恢复已删除公告请求体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRestoreReqDTO {
    /**
     * 公告标识号
     */
    private Integer boardId;
}
