package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.BaseAcademyInfoListMajorRespDTO;
import org.AList.domain.dto.resp.BaseClassInfoListStuRespDTO;
import org.AList.domain.dto.resp.BaseMajorInfoListClassRespDTO;
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
    @PutMapping("/addClass")
    public Result<Void> addBaseClassInfo(@RequestBody BaseClassInfoAddReqDTO requestParam){
        adminBaseInfoService.addBaseClassInfo(requestParam);
        return Results.success();
    }

    /**
     * 新增专业信息
     */
    @PutMapping("/addMajor")
    public Result<Void> addBaseMajorInfo(@RequestBody BaseMajorInfoAddReqDTO requestParam){
        adminBaseInfoService.addBaseMajorInfo(requestParam);
        return Results.success();
    }

    /**
     * 更新班级信息
     */
    @PostMapping("/updateClass")
    public Result<Void> updateBaseClassInfo(@RequestBody BaseClassInfoUpdateReqDTO requestParam){
        adminBaseInfoService.updateBaseClassInfo(requestParam);
        return Results.success();
    }

    /**
     * 更新班级所属的专业
     */
    @PostMapping("/updateClassMA")
    public Result<Void> updateBaseClassInfoMA(@RequestBody BaseClassInfoUpdateMAReqDTO requestParam){
        adminBaseInfoService.updateBaseClassInfoMA(requestParam);
        return Results.success();
    }

    /**
     * 修改专业所属学院
     */
    @PostMapping("/changeMajorAcademy")
    public Result<Void> changeMajorAcademy(@RequestBody ChangeMajorAcademyReqDTO requestParam){
        adminBaseInfoService.changeMajorAcademy(requestParam);
        return Results.success();
    }

    /**
     * 更新专业信息
     */
    @PostMapping("/updateMajor")
    public Result<Void> updateBaseMajorInfo(@RequestBody BaseMajorInfoUpdateReqDTO requestParam){
        adminBaseInfoService.updateBaseMajorInfo(requestParam);
        return Results.success();
    }

    /**
     * 更新学院信息
     */
    @PostMapping("/updateAcademy")
    public Result<Void> updateBaseAcademyInfo(@RequestBody BaseAcademyInfoUpdateReqDTO requestParam){
        adminBaseInfoService.updateBaseAcademyInfo(requestParam);
        return Results.success();
    }


    /**
     * 分页展示某个班级下的学生信息
     */
    @PostMapping("/listClassStu")
    public Result<IPage<BaseClassInfoListStuRespDTO>> listClassStu(@RequestBody BaseClassInfoListStuReqDTO requestParam){
        return Results.success(adminBaseInfoService.listClassStu(requestParam));
    }

    /**
     * 分页展示某个专业下的班级信息
     */
    @PostMapping("/listMajorClass")
    public Result<IPage<BaseMajorInfoListClassRespDTO>> listMajorClass(@RequestBody BaseMajorInfoListClassReqDTO requestParam){
        return Results.success(adminBaseInfoService.listMajorClass(requestParam));
    }

    /**
     * 分页展示某个学院下的专业信息
     */
    @PostMapping("/listAcademyMajor")
    public Result<IPage<BaseAcademyInfoListMajorRespDTO>> listAcademyMajor(@RequestBody BaseAcademyInfoListMajorReqDTO requestParam){
        return Results.success(adminBaseInfoService.listAcademyMajor(requestParam));
    }

}
