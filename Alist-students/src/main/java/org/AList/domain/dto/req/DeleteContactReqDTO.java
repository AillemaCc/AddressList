package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除通讯信息请求实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteContactReqDTO {
    /**
     * 学号
     */
    private String studentId;
}
