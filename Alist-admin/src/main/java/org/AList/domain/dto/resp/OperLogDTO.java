package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperLogDTO {
    /**  
     * 操作标题  
     */  
    private String title;  
      
    /**  
     * 操作内容  
     */  
    private String content;  
      
    /**  
     * 操作时间  
     */  
    private Date operTime;
      
    /**  
     * 操作方法  
     */  
    private String method;  
}