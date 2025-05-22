package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基本信息--班级信息新增请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseClassInfoAddReqDTO {
    /**
     * 班级编号
     */
    private Integer classNum;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 专业标识号
     */
    private Integer majorNum;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学院标识号
     */
    private Integer academyNum;

    /**
     * 学院名称
     */
    private String academyName;
}
