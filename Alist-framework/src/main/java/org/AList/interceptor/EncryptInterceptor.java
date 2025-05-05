package org.AList.interceptor;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.AList.annonation.EncryptField;
import org.AList.utils.FieldEncryptUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 对update操作进行拦截，对{@link EncryptField}字段进行加密处理；
 * 无论是save方法还是saveBatch方法都会被成功拦截；
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Component
public class EncryptInterceptor implements Interceptor {

    private static final String METHOD = "update";

    @Setter(onMethod_ = {@Autowired})
    private FieldEncryptUtil fieldEncryptUtil;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!METHOD.equals(invocation.getMethod().getName())) {
            return invocation.proceed();
        }

        Object[] args = invocation.getArgs();
        if (args.length < 2 || args[1] == null) {
            return invocation.proceed();
        }

        // 处理不同类型的参数
        Object param = args[1];
        if (param instanceof Map) {
            // 处理批量更新或带条件的更新
            ((Map<?, ?>) param).values().stream()
                    .filter(Objects::nonNull)
                    .forEach(fieldEncryptUtil::encrypt);
        } else {
            // 处理普通实体对象
            fieldEncryptUtil.encrypt(param);
        }

        return invocation.proceed();
    }
}
