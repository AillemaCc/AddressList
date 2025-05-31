package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.annotation.MyLog;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.HomePageQueryReqDTO;
import org.AList.domain.dto.resp.HomePageDataDTO;
import org.AList.service.StuHomePageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stu/homepage")
@RequiredArgsConstructor
public class StuHomePageController {
    private final StuHomePageService stuHomePageService;
    /**
     * 获取学生主页通讯信息
     * @return 学生通讯信息
     */
    @MyLog(title = "学生主页信息模块", content = "获取学生主页通讯信息操作")
    @PostMapping("/query")
    public Result<HomePageDataDTO> queryHomepageInfo(@RequestBody HomePageQueryReqDTO requestParam) {
        return Results.success(stuHomePageService.queryHomepageInfo(requestParam.getStudentId()));
    }
}
