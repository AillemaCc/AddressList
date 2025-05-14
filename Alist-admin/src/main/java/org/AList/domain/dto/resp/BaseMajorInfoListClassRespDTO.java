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
}
