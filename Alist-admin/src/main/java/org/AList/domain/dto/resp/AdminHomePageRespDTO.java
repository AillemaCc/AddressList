package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
  
@Data  
@Builder  
@NoArgsConstructor  
@AllArgsConstructor  
public class AdminHomePageRespDTO {  
    /**  
     * 管理员用户名  
     */  
    private String username;  
      
    /**  
     * 待审核请求数量  
     */  
    private Long pendingAuditCount;  
      
    /**  
     * 已发布公告数量  
     */  
    private Long releasedBoardCount;  
      
    /**  
     * 最近操作日志  
     */  
    private List<OperLogDTO> recentOperations;  
}  
  
