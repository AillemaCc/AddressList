package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.*;
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
     * 个人通讯信息新增接口
     * @return void
     */
    @PutMapping("/contact/add")
    public Result<Void> addStudentContact(@RequestBody ContactAddReqDTO requestParam){
        stuContactService.addStudentContact(requestParam);
        return Results.success();
    }
    // todo 开发通过姓名新增他人通讯信息
//    /**
//     * 通过姓名新增他人通讯信息
//     */
//    @PutMapping("/contact/addOther")
//    public Result<Void> addOtherStudentContact(@RequestBody AddOtherContactReqDTO requestParam){}

    /**
     * 个人通讯信息删除接口
     * @param requestParam 删除请求体
     * @return void
     */
    @DeleteMapping("/contact/delete")
    public Result<Void> deleteStudentContact(@RequestBody ContactDeleteReqDTO requestParam){
        stuContactService.deleteStudentContact(requestParam);
        return Results.success();
    }

    /**
     * 按学号修改个人通讯信息
     * @param requestParam 修改请求体
     * @return void
     */
    @PostMapping("/contact/update")
    public Result<Void> updateStudentContact(@RequestBody ContactUpdateReqDTO requestParam){
        stuContactService.updateStudentContact(requestParam);
        return Results.success();
    }

    /**
     * 按学号查询个人通讯信息
     * @param requestParam 查询请求体
     * @return void
     */
    @GetMapping("/contact/query")
    public Result<QueryContactRespDTO> queryContactByStuId(@RequestBody ContactQueryByIdReqDTO requestParam){
        return Results.success(stuContactService.queryContactById(requestParam));
    }

    /**
     * 分页查询个人全量通讯信息
     * @return 分页查询
     */
    @GetMapping("/contact/list")
    public Result<IPage<QueryContactRespDTO>> queryContactList(){
        return Results.success(stuContactService.queryContactList());
    }

}
