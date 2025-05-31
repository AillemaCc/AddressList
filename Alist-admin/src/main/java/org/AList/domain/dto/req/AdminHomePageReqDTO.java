package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;  
import lombok.Data;  
import lombok.NoArgsConstructor;  
  
/**  
 * 管理员首页请求实体类  
 */  
@Data  
@AllArgsConstructor  
@NoArgsConstructor  
@Builder  
public class AdminHomePageReqDTO {  
    /**  
     * 用户名  
     */  
    private String username;  
}