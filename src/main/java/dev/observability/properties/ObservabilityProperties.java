package dev.observability.properties;

import java.util.HashMap;
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
    }

    @Data
    public static class Logging {
        private boolean includeHeaders = false;
        private boolean includeQueryString = true;
        private boolean includePayload = false;
        private int maxPayloadLength = 1000;
    }
}
