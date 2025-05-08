package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对单个申请信息的YesOrNo操作请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationYONReqDTO {
    /**
     * 发送者学号
     */
    private String sender;
    /**
     * 接收者学号
     */
    private String receiver;
}
