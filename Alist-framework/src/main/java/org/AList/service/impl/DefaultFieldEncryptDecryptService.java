package org.AList.service.impl;

import com.baomidou.mybatisplus.core.toolkit.AES;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.AList.common.properties.EncryptProperties;
import org.AList.service.FieldEncryptDecryptService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 使用Mybatis-Plus自带的AES加解密
 */
@Component
@RequiredArgsConstructor
public class DefaultFieldEncryptDecryptService implements FieldEncryptDecryptService {

    private final EncryptProperties encryptProperties;
    private String ENCRYPT_PREFIX;
    private String ENCRYPT_KEY; // 实际项目应从配置读取
    @PostConstruct
    private void init() {
        this.ENCRYPT_PREFIX = encryptProperties.getEncryptPrefix();
        this.ENCRYPT_KEY = encryptProperties.getEncryptKey();
    }

    @Override
    public String encrypt(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        // 已加密的内容不再重复加密
        if (isEncrypt(value)) {
            return value;
        }

        try {
            return ENCRYPT_PREFIX + AES.encrypt(value, ENCRYPT_KEY);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    @Override
    public String decrypt(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        // 未加密的内容直接返回
        if (!isEncrypt(value)) {
            return value;
        }

        try {
            // 移除前缀
            String actualEncryptedValue = value.substring(ENCRYPT_PREFIX.length());
            return AES.decrypt(actualEncryptedValue, ENCRYPT_KEY);
        } catch (Exception e) {
            throw new RuntimeException("解密失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isEncrypt(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        // 更严格的加密格式校验
        return value.startsWith(ENCRYPT_PREFIX) && value.length() > ENCRYPT_PREFIX.length();
    }
}
