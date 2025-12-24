package dev.observability.autoconfigure;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;

import java.util.Map;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.observability.properties.ObservabilityProperties;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ObservabilityProperties.class)
@ConditionalOnProperty(prefix = "observability", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MetricsConfiguration {
    private final ObservabilityProperties properties;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            Map<String, String> commonTags = properties.getMetrics().getCommonTags();
            
            if (!commonTags.isEmpty()) {
                commonTags.forEach(registry.config()::commonTags);
            }
        };
    }

    @Bean
    public MeterFilter meterFilter() {
        return MeterFilter.denyNameStartsWith("tomcat.");
    }
}
