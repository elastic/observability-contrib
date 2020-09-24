package de.spinscale.javalin;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.JvmCompilationMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.Log4j2Metrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class Micrometer {

    final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(new PrometheusConfig() {
        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public String prefix() {
            return "javalin";
        }
    });

    public Micrometer() {
        Metrics.addRegistry(registry);
        new JvmGcMetrics().bindTo(Metrics.globalRegistry);
        new JvmHeapPressureMetrics().bindTo(Metrics.globalRegistry);
        new JvmThreadMetrics().bindTo(Metrics.globalRegistry);
        new JvmCompilationMetrics().bindTo(Metrics.globalRegistry);
        new JvmMemoryMetrics().bindTo(Metrics.globalRegistry);
        new Log4j2Metrics().bindTo(Metrics.globalRegistry);
        new UptimeMetrics().bindTo(Metrics.globalRegistry);
        new FileDescriptorMetrics().bindTo(Metrics.globalRegistry);
        new ProcessorMetrics().bindTo(Metrics.globalRegistry);
    }

    public String scrape() {
        return registry.scrape();
    }
}
