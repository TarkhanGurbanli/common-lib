package com.tarkhangurbanli.common.lib.configuration;

import com.tarkhangurbanli.common.lib.annotation.EnableSqlLogging;
import com.tarkhangurbanli.common.lib.properties.SqlLoggingProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configuration for enabling SQL logging via {@code @EnableSqlLogging}.
 *
 * <p>This is only active if {@code @EnableSqlLogging} is present and
 * {@code spring.jpa.sql-logging.enabled=true}.</p>
 *
 * @author Tarkhan Gurbanli
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(EnableSqlLogging.class)
@EnableConfigurationProperties(SqlLoggingProperties.class)
public class SqlLoggingAutoConfiguration {
    // No additional beans needed. Aspect is auto-discovered.
}
