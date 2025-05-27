package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.common.properties.EncryptProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class EncryptDecryptController {
    private final EncryptProperties properties;
    @GetMapping("/encrypt/config")
    public Result<EncryptProperties> getEncryptConfig() {
        return Results.success(properties);
    }
}
