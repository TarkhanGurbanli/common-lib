package com.tarkhangurbanli.common.lib.aspect;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * Aspect for automatic method-level logging.
 *
 * <p>Supports filtering by base package and logs:</p>
 * <ul>
 *   <li>Method entry and exit (with arguments and result) at DEBUG level</li>
 *   <li>Exceptions at ERROR level</li>
 * </ul>
 *
 * @author Tarkhan Gurbanli
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Base package to restrict logging to (injected dynamically via registrar).
     */
    @Setter
    private String basePackage = "";

    @Pointcut("execution(* *(..))")
    private void anyMethod() {}

    @Around("anyMethod()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        if (!shouldLog(className)) return joinPoint.proceed();

        String methodName = joinPoint.getSignature().getName();
        String arguments = Arrays.toString(joinPoint.getArgs());

        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with arguments = {}", className, methodName, arguments);
        }

        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}", className, methodName, result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", arguments, className, methodName);
            throw e;
        }
    }

    @AfterThrowing(pointcut = "anyMethod()", throwing = "e")
    public void logException(JoinPoint joinPoint, Throwable e) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        if (!shouldLog(className)) return;

        String methodName = joinPoint.getSignature().getName();
        String cause = getRootCause(e);

        if (log.isDebugEnabled()) {
            log.error("Exception in {}.{}() with cause = '{}' and message = '{}'",
                    className, methodName, cause,
                    StringUtils.hasText(e.getMessage()) ? e.getMessage() : "No message", e);
        } else {
            log.error("Exception in {}.{}() with cause = {}", className, methodName, cause);
        }
    }

    private boolean shouldLog(String className) {
        return basePackage.isBlank() || className.startsWith(basePackage);
    }

    private String getRootCause(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root != root.getCause()) {
            root = root.getCause();
        }
        return root.getClass().getName();
    }

}
