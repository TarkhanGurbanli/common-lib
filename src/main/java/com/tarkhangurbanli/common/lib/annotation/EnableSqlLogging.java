package com.tarkhangurbanli.common.lib.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables SQL-level logging for Spring Data JPA repository method calls.
 *
 * <p>This annotation activates logging if the following property is set in your
 * {@code application.yml} or {@code application.properties}:</p>
 *
 * <pre>
 * spring:
 *   jpa:
 *     sql-logging:
 *       enabled: true
 * </pre>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @EnableSqlLogging
 * @Configuration
 * public class LoggingConfig {
 * }
 * }</pre>
 *
 * <p>INFO, DEBUG, and ERROR log levels are used to control log verbosity.</p>
 *
 * @author Tarkhan Gurbanli
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSqlLogging {
}
