package pl.cheily.filegen.ResourceModules.Plugins;

import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;
import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginClassResolutionException;
import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginInstantiationException;
import pl.cheily.filegen.Utils.SafeInvocationUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.jar.JarFile;

public class PluginMultiClassLoader extends ClassLoader {
    private class PluginEntry {
        public final URLClassLoader classLoader;
        public final FileSystem zipFileSystem;
        public ResourceModuleDefinition definition;
        public boolean initialized = false;

        private PluginEntry(URLClassLoader classLoader, FileSystem zipFileSystem, ResourceModuleDefinition definition, boolean initialized) {
            this.classLoader = classLoader;
            this.zipFileSystem = zipFileSystem;
            this.definition = definition;
            this.initialized = initialized;
        }
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PluginMultiClassLoader.class);

    private final HashMap<ResourceModuleDefinition, PluginEntry> classLoaders = new HashMap<>();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Optional<FileSystemProvider> _zipFSP = Optional.empty();
    private static Optional<FileSystemProvider> zipFSP() {
        if (_zipFSP.isEmpty()) {
            _zipFSP = FileSystemProvider.installedProviders().stream().filter(provider -> provider.getScheme().equals("jar")).findFirst();
        }
        return _zipFSP;
    }

    public PluginMultiClassLoader() {
    }

    public PluginEntry getEntry(ResourceModuleDefinition definition) throws MalformedURLException {
        PluginEntry entry = classLoaders.get(definition);
        URLClassLoader loader;
        if (entry == null) {
             loader = new URLClassLoader(
                    List.of(definition.getInstallFilePath().toUri().toURL()).toArray(new URL[1]),
                    getClass().getClassLoader()
            );

            FileSystem fs = zipFSP().isPresent()
                    ? SafeInvocationUtil.getOrNull(() -> zipFSP().get().newFileSystem(URI.create("jar:" + definition.getInstallFilePath().toUri()), Map.of()))
                    : null;

            entry = new PluginEntry(loader, fs, definition, false);
            classLoaders.put(definition, entry);
        }
        return entry;
    }

    public boolean isLoaded(ResourceModuleDefinition definition) {
        return classLoaders.containsKey(definition);
    }

    public void unload(ResourceModuleDefinition definition) {
        var entry = classLoaders.get(definition);
        ClassLoader loader = entry.classLoader;
        if (loader instanceof URLClassLoader urlClassLoader) {
            try {
                urlClassLoader.close();
            } catch (Exception e) {
                logger.error("Failed graceful unload of classloader for module {{}}! Cause: {}", definition.qualifiedName(), e.getMessage(), e);
            }
        } else {
            logger.warn("Unexpected classloader type for module {{}} (not URLClassLoader). Cannot do graceful unload!", definition.qualifiedName());
        }
        try {
            entry.zipFileSystem.close();
        } catch (IOException e) {
            logger.error("Failed to close zip filesystem for module {{}}! Cause: {}", definition.qualifiedName(), e.getMessage(), e);
        }
        classLoaders.remove(definition);
    }

    public Class<?> findClass(ResourceModuleDefinition definition) throws PluginClassResolutionException {
        PluginEntry entry;
        ClassLoader loader;
        try {
            entry = getEntry(definition);
            loader = entry.classLoader;
            if (!entry.initialized)
                loadAllClasses(entry);
        } catch (MalformedURLException e) {
            throw PluginClassResolutionException.forModule(definition, e.getCause());
        }

        try (var spiResource = loader.getResourceAsStream("META-INF/services/" + definition.serviceInterface())) {
            String implementationName;

            if (spiResource == null) throw new IllegalStateException("Cannot find SPI declaration meta-file.");
            implementationName = new String(spiResource.readAllBytes());

            var implLines = implementationName.split("\n");
            if (implLines.length < 1) throw new IllegalStateException("SPI declaration file does not contain any content.");
            if (implLines.length > 1) {
                logger.warn("We only support a singular provider implementation per plugin, using first provider from list.");
                implementationName = implLines[0];
            }

            if (implementationName == null || implementationName.isBlank())
                throw new IllegalStateException("Invalid SPI declaration file content. Cannot parse provider name.");

//            return loader.loadClass(implementationName);
            return Class.forName(implementationName, true, loader);
        } catch (IOException | IllegalStateException | ClassNotFoundException e) {
            throw PluginClassResolutionException.forModule(definition, e);
        }
    }

    public void loadAllClasses(PluginEntry pluginEntry) throws PluginClassResolutionException {
        try (JarFile jarFile = new JarFile(pluginEntry.definition.getInstallFilePath().toFile())) {
            var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                var jarEntry = entries.nextElement();
                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                String className = jarEntry.getName()
                        .substring(0, jarEntry.getName().length() - 6)
                        .replace('/', '.');

                Class.forName(className, true, pluginEntry.classLoader);
            }
            pluginEntry.initialized = true;
        } catch (IOException | ClassNotFoundException e) {
            throw PluginClassResolutionException.forModule(pluginEntry.definition, e);
        }
    }
}