package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询个人拥有的全量通讯信息请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactQueryAllOwnReqDTO {
    /**
     * 通讯信息的owner学号
     */
    private String ownerId;
}
