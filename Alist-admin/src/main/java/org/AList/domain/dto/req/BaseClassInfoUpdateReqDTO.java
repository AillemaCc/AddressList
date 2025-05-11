package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基本信息--班级信息更新请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseClassInfoUpdateReqDTO {
    /**
     * 班级编号
     */
    private Integer classNum;

    /**
     * 班级名称
     */
    private String className;
}
