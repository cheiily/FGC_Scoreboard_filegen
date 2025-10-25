package pl.cheily.filegen.ResourceModules.Plugins;

import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;

public class PluginMultiClassLoader extends ClassLoader {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PluginMultiClassLoader.class);

    private final HashMap<ResourceModuleDefinition, ClassLoader> classLoaders = new HashMap<>();

    public PluginMultiClassLoader() {
    }

    public ClassLoader getClassLoader(ResourceModuleDefinition definition) throws MalformedURLException {
        ClassLoader loader = classLoaders.get(definition);
        if (loader == null) {
            loader = new URLClassLoader(
                    List.of(definition.getInstallFilePath().toUri().toURL()).toArray(new URL[1]),
                    null
            );
            classLoaders.put(definition, loader);
        }
        return loader;
    }

    public boolean isLoaded(ResourceModuleDefinition definition) {
        return classLoaders.containsKey(definition);
    }

    public void unload(ResourceModuleDefinition definition) {
        var loader = classLoaders.get(definition);
        if (loader instanceof URLClassLoader urlClassLoader) {
            try {
                urlClassLoader.close();
            } catch (Exception e) {
                logger.error("Failed graceful unload of classloader for module {{}}!", definition.qualifiedName(), e);
            }
        } else {
            logger.warn("Unexpected classloader type for module {{}} (not URLClassLoader). Cannot do graceful unload!", definition.qualifiedName());
        }
        classLoaders.remove(definition);
    }

    public Class<?> findClass(ResourceModuleDefinition definition) throws ClassNotFoundException {
        ClassLoader loader = null;
        try {
            loader = getClassLoader(definition);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not create a classloader for module \"" + definition.qualifiedName() + "\". " +
                    "Definition might be invalid, try redownloading or contact the module's developer.", e);
        }

        // todo might be required to discover the provider implementation from META-INF/services first
        return Class.forName(definition.serviceInterface(), true, loader);
    }

    @Override
    protected Class<?> findClass(String serviceInterfaceName) throws ClassNotFoundException {
        var loaderKey = classLoaders.keySet().stream()
                .filter(key -> key.serviceInterface().equals(serviceInterfaceName))
                .findFirst()
                .orElseThrow(() -> new ClassNotFoundException("No classloader found for service interface: " + serviceInterfaceName));
        return findClass(loaderKey);
    }

//    @Override
//    public Class<? extends IPluginBase> loadClass(String name) throws ClassNotFoundException {
//        Class<?> clazz = super.loadClass(name);
//        if (!IPluginBase.class.isAssignableFrom(clazz)) {
//            throw new ClassNotFoundException("Class " + name + " does not implement IPluginBase");
//        }
//        ServiceLoader.load(IPluginBase.class);
//        return clazz.asSubclass(IPluginBase.class);
//    }
}