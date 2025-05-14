package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新班级所属的专业信息和学院信息请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseClassInfoUpdateMAReqDTO {

    /**
     * 班级编号
     */
    private Integer classNum;

    /**
     * 班级名称
     */
    private String className;

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
