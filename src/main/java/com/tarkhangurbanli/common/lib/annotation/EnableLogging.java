package com.tarkhangurbanli.common.lib.annotation;

import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

/**
 * Enables method-level logging for services, controllers, and components
 * inside the specified {@code basePackage}.
 *
 * <p>This annotation activates AOP-based method entry, exit, and exception logging.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Logs method entry/exit at DEBUG level with arguments and return values.</li>
 *   <li>Logs exceptions at ERROR level with cause and optional stack trace.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * @EnableLogging(basePackage = "com.myapp")
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
@Import(com.tarkhangurbanli.common.lib.configuration.LoggingRegistrar.class)
public @interface EnableLogging {

    /**
     * Package to be scanned and logged.
     * Example: "com.example.service"
     */
    String basePackage() default "";
}
