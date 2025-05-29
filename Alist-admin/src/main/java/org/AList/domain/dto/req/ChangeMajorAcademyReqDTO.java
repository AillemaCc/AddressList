package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeMajorAcademyReqDTO {  
    /**  
     * 专业编号  
     */  
    private Integer majorNum;  
  
    /**  
     * 新的学院编号  
     */  
    private Integer newAcademyNum;  
  
    /**  
     * 新的学院名称  
     */  
    private String newAcademyName;  
  
    /**  
     * 是否创建新学院（true: 创建新学院, false: 使用现有学院）  
     */  
    private Boolean createNewAcademy;  
}