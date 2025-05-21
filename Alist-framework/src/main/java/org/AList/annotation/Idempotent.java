package org.AList.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {  
    /**  
     * 幂等键前缀  
     */  
    String prefix() default "";  
      
    /**  
     * 生成幂等键的SpEL表达式  
     * 可以引用方法参数名  
     */  
    String key();  
      
    /**  
     * 过期时间（秒）  
     */  
    long expiration() default 86400; // 24小时  
}