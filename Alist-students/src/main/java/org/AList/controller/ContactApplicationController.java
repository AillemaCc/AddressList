package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.AList.domain.dto.req.QueryApplicationPageReqDTO;
import org.AList.domain.dto.resp.QueryApplicationPageRespDTO;
import org.AList.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

/**
 * 站内信申请控制层
 */
@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ContactApplicationController {
    private final ApplicationService applicationService;

    /**
     * 向某个人发送站内信申请
     * @param requestParam 请求实体
     * @return void
     */
    @PutMapping("/send")
    public Result<Void> sendApplication(@RequestBody ApplicationSendMsgReqDTO requestParam){
        applicationService.sendApplication(requestParam);
        return Results.success();
    }

    /**
     * 分页展示所有站内信请求
     * @return 分页返回
     */
    @GetMapping("/listAll")
    public Result<IPage<QueryApplicationPageRespDTO>> listAllValidApplication(@RequestBody QueryApplicationPageReqDTO requestParam){
        return Results.success(applicationService.listAllValidApplication(requestParam));
    }

    /**
     * 分页展示所有已通过站内信请求
     */
    @GetMapping("/listAllAccept")
    public Result<IPage<QueryApplicationPageRespDTO>> listAllAcceptedApplication(@RequestBody QueryApplicationPageReqDTO requestParam){
        return Results.success(applicationService.listAllAcceptedApplication(requestParam));
    }

}
