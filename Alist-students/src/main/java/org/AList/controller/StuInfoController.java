package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.AddContactReqDTO;
import org.AList.domain.dto.req.DeleteContactReqDTO;
import org.AList.domain.dto.req.QueryContactByIdReqDTO;
import org.AList.domain.dto.req.UpdateContactReqDTO;
import org.AList.domain.dto.resp.QueryContactRespDTO;
import org.AList.service.StuContactService;
import org.springframework.web.bind.annotation.*;

/**
 * 学生通讯录信息操作Controller层
 */
@RestController
@RequestMapping("/api/stu/info")
@RequiredArgsConstructor
public class StuInfoController {
    private final StuContactService stuContactService;
    /**
     * 通讯信息新增接口
     * @return void
     */
    @PutMapping("contact/add")
    public Result<Void> addStudentContact(@RequestBody AddContactReqDTO requestParam){
        stuContactService.addStudentContact(requestParam);
        return Results.success();
    }

    /**
     * 通讯信息删除接口
     * @param requestParam 删除请求体
     * @return void
     */
    @DeleteMapping("contact/delete")
    public Result<Void> deleteStudentContact(@RequestBody DeleteContactReqDTO requestParam){
        stuContactService.deleteStudentContact(requestParam);
        return Results.success();
    }

    /**
     * 按学号修改通讯信息
     * @param requestParam 修改请求体
     * @return void
     */
    @PostMapping("contact/update")
    public Result<Void> updateStudentContact(@RequestBody UpdateContactReqDTO requestParam){
        stuContactService.updateStudentContact(requestParam);
        return Results.success();
    }

    @GetMapping("/contact/query")
    public Result<QueryContactRespDTO> queryContactByStuId(@RequestParam QueryContactByIdReqDTO requestParam){
        return Results.success(stuContactService.queryContactById(requestParam));
    }
}
