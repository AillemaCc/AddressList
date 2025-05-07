package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按学号查询通讯信息响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryContactRespDTO {

    /**
     * 学号
     */
    private String studentId;
    /**
     * 就业单位
     */
    private String employer;

    /**
     * 所在城市
     */
    private String city;
}
