package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.AList.service.ApplicationService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
