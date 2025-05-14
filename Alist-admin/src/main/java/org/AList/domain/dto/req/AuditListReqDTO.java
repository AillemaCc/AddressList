package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员审核操作分页请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditListReqDTO {
    private Integer current;
    private Integer size;
}
