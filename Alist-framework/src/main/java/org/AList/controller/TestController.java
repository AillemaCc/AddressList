package org.AList.controller;

import lombok.AllArgsConstructor;
import org.AList.domain.dao.entity.TestDO;
import org.AList.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@AllArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/list")
    public List<TestDO> list() {
        return testService.list();
    }
}
