package com.tarkhangurbanli.common.lib.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties bound to prefix: {@code spring.jpa.sql-logging}.
 *
 * <p>This configuration controls whether SQL logging is enabled and
 * the default log level to be used.</p>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.jpa.sql-logging")
public class SqlLoggingProperties {

    /**
     * Whether SQL logging is enabled.
     * Must be explicitly set to {@code true} in application configuration.
     */
    private boolean enabled = false;

    /**
     * Log level: INFO, DEBUG, or ERROR.
     * Defaults to INFO.
     */
    private LogLevel level = LogLevel.INFO;

    public enum LogLevel {
        INFO,
        DEBUG,
        ERROR
    }

}
