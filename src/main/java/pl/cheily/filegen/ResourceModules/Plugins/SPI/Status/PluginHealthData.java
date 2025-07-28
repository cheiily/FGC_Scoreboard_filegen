package pl.cheily.filegen.ResourceModules.Plugins.SPI.Status;

import java.util.List;

public record PluginHealthData(
    List<HealthRecord> healthRecords,
    String message
) {
    public enum HealthStatus {
        READY,
        NOT_READY
    }

    public record HealthRecord(
        String methodName,
        HealthStatus status,
        String message
    ) {}
}
