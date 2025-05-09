package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按学号查询通讯信息请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactQueryByIdReqDTO {
    /**
     * 学号
     */
    private String ownerId;

    /**
     * 通讯信息id
     */
    private String contactId;
}
