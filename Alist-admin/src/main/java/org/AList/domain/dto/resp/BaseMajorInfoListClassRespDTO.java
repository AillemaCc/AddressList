package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页展示某个专业下的班级信息响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseMajorInfoListClassRespDTO {
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
    private String major;

    /**
     * 学院名称
     */
    private String academy;

    /**
     * 学院标识号
     */
    private Integer academyNum;
}
