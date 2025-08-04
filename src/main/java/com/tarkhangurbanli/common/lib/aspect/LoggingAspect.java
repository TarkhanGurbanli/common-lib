package com.tarkhangurbanli.common.lib.aspect;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * Aspect for automatic method-level logging with package filtering capabilities.
 *
 * <p>This aspect provides detailed logging for:</p>
 * <ul>
 *   <li>Method entry points (with arguments)</li>
 *   <li>Method exit points (with return values)</li>
 *   <li>Exceptions with root cause analysis</li>
 * </ul>
 *
 * <p>The aspect can be configured to log only specific packages using {@code @EnableLogging} annotation.</p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * @Configuration
 * @EnableLogging(basePackage = "com.myapp.services")
 * public class AppConfig {
 * }
 * }</pre>
 *
 * @author Tarkhan Gurbanli
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1) // Ensure it runs after Spring infrastructure components
public class LoggingAspect {

    /**
     * Base package prefix for filtering logged methods.
     * <p>If empty, logs all Spring components.</p>
     */
    @Setter
    private String basePackage = "";

    /**
     * Pointcut that matches all Spring-managed components.
     * <p>Includes:
     * <ul>
     *   <li>Service implementations</li>
     *   <li>Controllers</li>
     *   <li>Repositories</li>
     *   <li>Components</li>
     * </ul>
     */
    @Pointcut("within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Controller *) || " +
            "within(@org.springframework.stereotype.Repository *) || " +
            "within(@org.springframework.stereotype.Component *) || " +
            "within(@org.springframework.web.bind.annotation.RestController *)")
    public void springComponents() {}

    /**
     * Around advice that logs method entry and exit.
     *
     * @param joinPoint the join point
     * @return the method result
     * @throws Throwable if the intercepted method throws an exception
     */
    @Around("springComponents()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        if (!shouldLog(className)) {
            return joinPoint.proceed();
        }

        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with arguments = {}",
                    className, methodName,
                    args.length > 0 ? Arrays.toString(args) : "[]");
        }

        try {
            Object result = joinPoint.proceed();

            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}",
                        className, methodName,
                        result != null ? result.toString() : "null");
            }
            return result;
        } catch (Exception e) {
            logException(joinPoint, e);
            throw e;
        }
    }

    /**
     * Advice that logs exceptions thrown from methods.
     *
     * @param joinPoint the join point
     * @param e the exception
     */
    @AfterThrowing(pointcut = "springComponents()", throwing = "e")
    public void logException(JoinPoint joinPoint, Throwable e) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        if (!shouldLog(className)) {
            return;
        }

        String methodName = joinPoint.getSignature().getName();
        String rootCause = getRootCause(e);
        String errorMessage = StringUtils.hasText(e.getMessage())
                ? e.getMessage()
                : "No message available";

        log.error("Exception in {}.{}() - Root cause: {} - Message: {}",
                className, methodName, rootCause, errorMessage, e);
    }

    /**
     * Determines whether a class should be logged based on package filtering.
     *
     * @param className the fully qualified class name
     * @return true if logging should be performed, false otherwise
     */
    private boolean shouldLog(String className) {
        return (basePackage.isBlank() || className.startsWith(basePackage))
                && !className.startsWith("org.springframework");
    }

    /**
     * Extracts the root cause of a throwable.
     *
     * @param throwable the exception to analyze
     * @return the class name of the root cause
     */
    private String getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause != rootCause.getCause()) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getClass().getName();
    }

}