package org.AList.aop;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.AList.annotation.MyLog;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
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
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 切面处理类，记录操作日志到数据库
 */

@Aspect
@Component
@RequiredArgsConstructor
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
            throw new ClientException("请求为空");
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
            operLog.setResponseResult(JSON.toJSONString(result)); // 返回结果
            operLog.setOperName(StuIdContext.getStudentId());
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
            throw new ClientException("请求为空");
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
            operLog.setOperName(StuIdContext.getStudentId());
            if (request != null) {
                operLog.setIp(LinkUtil.getActualIp(request));
            }
            if (request != null) {
                operLog.setRequestUrl(request.getRequestURI()); // 请求URI
            }
            operLog.setOperTime(new Date()); // 时间
            operLog.setStatus(1);//操作状态（0正常 1异常）
            operLog.setErrorMsg(stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace()));//记录异常信息
            operLogMapper.insert(operLog);

        }catch (Exception e2) {
            throw new ClientException(e2.getMessage());
        }

    }
    /**
     * 转换异常信息为字符串
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement stet : elements) {
            stringBuilder.append(stet).append("\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + stringBuilder.toString();
        message = substring(message,0 ,2000);
        return message;
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
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray)
    {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null)
        {
            for (Object o : paramsArray)
            {
                if (o != null)
                {
                    try
                    {
                        Object jsonObj = JSON.toJSON(o);
                        params.append(jsonObj.toString()).append(" ");
                    }
                    catch (Exception e)
                    {
                        throw new ClientException(e.getMessage());
                    }
                }
            }
        }
        return params.toString().trim();
    }






}