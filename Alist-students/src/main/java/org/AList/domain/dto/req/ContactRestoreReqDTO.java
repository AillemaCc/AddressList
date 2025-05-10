package org.AList.domain.dto.req;

import lombok.Data;

/**
 * 通讯信息恢复请求DTO
 */
@Data
public class ContactRestoreReqDTO {
    /**
     * 拥有者ID
     */
    private String ownerId;
    
    /**
     * 通讯录ID
     */
    private String contactId;
}