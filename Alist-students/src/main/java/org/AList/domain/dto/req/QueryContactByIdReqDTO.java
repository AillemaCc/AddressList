package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按学号查询通讯信息请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryContactByIdReqDTO {
    /**
     * 学号
     */
    private String studentId;
}
