package dev.observability.metrics;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomMetrics {
    private final MeterRegistry registry;

    public void incrementCounter(String name, String... tags) {
        Counter.builder(name)
            .tags(tags)
            .register(registry)
            .increment();
    }

    public void recordTimer(String name, long duration, TimeUnit unit, String... tags) {
        Timer.builder(name)
            .tags(tags)
            .register(registry)
            .record(duration, unit);
    }

    public <T extends Number> void registerGauge(String name, T value, String... tags) {
        registry.gauge(name, Tags.of(tags), value);
    }
}
