package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationQueryPageRespDTO {
    /**
     * 发送者-学号
     */
    private String sender;

    /**
     * 发送者姓名
     */
    private String senderName;


    /**
     * 接收者-学号
     */
    private String receiver;

    /**
     * 接收者姓名
     */
    private String receiverName;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 验证状态 0待审核 1通过 2拒绝
     */
    private Integer status;
}
