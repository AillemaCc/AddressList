package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;

/**
 * 管理员审核服务类接口层
 */
public interface AdministerAuditService {
    /**
     * 分页查询所有待审核请求接口层
     * @return 所有待审核请求分页信息
     */
    IPage<AuditUserPageRespDTO> listAuditUser();
}
