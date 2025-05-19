package org.AList.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.AList.annotation.MyLog;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.ExeclStudentExportConditionReqDTO;
import org.AList.service.AdminExeclOpsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 管理员execl操作服务Controller层
 */

@RestController
@RequestMapping("/api/admin/execl")
@RequiredArgsConstructor
public class AdminExeclOpsController {
    private final AdminExeclOpsService adminExeclOpsService;

    /**
     * 通过execl文件上传学生学籍数据
     */
    @MyLog
    @SneakyThrows
    @PostMapping("/importStuDef")
    public Result<Void> importStuDef(@RequestParam("fileStuDef") MultipartFile file){
        adminExeclOpsService.importStudentDefInfo(file);
        return Results.success();
    }

    /**
     * 导出全部学籍信息
     */
    @MyLog
    @GetMapping("/exportStuDef")
    public Result<Void> exportStuDef(HttpServletResponse response) {
        adminExeclOpsService.exportStudentDefInfo(response);
        return Results.success();
    }

    /**
     * 条件导出学籍信息
     */
    @MyLog
    @GetMapping("/exportStuDefByCondition")
    public void exportStuDefByCondition(
            @RequestParam(required = false) String majorNum,
            @RequestParam(required = false) String classNum,
            @RequestParam(required = false) String enrollmentYear,
            @RequestParam(required = false) String graduationYear,
            HttpServletResponse response) {

        ExeclStudentExportConditionReqDTO condition = new ExeclStudentExportConditionReqDTO();
        condition.setMajorNum(majorNum);
        condition.setClassNum(classNum);
        condition.setEnrollmentYear(enrollmentYear);
        condition.setGraduationYear(graduationYear);

        adminExeclOpsService.exportStudentDefInfoByCondition(response, condition);
    }
}
