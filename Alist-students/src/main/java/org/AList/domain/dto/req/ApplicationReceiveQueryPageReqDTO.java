package org.AList.domain.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.domain.dao.entity.ApplicationDO;

/**
 * 用户接受的请求分页查询实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationReceiveQueryPageReqDTO extends Page<ApplicationDO> {
    /**
     * 接收者-学号
     */
    private String receiver;
}
