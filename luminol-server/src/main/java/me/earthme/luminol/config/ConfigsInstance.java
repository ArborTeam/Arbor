package me.earthme.luminol.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.papermc.paper.threadedregions.RegionizedServer;
import me.earthme.luminol.commands.ConfigCommand;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.DoNotLoad;
import me.earthme.luminol.config.flags.HotReloadUnsupported;
import me.earthme.luminol.config.flags.TransformedConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ConfigsInstance {
    public final Logger logger = LogManager.getLogger();
    private final File baseConfigFolder;
    private final File baseConfigFile;
    private final String name;
    private final String pack;
    private final Set<IConfigModule> allInstanced = new HashSet<>();
    private final Map<String, Object> stagedConfigMap = new HashMap<>();
    private final Map<String, Object> defaultvalueMap = new HashMap<>();
    public boolean alreadyInit = false;
    private CommentedFileConfig configFileInstance;

    public ConfigsInstance(@NotNull File base, @NotNull String name, @NotNull String pack) {
        this.baseConfigFolder = base;
        this.name = name;
        this.pack = pack;
        this.baseConfigFile = new File(base, name + "_global_config.toml");
    }

    public void setupLatch() {
        ConfigCommand command = new ConfigCommand(name);
        Bukkit.getCommandMap().register(name + "config", name, command);
        command.initConfig(this);
        alreadyInit = true;
    }

    public void reload() {
        RegionizedServer.ensureGlobalTickThread("Reload " + name + " config off global region thread!");

        dropAllInstanced();
        try {
            preLoadConfig();
            finalizeLoadConfig();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Contract(" -> new")
    public @NotNull CompletableFuture<Void> reloadAsync() {
        return CompletableFuture.runAsync(this::reload, task -> RegionizedServer.getInstance().addTask(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error(e);
            }
        }));
    }

    public void dropAllInstanced() {
        allInstanced.clear();
    }

    public void finalizeLoadConfig() {
        for (IConfigModule module : allInstanced) {
            module.onLoaded(configFileInstance);
        }
        setupLatch();
    }

    public void preLoadConfig() throws IOException {
        baseConfigFolder.mkdirs();

        if (!baseConfigFile.exists()) {
            baseConfigFile.createNewFile();
        }

        configFileInstance = CommentedFileConfig.of(baseConfigFile);

        configFileInstance.load();

        try {
            instanceAllModule();
            loadAllModules();
        } catch (Exception e) {
            logger.error("Failed to load config modules!", e);
            throw new RuntimeException(e);
        }

        saveConfigs();
    }

    private void loadAllModules() throws IllegalAccessException {
        for (IConfigModule instanced : allInstanced) {
            loadForSingle(instanced);
        }
    }

    private void instanceAllModule() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Class<?> clazz : getClasses(pack)) {
            if (IConfigModule.class.isAssignableFrom(clazz)) {
                allInstanced.add((IConfigModule) clazz.getConstructor().newInstance());
            }
        }
    }

    private void loadForSingle(@NotNull IConfigModule singleConfigModule) throws IllegalAccessException {
        final EnumConfigCategory category = singleConfigModule.getCategory();

        Field[] fields = singleConfigModule.getClass().getDeclaredFields();

        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                boolean skipLoad = field.getAnnotation(DoNotLoad.class) != null || (alreadyInit && field.getAnnotation(HotReloadUnsupported.class) != null);
                ConfigInfo configInfo = field.getAnnotation(ConfigInfo.class);

                if (skipLoad || configInfo == null) {
                    continue;
                }

                final String fullConfigKeyName = category.getBaseKeyName() + "." + singleConfigModule.getBaseName() + "." + configInfo.baseName();

                field.setAccessible(true);
                final Object currentValue = field.get(null);
                if (!alreadyInit) defaultvalueMap.put(fullConfigKeyName, currentValue);
                boolean removed = fullConfigKeyName.equals("removed.removed_config.removed");

                if (!configFileInstance.contains(fullConfigKeyName) || removed) {
                    for (TransformedConfig transformedConfig : field.getAnnotationsByType(TransformedConfig.class)) {
                        final String oldConfigKeyName = String.join(".", transformedConfig.category()) + "." + transformedConfig.name();
                        if (!Objects.equals(transformedConfig.originInstance(), "")) {
                            ConfigManager.registerTransformedConfig(transformedConfig.originInstance(), name, oldConfigKeyName, fullConfigKeyName, transformedConfig);
                        } else {
                            Object oldValue = configFileInstance.get(oldConfigKeyName);
                            if (oldValue != null) {
                                boolean success = true;
                                if (transformedConfig.transform() && !removed) {
                                    try {
                                        for (Class<? extends DefaultTransformLogic> logic : transformedConfig.transformLogic()) {
                                            oldValue = logic.getDeclaredConstructor().newInstance().transform(oldValue);
                                        }
                                        configFileInstance.set(fullConfigKeyName, oldValue);
                                    } catch (Exception e) {
                                        success = false;
                                        logger.error("Failed to transform removed config {}!", transformedConfig.name());
                                    }

                                    if (transformedConfig.transformComments()) {
                                        configFileInstance.setComment(fullConfigKeyName, configFileInstance.getComment(oldConfigKeyName));
                                    }
                                }

                                if (success) removeConfig(oldConfigKeyName, transformedConfig.category());
                                final String comments = configInfo.comments();

                                if (!comments.isBlank()) configFileInstance.setComment(fullConfigKeyName, comments);

                                if (!removed && configFileInstance.get(fullConfigKeyName) != null) break;
                            }
                        }
                    }
                    if (removed) {
                        configFileInstance.remove("removed");
                        continue;
                    }
                    if (configFileInstance.get(fullConfigKeyName) != null) continue;
                    if (currentValue == null) {
                        throw new UnsupportedOperationException("Config " + singleConfigModule.getBaseName() + "tried to add an null default value!");
                    }

                    final String comments = configInfo.comments();

                    if (!comments.isBlank()) {
                        configFileInstance.setComment(fullConfigKeyName, comments);
                    }

                    configFileInstance.add(fullConfigKeyName, currentValue);
                    continue;
                }

                Object actuallyValue;
                if (stagedConfigMap.containsKey(fullConfigKeyName)) {
                    actuallyValue = stagedConfigMap.get(fullConfigKeyName);
                    if (actuallyValue == null) actuallyValue = defaultvalueMap.get(fullConfigKeyName);
                    stagedConfigMap.remove(fullConfigKeyName);
                } else {
                    actuallyValue = configFileInstance.get(fullConfigKeyName);
                }
                try {
                    actuallyValue = tryTransform(field.get(null).getClass(), actuallyValue);
                    configFileInstance.set(fullConfigKeyName, actuallyValue);
                } catch (IllegalFormatConversionException e) {
                    resetConfig(fullConfigKeyName);
                    logger.error("Failed to transform config {}, reset to default!", fullConfigKeyName);
                }
                field.set(null, actuallyValue);
            }
        }
    }

    public void removeConfig(String name, String[] keys) {
        configFileInstance.remove(name);
        Object configAtPath = configFileInstance.get(String.join(".", keys));
        if (configAtPath instanceof UnmodifiableConfig && ((UnmodifiableConfig) configAtPath).isEmpty()) {
            removeConfig(keys);
        }
    }

    public void removeConfig(String[] keys) {
        configFileInstance.remove(String.join(".", keys));
        Object configAtPath = configFileInstance.get(String.join(".", Arrays.copyOfRange(keys, 1, keys.length)));
        if (configAtPath instanceof UnmodifiableConfig && ((UnmodifiableConfig) configAtPath).isEmpty()) {
            removeConfig(Arrays.copyOfRange(keys, 1, keys.length));
        }
    }

    public boolean setConfig(String[] keys, Object value) {
        return setConfig(String.join(".", keys), value);
    }

    public boolean setConfig(String key, Object value) {
        if (configFileInstance.contains(key) && configFileInstance.get(key) != null) {
            stagedConfigMap.put(key, value);
            return true;
        }
        return false;
    }

    private Object tryTransform(Class<?> targetType, Object value) {
        if (!targetType.isAssignableFrom(value.getClass())) {
            try {
                if (targetType == Integer.class) {
                    value = Integer.parseInt(value.toString());
                } else if (targetType == Double.class) {
                    value = Double.parseDouble(value.toString());
                } else if (targetType == Boolean.class) {
                    value = Boolean.parseBoolean(value.toString());
                } else if (targetType == Long.class) {
                    value = Long.parseLong(value.toString());
                } else if (targetType == Float.class) {
                    value = Float.parseFloat(value.toString());
                } else if (targetType == String.class) {
                    value = value.toString();
                }
            } catch (Exception e) {
                logger.error("Failed to transform value {}!", value);
                throw new IllegalFormatConversionException((char) 0, targetType);
            }
        }
        return value;
    }

    public void saveConfigs() {
        configFileInstance.save();
    }

    public void resetConfig(String[] keys) {
        resetConfig(String.join(".", keys));
    }

    public void resetConfig(String key) {
        stagedConfigMap.put(key, null);
    }

    public String getConfig(String[] keys) {
        return getConfig(String.join(".", keys));
    }

    public String getConfig(String key) {
        return configFileInstance.get(key).toString();
    }

    public CommentedFileConfig getFileInstance() {
        return configFileInstance;
    }

    public List<String> completeConfigPath(String partialPath) {
        List<String> allPaths = getAllConfigPaths(partialPath);
        List<String> result = new ArrayList<>();

        for (String path : allPaths) {
            String remaining = path.substring(partialPath.length());
            if (remaining.isEmpty()) continue;

            int dotIndex = remaining.indexOf('.');
            String suggestion = (dotIndex == -1)
                    ? path
                    : partialPath + remaining.substring(0, dotIndex);

            if (!result.contains(suggestion)) {
                result.add(suggestion);
            }
        }
        return result;
    }

    private List<String> getAllConfigPaths(String currentPath) {
        return defaultvalueMap.keySet().stream()
                .filter(k -> k.startsWith(currentPath))
                .toList();
    }

    public @NotNull Set<Class<?>> getClasses(String pack) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDirName = pack.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    findClassesInPackageByFile(pack, filePath, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        findClassesInPackageByJar(pack, entries, packageDirName, classes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classes;
    }

    private void findClassesInPackageByFile(String packageName, String packagePath, Set<Class<?>> classes) {
        File dir = new File(packagePath);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles((file) -> file.isDirectory() || file.getName().endsWith(".class"));
        if (dirfiles != null) {
            for (File file : dirfiles) {
                if (file.isDirectory()) {
                    findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
                } else {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    try {
                        classes.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void findClassesInPackageByJar(String packageName, Enumeration<JarEntry> entries, String packageDirName, Set<Class<?>> classes) {
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                if (name.endsWith(".class") && !entry.isDirectory()) {
                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    try {
                        classes.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}