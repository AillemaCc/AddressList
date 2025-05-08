package org.AList.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新通讯信息请求实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactUpdateReqDTO {
    /**
     * 学号
     */
    private String studentId;
    /**
     * 就业单位
     */
    private String employer;

    /**
     * 所在城市
     */
    private String city;
}
