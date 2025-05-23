package org.AList.utils;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.AList.common.convention.errorcode.BaseErrorCode;
import org.AList.common.convention.errorcode.IErrorCode;
import org.AList.common.convention.exception.ClientException;

import java.util.function.Supplier;

public class SentinelUtils {
      
    // 支持传入错误码的版本  
    public static <T> T executeWithSentinel(String resourceName, Supplier<T> supplier, IErrorCode errorCode) {
        Entry entry = null;
        try {  
            entry = SphU.entry(resourceName);
            return supplier.get();  
        } catch (BlockException e) {
            throw new ClientException(errorCode);
        } catch (Exception e) {  
            Tracer.trace(e);
            throw e;  
        } finally {  
            if (entry != null) {  
                entry.exit();  
            }  
        }  
    }  
      
    // 支持传入错误码和自定义消息的版本  
    public static <T> T executeWithSentinel(String resourceName, Supplier<T> supplier,   
                                          IErrorCode errorCode, String customMessage) {  
        Entry entry = null;  
        try {  
            entry = SphU.entry(resourceName);  
            return supplier.get();  
        } catch (BlockException e) {  
            throw new ClientException(customMessage, errorCode);  
        } catch (Exception e) {  
            Tracer.trace(e);  
            throw e;  
        } finally {  
            if (entry != null) {  
                entry.exit();  
            }  
        }  
    }  
      
    // 保留原有的字符串版本以保持向后兼容  
    public static <T> T executeWithSentinel(String resourceName, Supplier<T> supplier, String errorMessage) {  
        return executeWithSentinel(resourceName, supplier, BaseErrorCode.CLIENT_ERR, errorMessage);
    }  
}