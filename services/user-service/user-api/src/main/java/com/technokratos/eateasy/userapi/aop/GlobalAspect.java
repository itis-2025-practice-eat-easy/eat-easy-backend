package com.technokratos.eateasy.userapi.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GlobalAspect {

    @Pointcut("execution(* com.technokratos.eateasy.userapi.service.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.technokratos.eateasy.userapi.api.*.*(..))")
    public void apiLayer() {}

    @Around("serviceLayer() || apiLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - startTime;

        log.info("{} method {} completed in {} ms. Result: {}",
                joinPoint.getSignature().getDeclaringTypeName().contains(".service.")
                        ? "Service" : "API",
                joinPoint.getSignature().getName(),
                elapsedTime,
                result);
        return result;
    }

    @Before("serviceLayer() || apiLayer()")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        log.info("Executing method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceLayer() || apiLayer()",
            returning = "result")
    public void logAfterServiceMethod(JoinPoint joinPoint, Object result) {
        log.info("Method {} executed, result: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }
}