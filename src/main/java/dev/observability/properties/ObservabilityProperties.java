package dev.observability.properties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "observability")
public class ObservabilityProperties {
    private boolean enabled;
    private Metrics metrics = new Metrics();
    private Logging logging = new Logging();

    @Data
    public static class Metrics {
        private Map<String, String> commonTags = new HashMap<>();
        private boolean customMetricsEnabled = true;

        private List<String> excludePatterns = Arrays.asList(
            "/actuator/**",
            "/error",
            "/favicon.ico",
            "/swagger-ui/**",
            "/v3/api-docs/**"
        );

        private Percentiles percentiles = new Percentiles();
    }

    @Data
    public static class Percentiles {
        private boolean enabled = false;
        private List<Double> percentiles = Arrays.asList(0.5, 0.95, 0.99);
    }

    @Data
    public static class Logging {
        private boolean includeHeaders = false;
        private boolean includeQueryString = true;
        private boolean includePayload = false;
        private int maxPayloadLength = 1000;
    }
}
