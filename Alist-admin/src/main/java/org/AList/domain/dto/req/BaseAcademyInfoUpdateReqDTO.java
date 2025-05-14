package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学院信息更新请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseAcademyInfoUpdateReqDTO {

    /**
     * 学院编号
     */
    private Integer academyNum;

    /**
     * 学院名称
     */
    private String academyName;
}
