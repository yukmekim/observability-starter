package dev.observability.autoconfigure;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;

import java.util.List;
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
    public MeterFilter excludeUrlPatternFilter() {
        List<String> patterns = properties.getMetrics().getExcludePatterns();

        return MeterFilter.deny(id -> {
            String uri = id.getTag("uri");
            if (uri == null) {
                return false;
            }

            return patterns.stream()
                .anyMatch(pattern -> matchesPattern(uri, pattern));
        });
    }

    private boolean matchesPattern(String uri, String pattern) {
        String regex = pattern
            .replace("**", ".*")
            .replace("*", "[^/]*")
            .replace("/", "\\/");
        return uri.matches("^" + regex + "$");
    }

    @Bean
    public MeterFilter meterFilter() {
        return MeterFilter.denyNameStartsWith("tomcat.");
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.metrics.percentiles", name = "enabled", havingValue = "true")
    public MeterFilter percentilesFilter() {
        List<Double> percentiles = properties.getMetrics().getPercentiles().getPercentiles();
        double[] percentileValues = percentiles.stream()
            .mapToDouble(Double::doubleValue)
            .toArray();

        return new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {

                if(id.getName().equals("http.server.requests")) {
                    return DistributionStatisticConfig.builder()
                        .percentiles(percentileValues)
                        .build()
                        .merge(config);
                }
                return config;
            }
        };
    }
}
