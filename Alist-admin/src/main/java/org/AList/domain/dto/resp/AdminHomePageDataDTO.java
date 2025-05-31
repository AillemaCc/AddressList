package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
  
@Data  
@Builder  
@NoArgsConstructor  
@AllArgsConstructor  
public class AdminHomePageDataDTO {
    /**
     * 公告类型
     */
    private BoardDTO board;

    /**
     * 请求类型
     */
    private RequestDTO request;

    /**
     * 用户名
     */
    private String username;
}  
  
