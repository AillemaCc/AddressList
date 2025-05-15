package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询所有未删除公告请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardQueryAllValidReqDTO {

    private Integer current;
    private Integer size;

}
