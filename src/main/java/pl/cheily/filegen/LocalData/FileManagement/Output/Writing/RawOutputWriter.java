package pl.cheily.filegen.LocalData.FileManagement.Output.Writing;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.Defaults;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatter;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class RawOutputWriter implements OutputWriter {
    private final static Logger logger = LoggerFactory.getLogger(RawOutputWriter.class);
    private OutputFormatter formatter;
    private boolean enabled = true;
    private final String name;

    public RawOutputWriter(String name, OutputFormatter formatter) {
        this.name = name;
        this.formatter = formatter;
    }


    /**
     * By contract, the writer will return false and not execute any further writes if it is disabled.
     * For proper error-handling however, it is upon the prompter to make a preemptive check of the writer's state.
     *
     * @return success value, always false if writer is disabled
     * @implNote The default implementation checks if the passed resourceName is a valid ResourcePath-registered path.
     * In case of extension, one may want to omit that safety check and save directly to the passed filepath.
     */
    @Override
    public boolean writeData() {
        if ( !dataManager.isInitialized() || !enabled || formatter == null ) return false;

        List<String> failedResources = new ArrayList<>();
        for (var entry : formatter.getAllFormatted().entrySet()) {
            try {
                if (entry.getKey() == null || entry.getKey().isHTML())
                    break;

                Path filePath;
                if (!Files.exists(filePath = entry.getKey().toPath())) {
                    Files.createDirectories(filePath.getParent());
                    Files.createFile(filePath);
                }

                if (entry.getKey() == ResourcePath.P1_FLAG || entry.getKey() == ResourcePath.P2_FLAG || entry.getKey() == ResourcePath.C1_FLAG || entry.getKey() == ResourcePath.C2_FLAG || entry.getKey() == ResourcePath.C3_FLAG) {
                    Path sourceFlag = Path.of(dataManager.flagsDir + "/" + entry.getValue());

                    if (sourceFlag.toString().equals(AppConfig.FLAG_EXTENSION()) || entry.getValue().isEmpty()) {
                        //empty nationality field - assign null flag
                        sourceFlag = dataManager.nullFlag;
                    } else if (!Files.exists(sourceFlag)) {
                        //nationality was a valid string but unable to find such file - message & assign null flag
                        String t = sourceFlag.getFileName().toString();
                        if (!t.isEmpty() && !t.equals(AppConfig.FLAG_EXTENSION())) {
                            logger.error("Unable to find corresponding file image: " + t);
                            failedResources.add(entry.getKey().toString());
                        }


                        sourceFlag = dataManager.nullFlag;
                    }

                    Files.copy(sourceFlag, filePath, StandardCopyOption.REPLACE_EXISTING);
                    Files.setLastModifiedTime(filePath, FileTime.from(Instant.now()));
                } else {
                    BufferedWriter bw = Files.newBufferedWriter(entry.getKey().toPath());
                    bw.write(entry.getValue());
                    bw.close();
                }

            } catch (IOException | DataManagerNotInitializedException ex) {
                failedResources.add(entry.getKey().toString());
                logger.error("Failed to write resource: " + entry.getKey().toString(), ex);
            }
        }

        if (!failedResources.isEmpty()) {
            logger.trace(MarkerFactory.getMarker("ALERT"), "Couldn't save resources: " + failedResources + ". Other resources were saved successfully.");
            logger.error(getName() + " failed to write resources formatted via " + formatter.getName() + " to: " + failedResources);
            return false;
        }
        return true;
    }

    @Override
    public OutputFormatter getFormatter() {
        return formatter;
    }

    @Override
    public void setFormatter(OutputFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }
}
