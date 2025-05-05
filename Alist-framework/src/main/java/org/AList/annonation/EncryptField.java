package org.AList.annonation;

import java.lang.annotation.*;

/**
 * 加密字段
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface EncryptField {

}
