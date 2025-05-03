package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.AccpetRegistrationReqDTO;
import org.AList.domain.dto.req.RefuseRegistrationReqDTO;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;

/**
 * 管理员审核服务类接口层
 */
public interface AdministerAuditService {
    /**
     * 分页查询所有待审核请求接口层
     * @return 所有待审核请求分页信息
     */
    IPage<AuditUserPageRespDTO> listAuditRegister();

    /**
     * 通过注册
     * @param requestParam 通过注册请求实体类
     */
    void acceptRegistration(AccpetRegistrationReqDTO requestParam);

    /**
     * 检查学号对应的注册请求的审核状态
     * @param requestParam 注册请求实体类
     */
    void checkReviewStatus(String requestParam);

    /**
     * 拒绝注册
     * @param requestParam 拒绝注册请求实体类
     */
    void refuseRegistration(RefuseRegistrationReqDTO requestParam);
}
