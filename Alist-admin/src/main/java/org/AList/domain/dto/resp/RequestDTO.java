package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;  
import lombok.Data;  
import lombok.NoArgsConstructor;  
  
/**  
 * 请求类型统计响应实体类  
 */  
@Data  
@AllArgsConstructor  
@NoArgsConstructor  
@Builder  
public class RequestDTO {  
    /**  
     * 已通过  
     */  
    private Integer approved;  
      
    /**  
     * 待审核  
     */  
    private Integer pending;  
      
    /**  
     * 已拒绝  
     */  
    private Integer rejected;  
}