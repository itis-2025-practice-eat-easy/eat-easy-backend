package om.technokratos.eateasy.logstarter.aspect;


import om.technokratos.eateasy.logstarter.annotation.NoLogging;
import om.technokratos.eateasy.logstarter.properties.LoggingProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private final LoggingProperties properties;

    public LoggingAspect(LoggingProperties properties) {
        this.properties = properties;
    }

    @Around("execution(public * com.technokratos.eateasy..*.*(..)) && " +
            "(within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Controller *) || " +
            "within(@org.springframework.stereotype.Repository *))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        // Проверка аннотации NoLogging
        if (signature.getMethod().isAnnotationPresent(NoLogging.class)) {
            return joinPoint.proceed();
        }

        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        if (properties.isLogParameters()) {
            Object[] args = joinPoint.getArgs();
            String parameters = Arrays.toString(args);
            logger.info("Entering {}.{} with parameters: {}", className, methodName, parameters);
        } else {
            logger.info("Entering {}.{}", className, methodName);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();

            if (properties.isLogReturnValues() && !void.class.equals(signature.getReturnType())) {
                logger.info("Exiting {}.{} with result: {}", className, methodName, result);
            } else {
                logger.info("Exiting {}.{}", className, methodName);
            }

            if (properties.isLogExecutionTime()) {
                logger.info("Execution time of {}.{}: {} ms", className, methodName, stopWatch.getTotalTimeMillis());
            }

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            logger.error("Exception in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}