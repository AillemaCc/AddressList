package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.AcceptRegistrationReqDTO;
import org.AList.domain.dto.req.AuditListReqDTO;
import org.AList.domain.dto.req.BanStudentReqDTO;
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
    IPage<AuditUserPageRespDTO> listAuditRegister(AuditListReqDTO requestParam);

    /**
     * 通过注册
     * @param requestParam 通过注册请求实体类
     */
    void acceptRegistration(AcceptRegistrationReqDTO requestParam);

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

    /**
     * 分页查询所有已经通过审核的合法用户
     * @return 合法用户分页信息
     */
    IPage<AuditUserPageRespDTO> listAuditRegisterValid(AuditListReqDTO requestParam);

    /**
     * 分页查询所有已被拒绝的用户
     * @return 被拒绝用户分页信息
     */
    IPage<AuditUserPageRespDTO> listAuditRegisterRefuse(AuditListReqDTO requestParam);

    /**
     * 根据学号ban学生
     * @param requestParam 学号请求体
     */
    void banStudentById(BanStudentReqDTO requestParam);

    /**
     * 根据学号unban学生
     * @param requestParam 学号请求体
     */
    void unBanStudentById(BanStudentReqDTO requestParam);
}
