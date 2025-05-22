package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询某个班级号下的所有学生请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class BaseClassInfoListStuReqDTO {

    /**
     * 班级标识号
     */
    private Integer classNum;

    /**
     * 当前页码（从1开始，非必填，默认1）
     */
    private Integer current;

    /**
     * 每页数量（非必填，默认10）
     */
    private Integer size;
}
