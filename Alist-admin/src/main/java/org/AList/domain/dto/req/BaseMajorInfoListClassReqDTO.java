package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页展示某个专业下的班级信息请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseMajorInfoListClassReqDTO {

    /**
     * 专业标识号
     */
    private Integer majorNum;

    /**
     * 当前页码（从1开始，非必填，默认1）
     */
    private Integer current = 1;

    /**
     * 每页数量（非必填，默认10）
     */
    private Integer size = 10;
}
