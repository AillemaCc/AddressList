package org.AList.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.service.AdminExeclOpsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
    @SneakyThrows
    @PostMapping("/importStuDef")
    public Result<Void> importStuDef(@RequestParam("fileStuDef") MultipartFile file){
        adminExeclOpsService.importStudentDefInfo(file);
        return Results.success();
    }
}
