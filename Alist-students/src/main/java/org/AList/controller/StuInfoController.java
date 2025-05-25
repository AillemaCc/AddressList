package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.annotation.MyLog;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.ContactQueryRespDTO;
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
    @MyLog(title = "个人通讯信息模块", content = "个人通讯信息新增操作")
    @PutMapping("/contact/add")
    public Result<Void> addStudentContact(@RequestBody ContactAddReqDTO requestParam){
        stuContactService.addStudentContact(requestParam);
        return Results.success();
    }

    /**
     * 个人拥有的通讯信息删除
     * @param requestParam 删除请求体
     * @return void
     */
    @MyLog(title = "个人通讯信息模块", content = "个人拥有的通讯信息删除操作")
    @DeleteMapping("/contact/delete")
    public Result<Void> deleteStudentContact(@RequestBody ContactDeleteReqDTO requestParam){
        stuContactService.deleteStudentContact(requestParam);
        return Results.success();
    }

    /**
     * 恢复删除的某一条个人通讯信息
     * @param requestParam 请求体
     * @return void
     */
    @MyLog(title = "个人通讯信息模块", content = "恢复删除的某一条个人通讯信息操作")
    @PostMapping("/contact/restore")
    public Result<Void> restoreStudentContact(@RequestBody ContactRestoreReqDTO requestParam){
        stuContactService.restoreStudentContact(requestParam);
        return Results.success();
    }

    /**
     * 按学号修改自己的个人通讯信息
     * @param requestParam 修改请求体
     * @return void
     */
    @MyLog(title = "个人通讯信息模块", content = "按学号修改自己的个人通讯信息操作")
    @PostMapping("/contact/update")
    public Result<Void> updateStudentContact(@RequestBody ContactUpdateReqDTO requestParam){
        stuContactService.updateStudentContact(requestParam);
        return Results.success();
    }

    /**
     * 按学号查询自己拥有的某个人的通讯信息
     * @param requestParam 查询请求体
     * @return void
     */
    @MyLog(title = "个人通讯信息模块", content = "按学号查询自己拥有的某个人的通讯信息操作")
    @GetMapping("/contact/query")
    public Result<ContactQueryRespDTO> queryContactByStuId(@RequestBody ContactQueryByIdReqDTO requestParam){
        return Results.success(stuContactService.queryContactById(requestParam));
    }

    /**
     * 分页查询个人全量通讯信息
     * @return 分页查询
     */
    @MyLog(title = "个人通讯信息模块", content = "分页查询个人全量通讯信息操作")
    @GetMapping("/contact/list")
    public Result<IPage<ContactQueryRespDTO>> queryContactList(@RequestBody ContactQueryAllOwnReqDTO requestParam){
        return Results.success(stuContactService.queryContactList(requestParam));
    }

    /**
     * 分页展示所有个人拥有的已删除的通讯信息
     */
    @MyLog(title = "个人通讯信息模块", content = "分页展示所有个人拥有的已删除的通讯信息操作")
    @GetMapping("/contact/listAllDelete")
    public Result<IPage<ContactQueryRespDTO>> queryContactListAllDelete(@RequestBody ContactQueryAllOwnReqDTO requestParam){
        return Results.success(stuContactService.queryContactListAllDelete(requestParam));
    }


}
