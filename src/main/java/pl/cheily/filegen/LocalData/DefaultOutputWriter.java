package pl.cheily.filegen.LocalData;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pl.cheily.filegen.Utils.Util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

public class DefaultOutputWriter implements OutputWriter {

    /**
     * @param resourceName name of the target file to write to
     * @param data         data to save, due to the nature of flag-file handling, the data string should contain the name of the source flag image.
     * @return success value
     * @implNote The default implementation checks if the passed resourceName is a valid ResourcePath-registered path.
     * In case of extension, one would want to omit that safety check and save directly to the passed file.
     */
    @Override
    public boolean writeData(String resourceName, String data) {
        try {
            ResourcePath rp = ResourcePath.of(resourceName);
            if ( rp == null ) return false;
            Path p;
            if ( !Files.exists(p = rp.toPath()) )
                Files.createDirectories(p.getParent());

            //TODO change this to default flag-file extension
            if ( !rp.toString().endsWith(".png") ) {
                BufferedWriter bw = Files.newBufferedWriter(p);
                bw.write(data);
                bw.flush();
            } else {
                Path sourceFlag = Path.of(Util.flagsDir + "/" + data);

                if ( !Files.exists(sourceFlag) ) {
                    sourceFlag = Util.nullFlag;
                    new Alert(Alert.AlertType.WARNING, "Unable to find corresponding file image: " + rp, ButtonType.OK).show();
                }

                Files.copy(sourceFlag, p, StandardCopyOption.REPLACE_EXISTING);
                Files.setLastModifiedTime(p, FileTime.from(Instant.now()));
            }

            return true;

        } catch (IOException ex) {
            return false;
        }
    }
}
