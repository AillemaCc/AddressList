package org.AList.common.convention.exception;


import org.AList.common.convention.errorcode.BaseErrorCode;
import org.AList.common.convention.errorcode.IErrorCode;

/**
 * 客户端异常
 */
public class UserException extends AbstractException {

    public UserException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public UserException(String message) {
        this(message, null, BaseErrorCode.User_ERR);
    }

    public UserException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public UserException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "UserException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}