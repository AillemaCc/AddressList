package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;  
import lombok.Data;  
import lombok.NoArgsConstructor;  
  
/**  
 * 公告类型统计响应实体类  
 */  
@Data  
@AllArgsConstructor  
@NoArgsConstructor  
@Builder  
public class BoardDTO {  
    /**  
     * 草稿  
     */  
    private Integer draft;  
      
    /**  
     * 已下架  
     */  
    private Integer pulledoff;  
      
    /**  
     * 已发布  
     */  
    private Integer released;  
}