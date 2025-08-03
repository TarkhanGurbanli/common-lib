package com.tarkhangurbanli.common.lib.annotation;

import com.tarkhangurbanli.common.lib.aspect.LoggingAspect;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables method-level logging for services, controllers, and other components within the given base package.
 * <p>
 * This annotation imports {@link LoggingAspect} and activates AOP-based logging on method entry, exit,
 * and exception handling.
 *
 * <p>If {@code basePackage} is not set, only Spring-managed beans will be logged.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Logs method entry, exit, and arguments at DEBUG level.</li>
 *   <li>Logs exceptions at ERROR level with full stack trace if DEBUG is enabled.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * @EnableLogging(basePackage = "com.mycompany.orderservice")
 * @Configuration
 * public class LoggingConfig {
 * }
 * }</pre>
 *
 * @author Tarkhan Gurbanli
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoggingAspect.class)
public @interface EnableLogging {

    /**
     * The root package to apply logging to.
     * Example: "com.mycompany.orderservice"
     */
    String basePackage() default "";

}
