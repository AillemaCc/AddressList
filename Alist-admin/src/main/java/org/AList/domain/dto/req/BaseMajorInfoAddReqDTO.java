package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseMajorInfoAddReqDTO {  
    /**  
     * 专业编号  
     */  
    private Integer majorNum;  
  
    /**  
     * 专业名称  
     */  
    private String majorName;  
  
    /**  
     * 学院编号  
     */  
    private Integer academyNum;  
  
    /**  
     * 学院名称  
     */  
    private String academyName;  
  
    /**  
     * 是否新建学院（true: 新建学院, false: 使用现有学院）  
     */  
    private Boolean createNewAcademy;  
}