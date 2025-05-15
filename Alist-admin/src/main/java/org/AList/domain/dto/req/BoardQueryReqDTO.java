package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询公告请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardQueryReqDTO {

    private Integer current;
    private Integer size;

}
