package pl.cheily.filegen.LocalData.FileManagement.Meta;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.LocalResourcePath;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;

public abstract class CachedIniDAOBase {
    protected final Logger logger;
    protected final LocalResourcePath path;
    protected final Ini cache;
    protected long cacheChangeTime = -0;

    protected CachedIniDAOBase(LocalResourcePath path) {
        this(path, new Config());
    }

    protected CachedIniDAOBase(LocalResourcePath path, Config config) {
        logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
        this.path = path;
        cache = new Ini();
        cache.setConfig(config);
        try {
            if ( !Files.exists(path.toPath()) ) {
                logger.debug("No pre-existing .ini file. Creating at {}", path);
                if (path.toPath().getParent() != null && !Files.exists(path.toPath().getParent()))
                    Files.createDirectories(path.toPath().getParent());
                Files.createFile(path.toPath());
            }


            File file = path.toPath().toFile();
            cache.load(file);
            cacheChangeTime = file.lastModified();
        } catch (DataManagerNotInitializedException e) {
            logger.error("Attempted Ini-DAO creation for {} while Data Manager isn't initialized.", this.path, e);
        } catch (InvalidFileFormatException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed parsing of {} for data during INI-Dao construction.", this.path, e);
        } catch (SecurityException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed read from {} for data during INI-Dao construction. Access to file denied.", this.path, e);
        } catch (IOException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed read from {} for data during INI-Dao construction.", this.path, e);
        }
    }

    protected boolean store() {
        try {
            if ( !Files.exists(path.toPath()) ) {
                logger.debug("No existing .ini file. Creating at {}", path);
                if (path.toPath().getParent() != null && !Files.exists(path.toPath().getParent()))
                    Files.createDirectories(path.toPath().getParent());
                Files.createFile(path.toPath());
            }

            cache.store(this.path.toPath().toFile());
        } catch (DataManagerNotInitializedException e) {
            logger.warn("Attempted write to {} while Data Manager isn't initialized.", this.path, e);
            return false;
        } catch (IOException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed write to {} for data.", this.path, e);
            return false;
        }
        return true;
    }

    protected boolean cacheInvalid() {
        if (cacheChangeTime == 0) return true;

        try {
            File file = this.path.toPath().toFile();
            return file.lastModified() != cacheChangeTime;
        } catch (DataManagerNotInitializedException e) {
            logger.warn("Attempted cache check from {} while Data Manager isn't initialized.", this.path, e);
        } catch (SecurityException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed read from {} for data during cache check. Access to file was denied.", this.path, e);
        }
        return true;
    }

    public void refresh() {
        try {
            File file = this.path.toPath().toFile();
            cacheChangeTime = file.lastModified();
            cache.clear();
            cache.load(file);
        } catch (DataManagerNotInitializedException e) {
            logger.warn("Attempted read from {} while Data Manager isn't initialized.", this.path, e);
        } catch (InvalidFileFormatException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed parsing of {} for data.", this.path, e);
        } catch (IOException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed read from {} for data.", this.path, e);
        }
    }


}
