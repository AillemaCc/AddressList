package org.AList.domain.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.domain.dao.entity.ApplicationDO;

/**
 * 用户发送的站内信请求分页查询实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationSendQueryPageReqDTO extends Page<ApplicationDO> {
    /**
     * 发送者-学号
     */
    private String sender;

//    /**
//     * 当前页码（从1开始，非必填，默认1）
//     */
//    private Integer current = 1;
//
//    /**
//     * 每页数量（非必填，默认10）
//     */
//    private Integer size = 10;
}
