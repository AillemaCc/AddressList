package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.AccpetRegistrationReqDTO;
import org.AList.domain.dto.req.BanStudentReqDTO;
import org.AList.domain.dto.req.RefuseRegistrationReqDTO;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;
import org.AList.service.AdministerAuditService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员审核操作控制层
 */
@RestController
@RequestMapping("/api/admin/info")
@RequiredArgsConstructor
public class AdministerAuditController {
    private final AdministerAuditService administerAuditService;

    /**
     * 分页查询所有待审核请求
     * @return 所有待审核请求
     */
    @GetMapping("/auditList")
    public Result<IPage<AuditUserPageRespDTO>> listAuditRegister(){
        return Results.success(administerAuditService.listAuditRegister());
    }

    /**
     * 分页查询所有已通过请求
     * @return 所有已通过请求
     */
    @GetMapping("/validList")
    public Result<IPage<AuditUserPageRespDTO>> listAuditRegisterValid(){
        return Results.success(administerAuditService.listAuditRegisterValid());
    }

    /**
     * 分页查询所有被拒绝请求
     * @return 所有被拒绝请求
     */
    @GetMapping("/refuseList")
    public Result<IPage<AuditUserPageRespDTO>> listAuditRegisterRefuse(){
        return Results.success(administerAuditService.listAuditRegisterRefuse());
    }

    /**
     * 通过注册请求
     * @param requestParam 注册表单所属的学生学号
     */
    @PostMapping("/accept")
    public Result<Void> acceptRegistration(@RequestBody AccpetRegistrationReqDTO requestParam){
        administerAuditService.acceptRegistration(requestParam);
        return Results.success().setMessage("审核通过操作成功");
    }

    /**
     * 检查注册请求状态
     * @param requestParam 注册请求所属学生学号
     */
    @PostMapping("/checkReviewStatus")
    public Result<Void> checkReviewStatus(@RequestParam String requestParam){
        administerAuditService.checkReviewStatus(requestParam);
        return Results.success();
    }

    /**
     * 拒绝注册请求
     * @param requestParam 拒绝注册实体类
     */
    @PostMapping("/refuse")
    public Result<Void> refuseRegistration(@RequestBody RefuseRegistrationReqDTO requestParam){
        administerAuditService.refuseRegistration(requestParam);
        return Results.success().setMessage("审核拒绝操作成功");
    }

    /**
     * 根据学号ban通过审核的学生
     * @param requestParam 学号
     * @return null
     */
    @PostMapping("/ban")
    public Result<Void> banStudentById(@RequestBody BanStudentReqDTO requestParam){
        administerAuditService.banStudentById(requestParam);
        return Results.success();
    }
}
