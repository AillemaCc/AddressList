package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 展示学院下的专业的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseAcademyInfoListMajorRespDTO {
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
