package org.AList.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.annotation.MyLog;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.AbstractException;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.UserException;
import org.AList.common.limiter.LogRateLimiter;
import org.AList.common.limiter.UserLogRateLimiter;
import org.AList.domain.dao.entity.OperLogDO;
import org.AList.domain.dao.mapper.OperLogMapper;
import org.AList.utils.LinkUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import static org.AList.common.convention.errorcode.BaseErrorCode.*;


/**
 * 切面处理类，记录操作日志到数据库
 */

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperLogAspect{
    private final OperLogMapper operLogMapper;
    private final LogRateLimiter logRateLimiter;
    private final UserLogRateLimiter userLogRateLimiter;
    //为了记录方法的执行时间
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 设置操作日志切入点，这里介绍两种方式：
     * 1、基于注解切入（也就是打了自定义注解的方法才会切入）
     *    &#064;Pointcut("@annotation(org.Alist.annotation.MyLog)")
     * 2、基于包扫描切入
     *    &#064;Pointcut("execution(public  * org.Alist.controller..*.*(..))")
     */
    @Pointcut("@annotation(org.AList.annotation.MyLog)")
    public void operLogPointCut() {

    }

    @Before("operLogPointCut()")
    public void beforeMethod(JoinPoint point){
        startTime.set(System.currentTimeMillis());
    }

    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut("execution(* org.AList.controller..*.*(..))")
    public void operExceptionLogPointCut() {
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param result      返回结果
     */
    @AfterReturning(value = "operLogPointCut()", returning = "result")
    public void SaveOperLog(JoinPoint joinPoint,Object result){
        // 先尝试获取限流许可
        if (!logRateLimiter.tryAcquire()) {
            // 限流处理
            System.out.println("日志记录被系统限流，方法: " + joinPoint.getSignature().getName());
            return;
        }
        if(!userLogRateLimiter.tryAcquire(StuIdContext.getStudentId())){
            // 限流处理
            System.out.println("日志记录被系统限流，方法: " + joinPoint.getSignature().getName());
            return;
        }


        RequestAttributes requestAttributes= RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new UserException(EMPTY_PARAM);                                                                        //A0002：请求参数为空
        }
        HttpServletRequest request=(HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        try{
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            MyLog myLog = method.getAnnotation(MyLog.class);

            OperLogDO operLog = new OperLogDO();
            if (myLog != null) {
                operLog.setTitle(myLog.title());//设置模块名称
                operLog.setContent(myLog.content());//设置日志内容
            }
            // 将入参转换成json
            String params = argsArrayToString(joinPoint.getArgs());
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName + "()";
            operLog.setMethod(methodName); //设置请求方法
            if (request != null) {
                operLog.setRequestMethod(request.getMethod());//设置请求方式
            }
            operLog.setRequestParam(params); // 请求参数
            operLog.setResponseResult(JSON.toJSONString(result));
            String studentId = null;
            if (request != null) {
                studentId = request.getHeader("studentId");
                if (studentId == null || studentId.isEmpty()) {
                    studentId = "anonymous";
                }
            }
            // 返回结果
            operLog.setOperName(studentId);
            if (request != null) {
                operLog.setIp(LinkUtil.getActualIp(request));
            }
            if (request != null) {
                operLog.setRequestUrl(request.getRequestURI()); // 请求URI
            }
            operLog.setOperTime(new Date()); // 时间
            operLog.setStatus(0);//操作状态（0正常 1异常）
            Long takeTime = System.currentTimeMillis() - startTime.get();//记录方法执行耗时时间（单位：毫秒）
            operLog.setTakeTime(takeTime);
            operLogMapper.insert(operLog);
        }catch (Exception e) {
            throw new ClientException(e.getMessage());
        }

    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     */
    @AfterThrowing(pointcut = "operExceptionLogPointCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e){
        // 先尝试获取限流许可
        if (!logRateLimiter.tryAcquire()) {
            // 限流处理
            System.out.println("日志记录被限流，方法: " + joinPoint.getSignature().getName());
            return;
        }

        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        if (requestAttributes == null) {
            throw new UserException(EMPTY_PARAM);                                                                        //A0002：请求参数为空
        }
        HttpServletRequest request=(HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        OperLogDO operLog = new OperLogDO();

        try{
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName + "()";
            // 获取操作
            MyLog myLog = method.getAnnotation(MyLog.class);
            if (myLog != null) {
                operLog.setTitle(myLog.title());//设置模块名称
                operLog.setContent(myLog.content());//设置日志内容
            }
            // 将入参转换成json
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setMethod(methodName); //设置请求方法
            if (request != null) {
                operLog.setRequestMethod(request.getMethod());//设置请求方式
            }
            operLog.setRequestParam(params); // 请求参数
            if(StuIdContext.getStudentId()==null){
                operLog.setOperName("Login");
            }
            operLog.setOperName(StuIdContext.getStudentId());
            if (request != null) {
                operLog.setIp(LinkUtil.getActualIp(request));
            }
            if (request != null) {
                operLog.setRequestUrl(request.getRequestURI()); // 请求URI
            }
            operLog.setOperTime(new Date()); // 时间
            operLog.setStatus(1);//操作状态（0正常 1异常）
            operLog.setErrorMsg(stackTraceToString(e));//记录异常信息
            operLogMapper.insert(operLog);

        }catch (Exception e2) {
            log.error("记录异常日志失败", e2);
        }

    }
    /**
     * 转换异常信息为字符串（适配AbstractException体系）
     */
    public String stackTraceToString(Throwable e) {
        // 1. 处理AbstractException及其子类
        if (e instanceof AbstractException ae) {
            StringBuilder sb = new StringBuilder();

            // 添加错误码和错误信息
            sb.append("ErrorCode: ").append(ae.getErrorCode()).append("\n");
            sb.append("ErrorMessage: ").append(ae.getErrorMessage()).append("\n");

            // 添加原始异常信息（如果有）
            if (e.getCause() != null) {
                sb.append("RootCause: ").append(e.getCause().getClass().getName()).append("\n");
                sb.append("RootMessage: ").append(e.getCause().getMessage()).append("\n");
            }

            // 添加关键堆栈信息（限制行数）
            appendRelevantStackTrace(e, sb, 10);

            return substring(sb.toString(), 0, 2000);
        }

        // 2. 处理普通异常
        return stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace());
    }

    /**
     * 添加最相关的堆栈信息（限制行数）
     */
    private void appendRelevantStackTrace(Throwable e, StringBuilder sb, int maxLines) {
        sb.append("StackTrace: \n");
        StackTraceElement[] stackTrace = e.getStackTrace();
        int lines = Math.min(stackTrace.length, maxLines);

        for (int i = 0; i < lines; i++) {
            sb.append("\tat ").append(stackTrace[i]).append("\n");
        }

        if (stackTrace.length > maxLines) {
            sb.append("\t... ").append(stackTrace.length - maxLines)
                    .append(" more lines omitted\n");
        }
    }

    /**
     * 保留原有方法作为兼容
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage,
                                     StackTraceElement[] elements) {
        StringBuilder sb = new StringBuilder();
        sb.append(exceptionName).append(": ").append(exceptionMessage).append("\n");
        appendRelevantStackTraceElements(sb, elements, 15);
        return substring(sb.toString(), 0, 2000);
    }

    private void appendRelevantStackTraceElements(StringBuilder sb,
                                                  StackTraceElement[] elements,
                                                  int maxLines) {
        int lines = Math.min(elements.length, maxLines);
        for (int i = 0; i < lines; i++) {
            sb.append("\tat ").append(elements[i]).append("\n");
        }
        if (elements.length > maxLines) {
            sb.append("\t... ").append(elements.length - maxLines)
                    .append(" more lines omitted\n");
        }
    }

    //字符串截取
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        } else {
            if (end < 0) {
                end += str.length();
            }

            if (start < 0) {
                start += str.length();
            }

            if (end > str.length()) {
                end = str.length();
            }

            if (start > end) {
                return "";
            } else {
                if (start < 0) {
                    start = 0;
                }

                if (end < 0) {
                    end = 0;
                }
                return str.substring(start, end);
            }
        }
    }


    /**
     * 安全的参数拼装方法
     */
    private String argsArrayToString(Object[] paramsArray) {
        if (paramsArray == null || paramsArray.length == 0) {
            return "[]";
        }

        StringBuilder params = new StringBuilder("[");
        for (Object o : paramsArray) {
            try {
                if (o == null) {
                    params.append("null");
                } else if (isBasicType(o)) {
                    // 基本类型直接输出
                    params.append(o.toString());
                } else if (o instanceof HttpServletRequest || o instanceof HttpServletResponse) {
                    // 特殊Web对象处理
                    params.append(o.getClass().getSimpleName());
                } else {
                    // 其他对象尝试JSON序列化
                    params.append(toSafeJsonString(o));
                }
                params.append(", ");
            } catch (Exception e) {
                // 记录错误但继续处理其他参数
                params.append("<JSON_ERROR: ").append(e.getClass().getSimpleName())
                        .append(" - ").append(e.getMessage()).append(">, ");
                log.warn("参数JSON序列化失败: {}", e.getMessage());
            }
        }

        // 移除最后的逗号和空格
        if (params.length() > 1) {
            params.setLength(params.length() - 2);
        }
        params.append("]");

        return params.toString();
    }

    /**
     * 判断是否是基本类型
     */
    private boolean isBasicType(Object o) {
        return o instanceof String || o instanceof Number || o instanceof Boolean
                || o instanceof Character || o instanceof Date;
    }

    /**
     * 安全的JSON序列化
     */
    private String toSafeJsonString(Object o) {
        try {
            return JSON.toJSONString(o,
                    SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.IgnoreNonFieldGetter,
                    SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            // 序列化失败时返回简化信息
            return "{" +
                    "\"class\":\"" + o.getClass().getName() + "\"," +
                    "\"toString\":\"" + o.toString().replace("\"", "'") + "\"" +
                    "}";
        }
    }






}