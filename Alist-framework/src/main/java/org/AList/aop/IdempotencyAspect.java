package org.AList.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.annotation.Idempotent;
import org.AList.common.convention.exception.ClientException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {  
    private final StringRedisTemplate redisTemplate;
    private final SpelExpressionParser parser = new SpelExpressionParser();
      
    @Around("@annotation(idempotent)")
    public Object idempotentOperation(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String idempotencyKey = generateKey(joinPoint, idempotent);
        log.info("尝试创建幂等键: idempotency:{}", idempotencyKey);

        Boolean isFirstExecution = redisTemplate.opsForValue().setIfAbsent(
                "idempotency:" + idempotencyKey,
                "1",
                idempotent.expiration(),
                TimeUnit.SECONDS
        );

        if (Boolean.TRUE.equals(isFirstExecution)) {
            log.info("成功创建幂等键: idempotency:{}", idempotencyKey);
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                log.info("执行异常，删除幂等键: idempotency:{}", idempotencyKey);
                redisTemplate.delete("idempotency:" + idempotencyKey);
                throw e;
            }
        } else {
            log.info("幂等键已存在，拒绝重复请求: idempotency:{}", idempotencyKey);
            throw new ClientException("检测到重复请求");
        }
    }

    private String generateKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();

        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        String keyValue = parser.parseExpression(idempotent.key()).getValue(context, String.class);
        return idempotent.prefix() + ":" + keyValue;
    }
}