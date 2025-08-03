package com.tarkhangurbanli.common.lib.aspect;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * A logging aspect that provides automatic method entry, exit, and exception logging
 * for services and controllers under a configurable base package.
 *
 * <p>If no base package is set, logs all Spring-managed beans under the root package.</p>
 *
 * <p>This aspect logs method calls, execution time, arguments, return values and exceptions.</p>
 *
 * @author Tarkhan Gurbanli
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Configurable base package for logging.
     * Can be overridden by @EnableLogging annotation value via Spring's property injection.
     */
    @Value("${common.lib.base-package:}")
    private String basePackage;

    @PostConstruct
    public void init() {
        if (basePackage.isBlank()) {
            log.warn("[LOGGING] No basePackage provided in @EnableLogging. Logging all Spring-managed beans.");
        } else {
            log.info("[LOGGING] Method-level logging enabled for base package: '{}'", basePackage);
        }
    }

    /**
     * Pointcut that matches all methods within the specified base package.
     */
    @Pointcut("execution(* *(..))")
    private void anyMethod() {}

    @Around("anyMethod()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String declaringClass = joinPoint.getSignature().getDeclaringTypeName();

        // Filter by basePackage if specified
        if (!basePackage.isBlank() && !declaringClass.startsWith(basePackage)) {
            return joinPoint.proceed();
        }

        if (!log.isDebugEnabled()) {
            return joinPoint.proceed();
        }

        String methodName = joinPoint.getSignature().getName();
        String arguments = Arrays.toString(joinPoint.getArgs());

        log.debug("Enter: {}.{}() with arguments = {}", declaringClass, methodName, arguments);

        try {
            Object result = joinPoint.proceed();
            log.debug("Exit: {}.{}() with result = {}", declaringClass, methodName, result);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", arguments, declaringClass, methodName);
            throw e;
        }
    }

    @AfterThrowing(pointcut = "anyMethod()", throwing = "e")
    public void logException(JoinPoint joinPoint, Throwable e) {
        String declaringClass = joinPoint.getSignature().getDeclaringTypeName();

        // Filter by basePackage if specified
        if (!basePackage.isBlank() && !declaringClass.startsWith(basePackage)) {
            return;
        }

        String methodName = joinPoint.getSignature().getName();
        Object cause = getExceptionCause(e);

        if (log.isDebugEnabled()) {
            log.error("Exception in {}.{}() with cause = '{}' and message = '{}'",
                    declaringClass, methodName, cause,
                    StringUtils.hasText(e.getMessage()) ? e.getMessage() : "No message", e);
        } else {
            log.error("Exception in {}.{}() with cause = {}", declaringClass, methodName, cause);
        }
    }

    private Object getExceptionCause(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root != root.getCause()) {
            root = root.getCause();
        }
        return root.getClass().getName();
    }

}
