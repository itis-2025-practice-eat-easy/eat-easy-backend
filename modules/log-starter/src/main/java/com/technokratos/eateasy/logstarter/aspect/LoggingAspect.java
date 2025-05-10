package com.technokratos.eateasy.logstarter.aspect;

import com.technokratos.eateasy.logstarter.properties.LoggingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.technokratos.eateasy.logstarter.annotation.NoLogging;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final LoggingProperties properties;

    @Around("execution(public * *(..)) && " +
            "(within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Repository *) || " +
            "within(@org.springframework.stereotype.Controller *) || " +
            "within(@org.springframework.web.bind.annotation.RestController *))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        if (signature.getMethod().isAnnotationPresent(NoLogging.class)) {
            return joinPoint.proceed();
        }
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        if (properties.isLogParameters()) {
            Object[] args = joinPoint.getArgs();
            String parameters = Arrays.toString(args);
            log.info("Entering {}.{} with parameters: {}", className, methodName, parameters);
        } else {
            log.info("Entering {}.{}", className, methodName);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            if (properties.isLogReturnValues() && !void.class.equals(signature.getReturnType())) {
                log.info("Exiting {}.{} with result: {}", className, methodName, result);
            } else {
                log.info("Exiting {}.{}", className, methodName);
            }
            if (properties.isLogExecutionTime()) {
                log.info("Execution time of {}.{}: {} ms", className, methodName, stopWatch.getTotalTimeMillis());
            }
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.info("Exception in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}