package org.AList.annotation;

import org.AList.common.enums.SensitiveType;

import java.lang.annotation.*;

/**
 * 数据脱敏注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {
    /**
     * 脱敏数据类型
     */
    SensitiveType type();
    
    /**
     * 前置不需要打码的长度(仅当type为CUSTOM时有效)
     */
    int prefixLen() default 0;
    
    /**
     * 后置不需要打码的长度(仅当type为CUSTOM时有效)
     */
    int suffixLen() default 0;
    
    /**
     * 打码符号(仅当type为CUSTOM时有效)
     */
    char maskChar() default '*';
}