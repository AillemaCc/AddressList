package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;  
import lombok.Data;  
import lombok.NoArgsConstructor;  
  
/**  
 * 学生账号情况统计响应实体类  
 */  
@Data  
@AllArgsConstructor  
@NoArgsConstructor  
@Builder  
public class DataStatisticDTO {  
    /**  
     * 已通过  
     */  
    private Integer approved;  
      
    /**  
     * 已删除  
     */  
    private Integer deleted;  
      
    /**  
     * 待回复请求数  
     */  
    private Integer pendingReply;  
      
    /**  
     * 已拒绝  
     */  
    private Integer rejected;  
      
    /**  
     * 已发送  
     */  
    private Integer sent;  
      
    /**  
     * 总通讯信息数  
     */  
    private Integer totalContacts;  
}