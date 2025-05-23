package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardQueryByIdReqDTO {  
    /**  
     * 公告标识号  
     */  
    private Integer boardId;  
}