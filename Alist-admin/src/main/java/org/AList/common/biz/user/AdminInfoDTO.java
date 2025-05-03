package org.AList.common.biz.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员上下文数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminInfoDTO {
    /**
     * 管理员username
     */
    private String username;
}
