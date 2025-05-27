package org.AList.service.impl;

import com.baomidou.mybatisplus.core.toolkit.AES;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.AList.service.FieldEncryptDecryptService;
import org.springframework.stereotype.Component;

/**
 * 使用Mybatis-Plus自带的AES加解密
 */
@Component
public class DefaultFieldEncryptDecryptService implements FieldEncryptDecryptService {
    private static final String ENCRYPT_PREFIX = "mpw+";
    private static final String ENCRYPT_KEY = "abcdefghijklmnop"; // 实际项目应从配置读取

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
