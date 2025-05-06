package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.AddContactReqDTO;
import org.AList.service.StuContactService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生通讯录信息操作Controller层
 */
@RestController
@RequestMapping("/api/stu/contact")
@RequiredArgsConstructor
public class StuInfoController {
    private final StuContactService stuContactService;
    /**
     * 通讯信息新增接口
     * @return void
     */
    @PutMapping("/add")
    public Result<Void> addStudentContact(@RequestBody AddContactReqDTO requestParam){
        stuContactService.addStudentContact(requestParam);
        return Results.success();
    }
}
