package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送信息请求实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationSendMsgReqDTO {

    /**
     * 接收者-学号
     */
    private String receiver;

    /**
     * 发送内容
     */
    private String content;

}
