package org.AList.common.enums;

import org.AList.common.convention.errorcode.IErrorCode;

/**
 * 用户错误码定义
 */
// todo 建议废弃，其中内容已包含在BaseErrorCode类中
public enum UserErrorCodeEnum implements IErrorCode {

    USER_NULL("B000200", "学生记录不存在，请检查您的学号是否正确"),

    USER_EXIST("B000201", "学生已注册，无需重复注册"),

    USER_SAVE_ERROR("B000203", "注册失败");

    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}