package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.HomePageQueryReqDTO;
import org.AList.domain.dto.resp.HomePageQueryRespDTO;
import org.AList.service.StuHomePageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stu/homepage")
@RequiredArgsConstructor
public class StuHomePageController {
    private final StuHomePageService stuHomePageService;
    /**
     * 获取学生主页通讯信息
     * @return 学生通讯信息
     */
    @GetMapping("/query")
    public Result<HomePageQueryRespDTO> queryHomepageInfo(@RequestBody HomePageQueryReqDTO requestParam) {
        return Results.success(stuHomePageService.queryHomepageInfo(requestParam.getStudentId()));
    }
}
