package org.AList.domain.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.domain.dao.entity.ApplicationDO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationQueryPageReqDTO extends Page<ApplicationDO> {
    /**
     * 接收者-学号
     */
    private String receiver;
}
