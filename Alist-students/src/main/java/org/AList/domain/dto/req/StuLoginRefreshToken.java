package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StuLoginRefreshToken {
    /**
     * 学号
     */
    private String studentId;
    /**
     * 刷新token
     */
    private String refreshToken;
}
