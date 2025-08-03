package com.tarkhangurbanli.common.lib.configuration;

import com.tarkhangurbanli.common.lib.aspect.LoggingAspect;
import com.tarkhangurbanli.common.lib.annotation.EnableLogging;
import java.util.Objects;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Dynamically registers {@link LoggingAspect} with access to basePackage from {@link EnableLogging}.
 */
public class LoggingRegistrar implements ImportBeanDefinitionRegistrar {

    private static final String BASE_PACKAGE_KEY = "basePackage";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String basePackage = (String) Objects.requireNonNull(metadata
                        .getAnnotationAttributes(EnableLogging.class.getName()))
                .get(BASE_PACKAGE_KEY);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(LoggingAspect.class);
        builder.addPropertyValue("basePackage", basePackage);

        registry.registerBeanDefinition("loggingAspect", builder.getBeanDefinition());
    }

}

