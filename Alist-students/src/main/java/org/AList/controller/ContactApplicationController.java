package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.annotation.MyLog;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.ApplicationQueryPageRespDTO;
import org.AList.domain.dto.resp.QuerySomeoneRespDTO;
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
     * 根据姓名查询已注册同学信息
     */
    @MyLog
    @PostMapping("/querySomeone")
    public Result<IPage<QuerySomeoneRespDTO>> querySomeone(@RequestBody QuerySomeoneReqDTO requestParam) {
        return Results.success(applicationService.querySomeone(requestParam));
    }

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
     * 同意某个站内信申请
     */
    @PutMapping("/acceptSingle")
    public Result<Void> acceptSingleApplication(@RequestBody ApplicationYONReqDTO requestParam){
        applicationService.acceptSingleApplication(requestParam);
        return Results.success();
    }

    /**
     * 拒绝某个站内信申请
     * @param requestParam 拒绝请求实体
     * @return void
     */
    @PutMapping("/refuseSingle")
    public Result<Void> refuseSingleApplication(@RequestBody ApplicationYONReqDTO requestParam){
        applicationService.refuseSingleApplication(requestParam);
        return Results.success();
    }

    /**
     * 删除某个站内信申请
     * @param requestParam 删除请求实体
     * @return void
     */
    @DeleteMapping("/deleteSingle")
    public Result<Void> deleteSingleApplication(@RequestBody ApplicationYONReqDTO requestParam){
        applicationService.deleteSingleApplication(requestParam);
        return Results.success();
    }

    /**
     * 分页展示发给自己的所有站内信请求
     * @return 分页返回
     */
    @PostMapping("/listAll")
    public Result<IPage<ApplicationQueryPageRespDTO>> listAllValidApplication(@RequestBody ApplicationReceiveQueryPageReqDTO requestParam){
        return Results.success(applicationService.listAllValidApplication(requestParam));
    }

    /**
     * 分页展示所有自己已通过站内信请求
     */
    @PostMapping("/listAllAccept")
    public Result<IPage<ApplicationQueryPageRespDTO>> listAllAcceptedApplication(@RequestBody ApplicationReceiveQueryPageReqDTO requestParam){
        return Results.success(applicationService.listAllAcceptedApplication(requestParam));
    }

    /**
     * 分页展示所有自己已拒绝的站内信请求
     */
    @PostMapping("/listAllRefuse")
    public Result<IPage<ApplicationQueryPageRespDTO>>  listAllRefuseApplication(@RequestBody ApplicationReceiveQueryPageReqDTO requestParam){
        return Results.success(applicationService.listAllRefusedApplication(requestParam));
    }

    /**
     * 分页展示所有自己已删除的站内信请求
     */
    @PostMapping("/listAllDelete")
    public Result<IPage<ApplicationQueryPageRespDTO>> listAllDeleteApplication(@RequestBody ApplicationReceiveQueryPageReqDTO requestParam){
        return Results.success(applicationService.listAllDeleteApplication(requestParam));
    }

    /**
     * 分页查询所有自己已发送的站内信请求
     */
    @PostMapping("/listAllSend")
    public Result<IPage<ApplicationQueryPageRespDTO>> listAllSendApplication(@RequestBody ApplicationSendQueryPageReqDTO requestParam){
        return Results.success(applicationService.listAllSendApplication(requestParam));
    }

}
