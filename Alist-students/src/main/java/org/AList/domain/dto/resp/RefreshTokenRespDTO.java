package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenRespDTO {
    private String accessToken;  
}