package com.tarkhangurbanli.common.lib.aspect;

import com.tarkhangurbanli.common.lib.properties.SqlLoggingProperties;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging SQL-related method calls from Spring Data JPA repositories.
 *
 * <p>This aspect becomes active only if {@code spring.jpa.sql-logging.enabled=true}.</p>
 *
 * <p>It logs repository method calls at different levels:</p>
 * <ul>
 *   <li><strong>INFO</strong>: Basic method invocation log</li>
 *   <li><strong>DEBUG</strong>: Method arguments, target class and method name</li>
 *   <li><strong>ERROR</strong>: Any exception thrown from repository layer</li>
 * </ul>
 *
 * <p>To configure log level, use property: {@code spring.jpa.sql-logging.level}</p>
 *
 * @author Tarkhan Gurbanli
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.jpa.sql-logging", name = "enabled", havingValue = "true")
public class SqlLoggingAspect {

    private final SqlLoggingProperties properties;

    @PostConstruct
    public void onStart() {
        log.info("[SQL LOGGING] SqlLoggingAspect initialized. Enabled={}, Level={}", properties.isEnabled(), properties.getLevel());
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.*(..))")
    public void repositoryMethods() {
        // Pointcut for Spring Data JPA
    }

    @Before("repositoryMethods()")
    public void logSqlCall(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        switch (properties.getLevel()) {
            case INFO -> log.info("[SQL] {}.{}() called", className, methodName);
            case DEBUG -> {
                log.debug("[SQL][DEBUG] {}.{}() called with args: {}", className, methodName, Arrays.toString(joinPoint.getArgs()));
            }
            default -> {
                // Do not log
            }
        }
    }

    @AfterThrowing(pointcut = "repositoryMethods()", throwing = "ex")
    public void logSqlError(JoinPoint joinPoint, Throwable ex) {
        if (properties.getLevel() == SqlLoggingProperties.LogLevel.ERROR || log.isErrorEnabled()) {
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            log.error("[SQL][ERROR] Exception in {}.{}(): {}", className, methodName, ex.getMessage(), ex);
        }
    }

}
