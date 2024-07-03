package pl.cheily.filegen.Data;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.Defaults;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class RawOutputWriter implements OutputWriter {
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
     * @param resourceName name of the target file to write to
     * @param data         data to save, due to the nature of flag-file handling, the data string should contain the name of the source flag image.
     * @return success value, always false if writer is disabled
     * @implNote The default implementation checks if the passed resourceName is a valid ResourcePath-registered path.
     * In case of extension, one may want to omit that safety check and save directly to the passed filepath.
     */
    @Override
    public boolean writeData(String resourceName, String... data) {
        if ( !enabled ) return false;

        String formatted = formatter.format(resourceName, data);

        try {
            ResourcePath rPath = ResourcePath.of(resourceName);
            if ( rPath == null ) return false;
            Path filePath;
            if ( !Files.exists(filePath = rPath.toPath()) ) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }

            if ( !rPath.toString().endsWith(Defaults.FLAG_EXTENSION) ) {
                try ( BufferedWriter bw = Files.newBufferedWriter(filePath) ) {
                    bw.write(formatted);
                }
            } else {
                Path sourceFlag = Path.of(dataManager.flagsDir + "/" + formatted);

                if ( sourceFlag.toString().equals(AppConfig.FLAG_EXTENSION()) ) {
                    //empty nationality field - assign null flag
                    sourceFlag = dataManager.nullFlag;
                }
                else if ( !Files.exists(sourceFlag) ) {
                    //nationality was a valid string but unable to find such file - message & assign null flag
                    String t = sourceFlag.getFileName().toString();
                    if ( !t.isEmpty() && !t.equals(AppConfig.FLAG_EXTENSION()) )
                        new Alert(Alert.AlertType.WARNING, "Unable to find corresponding file image: " + t, ButtonType.OK).show();


                    sourceFlag = dataManager.nullFlag;
                }

                Files.copy(sourceFlag, filePath, StandardCopyOption.REPLACE_EXISTING);
                Files.setLastModifiedTime(filePath, FileTime.from(Instant.now()));
            }

            return true;

        } catch (IOException | DataManagerNotInitializedException ex) {
            return false;
        }
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
