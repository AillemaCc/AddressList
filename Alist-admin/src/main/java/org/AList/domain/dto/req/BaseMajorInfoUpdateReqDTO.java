package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专业信息更新请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseMajorInfoUpdateReqDTO {

    /**
     * 专业编号
     */
    private Integer majorNum;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学院编号
     */
    private Integer academyNum;

    /**
     * 学院名称
     */
    private String academyName;
}
