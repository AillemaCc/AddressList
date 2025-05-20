package org.AList.aop;

import cn.hutool.core.util.DesensitizedUtil;
import org.AList.annotation.Sensitive;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 数据脱敏切面
 */
@Aspect
@Component
public class SensitiveAspect {

    /**
     * 切入点：所有带有@Sensitive注解的字段所在的类的public方法
     */
    @Pointcut("@annotation(org.AList.annotation.Sensitive)")
    public void sensitivePointCut() {
    }

    @Around("sensitivePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行原方法获取返回值
        Object result = joinPoint.proceed();
        
        if (Objects.isNull(result)) {
            return null;
        }
        
        // 处理返回值脱敏
        return processSensitive(result);
    }

    /**
     * 处理脱敏
     */
    private Object processSensitive(Object obj) throws IllegalAccessException {
        if (obj instanceof Collection) {
            // 集合类型处理
            for (Object item : (Collection<?>) obj) {
                processSensitive(item);
            }
        } else if (obj instanceof Map) {
            // Map类型处理
            for (Object value : ((Map<?, ?>) obj).values()) {
                processSensitive(value);
            }
        } else if (obj.getClass().isArray()) {
            // 数组类型处理
            for (Object item : (Object[]) obj) {
                processSensitive(item);
            }
        } else {
            // 普通对象处理
            doProcessSensitive(obj);
        }
        return obj;
    }

    /**
     * 实际脱敏处理
     */
    private void doProcessSensitive(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return;
        }
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            // 检查是否有@Sensitive注解
            Sensitive sensitive = field.getAnnotation(Sensitive.class);
            if (sensitive == null) {
                continue;
            }
            
            field.setAccessible(true);
            Object fieldValue = field.get(obj);
            if (fieldValue == null) {
                continue;
            }
            
            // 只处理String类型字段
            if (fieldValue instanceof String value) {
                String desensitizedValue = desensitize(value, sensitive);
                field.set(obj, desensitizedValue);
            }
        }
    }

    /**
     * 根据注解进行脱敏处理
     */
    private String desensitize(String value, Sensitive sensitive) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return switch (sensitive.type()) {
            case PHONE -> DesensitizedUtil.mobilePhone(value);
            case EMAIL -> DesensitizedUtil.email(value);
        };
    }
}