package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;
import org.AList.service.AdministerAuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员信息操作控制层
 */
@RestController
@RequestMapping("api/admin/info")
@RequiredArgsConstructor
public class AdministerAuditController {
    private final AdministerAuditService administerAuditService;

    /**
     * 分页查询所有待审核请求
     * @return 所有待审核请求
     */
    @GetMapping("/auditList")
    public Result<IPage<AuditUserPageRespDTO>> listAuditUser(){
        return Results.success(administerAuditService.listAuditUser());
    }
}
