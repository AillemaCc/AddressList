package org.AList.utils;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.AList.annotation.EncryptField;
import org.AList.service.FieldEncryptDecryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 加解密工具类
 */
@Slf4j
@Component
public class FieldEncryptUtil {

    @Setter(onMethod_ = {@Autowired})
    private FieldEncryptDecryptService fieldEncryptDecryptService;

    /**对EncryptField注解进行加密处理*/
    public void encrypt(Object obj) {
        if(ClassUtil.isPrimitiveWrapper(obj.getClass())) {
            return;
        }
        encryptOrDecrypt(obj, true);
    }

    /**
     * 对单个字段值进行加密处理
     * @param value 需要加密的字段值
     * @return 加密后的字符串
     */
    public String encrypt(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        return fieldEncryptDecryptService.encrypt(value);
    }

    /**对EncryptField注解进行解密处理*/
    public void decrypt(Object obj) {
        encryptOrDecrypt(obj, false);
    }

    /**对EncryptField注解进行解密处理*/
    public void decrypt(Collection list) {
        if(CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(this::decrypt);
    }

    /**对EncryptField注解进行加解密处理*/
    private void encryptOrDecrypt(Object obj, boolean encrypt) {
        // 根据update拦截规则，第0个参数一定是MappedStatement，第1个参数是需要进行判断的参数
        if(Objects.isNull(obj)) {
            return;
        }

        // 获取所有带加密注解的字段
        List<Field> encryptFields = null;
        // 判断类上面是否有加密注解
        EncryptField encryptField = AnnotationUtils.findAnnotation(obj.getClass(), EncryptField.class);
        if(Objects.nonNull(encryptField)) {
            // 如果类上有加密注解，则所有字段都需要加密
            encryptFields = FieldUtils.getAllFieldsList(obj.getClass());
        } else {
            encryptFields = FieldUtils.getFieldsListWithAnnotation(obj.getClass(), EncryptField.class);
        }

        // 没有字段需要加密，则跳过
        if(CollectionUtils.isEmpty(encryptFields)) {
            return;
        }

        encryptFields.forEach(f->{
            // 只支持String类型的加密
            if(!ClassUtil.isAssignable(String.class, f.getType())) {
                return;
            }

            String oldValue = (String) ReflectUtil.getFieldValue(obj, f);
            if(StringUtils.isBlank(oldValue)) {
                return;
            }

            String logText = null, newValue = null;
            if(encrypt) {
                logText = "加密";
                newValue = fieldEncryptDecryptService.encrypt(oldValue);
            } else {
                logText = "解密";
                newValue = fieldEncryptDecryptService.decrypt(oldValue);
            }

            log.info("{}成功[{}类的{}字段]. 处理前:{}, 处理后:{}",
                    logText, f.getDeclaringClass().getName(), f.getName(), oldValue, newValue);
            ReflectUtil.setFieldValue(obj, f, newValue);
        });
    }
}
