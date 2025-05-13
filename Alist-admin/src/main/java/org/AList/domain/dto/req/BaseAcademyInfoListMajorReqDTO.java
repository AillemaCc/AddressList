package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通过学院号查询班级信息请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseAcademyInfoListMajorReqDTO {
    /**
     * 学院标识号
     */
    private Integer academyNum;

    /**
     * 当前页码（从1开始，非必填，默认1）
     */
    private Integer current = 1;

    /**
     * 每页数量（非必填，默认10）
     */
    private Integer size = 10;
}
