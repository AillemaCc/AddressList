package org.AList.controller;

import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.service.jwt.JwtKeyRotationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;  
  
@RestController  
@RequestMapping("/api/admin/security")  
@RequiredArgsConstructor  
public class SecurityManagementController {  
      
    private final JwtKeyRotationService jwtKeyRotationService;
      
    /**  
     * 手动触发JWT密钥轮换  
     */  
    @PostMapping("/rotateJwtKey")
    public Result<Void> rotateJwtKey() {  
        jwtKeyRotationService.manualRotateSecret();  
        return Results.success().setMessage("JWT密钥轮换已触发");  
    }  
}