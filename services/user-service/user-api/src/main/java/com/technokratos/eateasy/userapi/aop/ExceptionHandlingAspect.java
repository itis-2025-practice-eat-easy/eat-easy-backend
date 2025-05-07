package com.technokratos.eateasy.userapi.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionHandlingAspect {

    @Pointcut("execution(* com.technokratos.eateasy.userapi.service.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.technokratos.eateasy.userapi.api.*.*(..))")
    public void apiLayer() {}

    @AfterThrowing(pointcut = "serviceLayer() || apiLayer()",
            throwing = "ex")
    public void handleException(Exception ex) {
        log.error("Error in file: {}", ex.getStackTrace()[0].getClassName(), ex);
    }

}