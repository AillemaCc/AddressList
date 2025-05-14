package org.AList.interceptor;

import cn.hutool.core.util.ClassUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.AList.annotation.EncryptField;
import org.AList.utils.FieldEncryptUtil;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.Collection;

/**
 * 对query操作进行拦截，对{@link EncryptField}字段进行解密处理；
 */
@Slf4j
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = Statement.class)
})
@Component
public class DecryptInterceptor implements Interceptor {

    private static final String METHOD = "query";

    @Setter(onMethod_ = {@Autowired})
    private FieldEncryptUtil fieldEncryptUtil;

    @SuppressWarnings("rawtypes")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();

        // 解密处理
        // 经过测试发现，无论是返回单个对象还是集合，result都是ArrayList类型
        if(ClassUtil.isAssignable(Collection.class, result.getClass())) {
            fieldEncryptUtil.decrypt((Collection) result);
        } else {
            fieldEncryptUtil.decrypt(result);
        }

        return result;
    }
}
