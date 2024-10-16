package org.cynic.music_api.framework.environment;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public final class SystemPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String SYSTEM_PROPERTY_PREFIX = "system.";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        environment.getPropertySources()
            .stream()
            .filter(it -> ClassUtils.isAssignable(it.getClass(), MapPropertySource.class))
            .map(MapPropertySource.class::cast)
            .map(MapPropertySource::getPropertyNames)
            .flatMap(Arrays::stream)
            .distinct()
            .filter(it -> StringUtils.startsWith(it, SYSTEM_PROPERTY_PREFIX))
            .map(it -> Map.entry(
                    StringUtils.substringAfter(it, SYSTEM_PROPERTY_PREFIX),
                    environment.getRequiredProperty(it)
                )
            )
            .forEach(it -> System.setProperty(it.getKey(), it.getValue()));

        Optional.of(environment)
            .filter(CloudPlatform.KUBERNETES::isActive)
            .ifPresentOrElse(_ -> {
                    System.setProperty("hazelcast.join.kubernetes.enabled", "true");
                    System.setProperty("hazelcast.join.tcp-ip.enabled", "false");
                },
                () -> {
                    System.setProperty("hazelcast.join.kubernetes.enabled", "false");
                    System.setProperty("hazelcast.join.tcp-ip.enabled", "true");
                }
            );
    }
}
