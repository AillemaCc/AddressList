package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.BaseClassInfoAddReqDTO;
import org.AList.domain.dto.req.BaseClassInfoListStuReqDTO;
import org.AList.domain.dto.req.BaseClassInfoUpdateReqDTO;
import org.AList.domain.dto.resp.BaseClassInfoListStuRespDTO;
import org.AList.service.AdminBaseInfoService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员信息操作控制层
 */
@RestController
@RequestMapping("/api/admin/base")
@RequiredArgsConstructor
public class AdminBaseInfoController {
    private final AdminBaseInfoService adminBaseInfoService;
    /**
     * 新增班级信息
     */
    @PutMapping("/add")
    public Result<Void> addBaseClassInfo(@RequestBody BaseClassInfoAddReqDTO requestParam){
        adminBaseInfoService.addBaseClassInfo(requestParam);
        return Results.success();
    }
    /**
     * 更新班级信息
     */
    @PostMapping("/update")
    public Result<Void> updateBaseClassInfo(@RequestBody BaseClassInfoUpdateReqDTO requestParam){
        adminBaseInfoService.updateBaseClassInfo(requestParam);
        return Results.success();
    }

    /*
      删除班级信息--不应该实现
     */

    /**
     * 分页展示某个班级下的学生信息
     */
    @GetMapping("/listClassStu")
    public Result<IPage<BaseClassInfoListStuRespDTO>> listClassStu(@RequestBody BaseClassInfoListStuReqDTO requestParam){
        return Results.success(adminBaseInfoService.listClassStu(requestParam));
    }
}
