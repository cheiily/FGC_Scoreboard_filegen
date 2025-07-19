package pl.cheily.filegen.ResourceModules.Plugins.SPI;

import java.util.ArrayList;
import java.util.List;

public class PluginHealthData {
    public enum HealthStatus {
        READY,
        NOT_READY
    }

    public record HealthRecord(
        String methodName,
        HealthStatus status,
        String message
    ) {}

    public List<HealthRecord> healthRecords = new ArrayList<>();
    public String message = "";
}
