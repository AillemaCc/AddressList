package org.AList.controller;

import lombok.AllArgsConstructor;
import org.AList.annotation.MyLog;
import org.AList.domain.dao.entity.TestDO;
import org.AList.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测试联调控制层
 */
@RestController
@RequestMapping("/api/test")
@AllArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/list")
    @MyLog(title = " 测试模块", content = "测试日志操作")
    public List<TestDO> list() {
        return testService.list();
    }
}