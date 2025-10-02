package me.earthme.luminol.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import io.papermc.paper.threadedregions.RegionizedServer;
import me.earthme.luminol.commands.config.ConfigCommand;
import me.earthme.luminol.config.flags.*;
import me.earthme.luminol.enums.EnumConfigCategory;
import me.earthme.luminol.utils.ClassLoadUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Configuration instance manager that handles loading, reloading and managing configuration modules.
 */
public class ConfigsInstance {
    public final Logger logger = LogUtils.getClassLogger();

    // Basic configuration properties
    private final File baseConfigFolder;
    private final File baseConfigFile;
    private final String name;          // Used to transform config to another config system
    private final String commandName;   // Used to register command
    private final String pack;          // Used to find all classes

    // Storage collections
    private final Set<IConfigModule> allInstanced = new HashSet<>();
    private final Map<String, Object> stagedConfigMap = new HashMap<>();
    private final Map<String, Object> defaultvalueMap = new HashMap<>();
    private final Map<String, String[]> suggestionsMap = new HashMap<>();

    // Constants and state flags
    public final String SPLIT = " # ";
    public boolean alreadyInit = false;
    private CommentedFileConfig configFileInstance;

    /**
     * Private constructor to create a configuration instance
     */
    private ConfigsInstance(@NotNull File base, @NotNull String name, @NotNull String file_name,
                            @NotNull String command_name, @NotNull String pack) {
        this.baseConfigFolder = base;
        this.name = name;
        this.pack = pack;
        this.commandName = command_name;
        this.baseConfigFile = new File(base, file_name);
    }

    // Factory methods for creating ConfigsInstance objects
    // ========================================================================

    public static ConfigsInstance of(@NotNull String name, @NotNull String pack) {
        return ConfigsInstance.of(new File(name + "_config"), name, pack);
    }

    public static ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String pack) {
        return ConfigsInstance.of(base, name, name + "_global_config.toml", pack);
    }

    public static ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String file_name, @NotNull String pack) {
        return ConfigsInstance.of(base, name, file_name, name + "config", pack);
    }

    public static ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String file_name,
                                     @NotNull String command_name, @NotNull String pack) {
        return new ConfigsInstance(base, name, file_name, command_name, pack);
    }

    // Lifecycle management methods
    // ========================================================================

    /**
     * Setup the configuration latch by registering the command
     */
    public void setupLatch() {
        ConfigCommand command = new ConfigCommand(name, commandName, this);
        command.register();
        alreadyInit = true;
    }

    /**
     * Reload configuration with default settings (keeping comments)
     */
    public void reload() {
        reload(true);
    }

    /**
     * Reload configuration with option to keep comments
     */
    public void reload(boolean keepComments) {
        RegionizedServer.ensureGlobalTickThread("Reload " + baseConfigFile.getName() + " off global region thread!");
        runUnloadTasks();
        dropAllInstanced();
        try {
            preLoadConfig(keepComments);
            finalizeLoadConfig();
        } catch (Exception e) {
            logger.error("Fail to load config file of {}.", name, e);
        }
    }

    /**
     * Reload configuration asynchronously
     */
    public @NotNull CompletableFuture<Void> reloadAsync(boolean keepComments) {
        return CompletableFuture.runAsync(() -> reload(keepComments), task -> RegionizedServer.getInstance().addTask(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("Fail to reload config of {}", name, e);
            }
        }));
    }

    /**
     * Clear all instantiated modules
     */
    public void dropAllInstanced() {
        allInstanced.clear();
    }

    /**
     * Run unload tasks for all modules
     */
    public void runUnloadTasks() {
        for (IConfigModule module : allInstanced) {
            module.onUnloaded(configFileInstance);
        }
    }

    /**
     * Finalize configuration loading by calling onLoaded for all modules
     */
    public void finalizeLoadConfig() {
        for (IConfigModule module : allInstanced) {
            module.onLoaded(configFileInstance);
        }
        setupLatch();
    }

    // Configuration loading methods
    // ========================================================================

    /**
     * Preload configuration with default settings (keeping comments)
     */
    public void preLoadConfig() throws IOException {
        preLoadConfig(true);
    }

    /**
     * Preload configuration with option to keep comments
     */
    public void preLoadConfig(boolean keepComments) throws IOException {
        baseConfigFolder.mkdirs();

        if (!baseConfigFile.exists()) {
            baseConfigFile.createNewFile();
        }

        configFileInstance = CommentedFileConfig.of(baseConfigFile);
        configFileInstance.load();

        try {
            instanceAllModule();
            loadAllModules(keepComments);
        } catch (Exception e) {
            logger.error("Failed to load config modules!", e);
            throw new RuntimeException(e);
        }

        saveConfigs();
    }

    /**
     * Load all configuration modules
     */
    private void loadAllModules(boolean keepComments) throws IllegalAccessException {
        for (IConfigModule instanced : allInstanced) {
            loadForSingle(instanced, keepComments);
        }
    }

    /**
     * Instantiate all configuration modules
     */
    private void instanceAllModule() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        for (Class<?> clazz : ClassLoadUtil.getClasses(pack)) {
            if (IConfigModule.class.isAssignableFrom(clazz)) {
                allInstanced.add((IConfigModule) clazz.getConstructor().newInstance());
            }
        }
    }

    /**
     * Load configuration for a single module
     */
    private void loadForSingle(@NotNull IConfigModule singleConfigModule, boolean keepComments) throws IllegalAccessException {
        ConfigClassInfo configClassInfo = getConfigClassInfo(singleConfigModule);
        if (configClassInfo == null) {
            return;
        }

        // Build configuration path and handle class comments
        final List<String> category = buildConfigCategoryPath(configClassInfo);
        final String fullConfigBasePath = String.join(".", category);
        handleClassLevelComments(configClassInfo, fullConfigBasePath);

        // Process each field in the module
        Field[] fields = singleConfigModule.getClass().getDeclaredFields();
        for (Field field : fields) {
            processConfigField(field, singleConfigModule, category, keepComments);
        }
    }

    /**
     * Get ConfigClassInfo annotation from a config module
     */
    private ConfigClassInfo getConfigClassInfo(IConfigModule singleConfigModule) {
        return singleConfigModule.getClass().getAnnotation(ConfigClassInfo.class);
    }

    /**
     * Build configuration category path from ConfigClassInfo annotation
     */
    private List<String> buildConfigCategoryPath(ConfigClassInfo configClassInfo) {
        final List<String> category = new ArrayList<>();
        category.add(configClassInfo.category().getBaseKeyName());
        category.addAll(List.of(configClassInfo.directory()));
        category.add(configClassInfo.name());
        return category;
    }

    /**
     * Handle class-level comments for configuration
     */
    private void handleClassLevelComments(ConfigClassInfo configClassInfo, String fullConfigBasePath) {
        final String comment = configFileInstance.getComment(fullConfigBasePath);
        if (comment == null || comment.isBlank()) {
            String comments0 = configClassInfo.comments();
            if (!comments0.isBlank()) {
                configFileInstance.setComment(fullConfigBasePath, comments0);
            }
        }
    }

    /**
     * Process a single configuration field
     */
    private void processConfigField(Field field, @NotNull IConfigModule singleConfigModule,
                                    List<String> category, boolean keepComments) throws IllegalAccessException {
        int modifiers = field.getModifiers();
        if (!(Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers))) {
            return;
        }

        // Check for special annotations
        boolean skipLoad = field.getAnnotation(DoNotLoad.class) != null;
        boolean doNotReload = alreadyInit && field.getAnnotation(HotReloadUnsupported.class) != null;
        ConfigInfo configInfo = field.getAnnotation(ConfigInfo.class);

        if (skipLoad || configInfo == null) {
            return;
        }

        // Build full configuration key
        final List<String> keys = new ArrayList<>(category);
        keys.addAll(List.of(configInfo.directory()));
        keys.add(configInfo.name());
        final String fullConfigKeyName = String.join(".", keys);

        field.setAccessible(true);
        final Object currentValue = field.get(null);
        boolean removed = getConfigClassInfo(singleConfigModule).category() == EnumConfigCategory.REMOVED;

        // Store default value if not initialized
        if (!alreadyInit && !removed) {
            defaultvalueMap.put(fullConfigKeyName, currentValue);
        }

        // Handle missing or removed configurations
        if (!configFileInstance.contains(fullConfigKeyName) || removed) {
            handleMissingOrRemovedConfig(field, fullConfigKeyName, configInfo, removed, keys);
        } else {
            // Handle existing configurations
            handleExistingConfig(field, fullConfigKeyName, configInfo, doNotReload, keepComments);
        }
    }

    /**
     * Handle missing or removed configuration entries
     */
    private void handleMissingOrRemovedConfig(Field field, String fullConfigKeyName,
                                              ConfigInfo configInfo, boolean removed,
                                              List<String> keys) throws IllegalAccessException {
        // Process transformed configurations
        processTransformedConfigs(field, fullConfigKeyName, configInfo, removed);

        // Handle removed configurations
        if (removed) {
            configFileInstance.remove("removed");
            return;
        }

        // Skip if value already exists
        if (configFileInstance.get(fullConfigKeyName) != null) {
            return;
        }

        // Validate default value
        final Object currentValue = field.get(null);
        if (currentValue == null) {
            throw new UnsupportedOperationException("Config " + configInfo.name() + "tried to add an null default value!");
        }

        // Add default value with comments
        final String comments = configInfo.comments();
        if (!comments.isBlank()) {
            configFileInstance.setComment(fullConfigKeyName, comments);
        }

        configFileInstance.add(fullConfigKeyName, currentValue);
    }

    /**
     * Process transformed configurations
     */
    private void processTransformedConfigs(Field field, String fullConfigKeyName,
                                           ConfigInfo configInfo, boolean removed) {
        for (TransformedConfig transformedConfig : field.getAnnotationsByType(TransformedConfig.class)) {
            final String oldConfigKeyName = String.join(".", transformedConfig.directory()) + "." + transformedConfig.name();

            if (!Objects.equals(transformedConfig.originInstance(), "")) {
                ConfigManager.registerTransformedConfig(transformedConfig.originInstance(), name,
                        oldConfigKeyName, fullConfigKeyName, transformedConfig);
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
                            configFileInstance.setComment(fullConfigKeyName,
                                    configFileInstance.getComment(oldConfigKeyName));
                        }
                    }

                    if (success) {
                        removeConfig(oldConfigKeyName, transformedConfig.directory());
                    }

                    final String comments = configInfo.comments();
                    if (!comments.isBlank()) {
                        configFileInstance.setComment(fullConfigKeyName, comments);
                    }

                    if (!removed && configFileInstance.get(fullConfigKeyName) != null) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Handle existing configuration entries
     */
    private void handleExistingConfig(Field field, String fullConfigKeyName,
                                      ConfigInfo configInfo, boolean doNotReload,
                                      boolean keepComments) throws IllegalAccessException {
        // Handle existing configurations
        Object actuallyValue = getActualConfigValue(fullConfigKeyName);

        // Transform value if needed
        try {
            actuallyValue = tryTransform(field.get(null).getClass(), actuallyValue);
            configFileInstance.set(fullConfigKeyName, actuallyValue);
        } catch (IllegalFormatConversionException e) {
            resetConfig(fullConfigKeyName);
            logger.error("Failed to transform config {}, reset to default!", fullConfigKeyName);
        }

        // Update field value if hot reload is supported
        if (!doNotReload) {
            field.set(null, actuallyValue);
        }

        // Handle comments
        if (!keepComments) {
            final String comments = configInfo.comments();
            configFileInstance.setComment(fullConfigKeyName, comments);
        }

        // Store suggestions for command completion
        if (!alreadyInit) {
            CommandSuggestions commandSuggestions = field.getAnnotation(CommandSuggestions.class);
            if (commandSuggestions != null) {
                suggestionsMap.put(fullConfigKeyName, commandSuggestions.suggest());
            }
        }
    }

    /**
     * Get the actual configuration value, handling staged values
     */
    private Object getActualConfigValue(String fullConfigKeyName) {
        Object actuallyValue;
        if (stagedConfigMap.containsKey(fullConfigKeyName)) {
            actuallyValue = stagedConfigMap.get(fullConfigKeyName);
            if (actuallyValue == null) {
                actuallyValue = defaultvalueMap.get(fullConfigKeyName);
            }
            if (actuallyValue instanceof String v) {
                actuallyValue = parseListFromString(v);
            }
            stagedConfigMap.remove(fullConfigKeyName);
        } else {
            actuallyValue = configFileInstance.get(fullConfigKeyName);
        }
        return actuallyValue;
    }

    // Configuration manipulation methods
    // ========================================================================

    /**
     * Remove a configuration entry
     */
    public void removeConfig(String name, String[] keys) {
        configFileInstance.remove(name);
        Object configAtPath = configFileInstance.get(String.join(".", keys));
        if (configAtPath instanceof UnmodifiableConfig && ((UnmodifiableConfig) configAtPath).isEmpty()) {
            removeConfig(keys);
        }
    }

    /**
     * Recursively remove configuration entries
     */
    public void removeConfig(String[] keys) {
        configFileInstance.remove(String.join(".", keys));
        Object configAtPath = configFileInstance.get(String.join(".", Arrays.copyOfRange(keys, 1, keys.length)));
        if (configAtPath instanceof UnmodifiableConfig && ((UnmodifiableConfig) configAtPath).isEmpty()) {
            removeConfig(Arrays.copyOfRange(keys, 1, keys.length));
        }
    }

    /**
     * Set configuration value by keys array
     */
    public boolean setConfig(String[] keys, Object value) {
        return setConfig(String.join(".", keys), value);
    }

    /**
     * Parse a list from string representation
     */
    public Object parseListFromString(String input) {
        if (input.startsWith("[") && input.endsWith("]")) {
            String content = input.substring(1, input.length() - 1).trim();

            if (content.isEmpty()) {
                return new ArrayList<>();
            }

            List<String> result = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            boolean inQuotes = false;
            boolean escapeNext = false;

            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);

                if (escapeNext) {
                    current.append(c);
                    escapeNext = false;
                } else if (c == '\\') {
                    escapeNext = true;
                } else if (c == '"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    result.add(current.toString().trim());
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }

            if (!current.isEmpty()) {
                result.add(current.toString().trim());
            }

            return result.stream().map(s -> {
                if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
                    return s.substring(1, s.length() - 1);
                }
                return s;
            }).collect(Collectors.toList());
        }
        return input;
    }

    /**
     * Convert a list to its string representation
     */
    public String parseStringFromList(List<?> list) {
        String ret;
        if (list.isEmpty()) {
            return "[]";
        }
        if (list.getFirst() instanceof String) {
            ret = list.stream()
                    .map(obj -> {
                        String str = obj.toString();
                        if (str.contains(",") || str.contains("\"") || str.contains(" ") || str.contains("[")) {
                            str = str.replace("\"", "\\\"");
                            return "\"" + str + "\"";
                        }
                        return str;
                    })
                    .collect(Collectors.joining(", ", "[", "]"));
        } else {
            ret = list.stream()
                    .map(obj -> {
                        String str;
                        try {
                            str = (String) list.getFirst().getClass().getMethod("transformInList").invoke(obj);
                        } catch (Exception e) {
                            str = null;
                        }
                        return "\"" + str + "\"";
                    })
                    .collect(Collectors.joining(", ", "[", "]"));
        }
        return ret;
    }

    /**
     * Set configuration value by key
     */
    public boolean setConfig(String key, Object value) {
        if (configFileInstance.contains(key) && configFileInstance.get(key) != null) {
            stagedConfigMap.put(key, value);
            return true;
        }
        return false;
    }

    /**
     * Attempt to transform a value to target type
     */
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

    /**
     * Save configuration to file
     */
    public void saveConfigs() {
        configFileInstance.save();
    }

    /**
     * Reset configuration by keys array
     */
    public void resetConfig(String[] keys) {
        resetConfig(String.join(".", keys));
    }

    /**
     * Reset configuration by key
     */
    public void resetConfig(String key) {
        stagedConfigMap.put(key, null);
    }

    // Configuration retrieval methods
    // ========================================================================

    /**
     * Get default configuration value as string
     */
    public String getDefaultConfig(String key) {
        return defaultvalueMap.get(key).toString();
    }

    /**
     * Get configuration value as string by keys array
     */
    public String getConfig(String[] keys) {
        return getConfig(String.join(".", keys));
    }

    /**
     * Get configuration value as string by key
     */
    public String getConfig(String key) {
        return getConfigOrigin(key).toString();
    }

    /**
     * Get original configuration value by keys array
     */
    public <T> T getConfigOrigin(String[] keys) {
        return getConfigOrigin(String.join(".", keys));
    }

    /**
     * Get original configuration value by key
     */
    public <T> T getConfigOrigin(String key) {
        return configFileInstance.get(key);
    }

    /**
     * Get configuration suggestions by keys array
     */
    public String[] getConfigSuggestions(String[] keys) {
        return getConfigSuggestions(String.join(".", keys));
    }

    /**
     * Get configuration suggestions by key
     */
    public String[] getConfigSuggestions(String key) {
        return suggestionsMap.get(key);
    }

    /**
     * Get the underlying configuration file instance
     */
    public CommentedFileConfig getFileInstance() {
        return configFileInstance;
    }

    // Configuration path completion methods
    // ========================================================================

    /**
     * Complete configuration path based on partial path
     */
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

    /**
     * Get single configuration entries under a key
     */
    public List<String> getSingleConfig(String key) {
        List<String> list = new ArrayList<>();
        if (!key.endsWith(".")) {
            key += ".";
        }
        List<String> checkList = completeConfigPath(key);
        for (String check : checkList) {
            if (completeConfigPath(check + ".").isEmpty()) {
                list.add(check);
            }
        }
        return list;
    }

    /**
     * Complete configuration path with specific dot index
     */
    public List<String> completeConfigPath(String partialPath, int dotIndex) {
        List<String> allPaths = getAllConfigPaths(partialPath);
        Set<String> resultSet = new HashSet<>();

        for (String path : allPaths) {
            String remaining = path.substring(partialPath.length());
            if (remaining.isEmpty()) continue;

            String fullPath = partialPath + remaining;
            String[] parts = fullPath.split("\\.");

            if (dotIndex == -1 || dotIndex < parts.length) {
                StringBuilder suggestionBuilder = new StringBuilder();
                for (int i = 0; i <= dotIndex; i++) {
                    if (i > 0) {
                        suggestionBuilder.append(".");
                    }
                    suggestionBuilder.append(parts[i]);
                }
                String suggestion = suggestionBuilder.toString();
                resultSet.add(suggestion);
            }
        }

        return new ArrayList<>(resultSet);
    }

    /**
     * Get all configuration paths starting with current path
     */
    public List<String> getAllConfigPaths(String currentPath) {
        return defaultvalueMap.keySet().stream()
                .filter(k -> k.startsWith(currentPath))
                .toList();
    }

    // Data retrieval methods
    // ========================================================================

    /**
     * Get all configuration data
     */
    public Map<String, Object> getAllData() {
        return getData("", false);
    }

    /**
     * Get configuration data with specified prefix
     */
    public Map<String, Object> getData(String prefix) {
        return getData(prefix, false);
    }

    /**
     * Get all configuration data with comments
     */
    public Map<String, Object> getAllDataWithComment() {
        return getData("", true);
    }

    /**
     * Get configuration data with comments and specified prefix
     */
    public Map<String, Object> getDataWithComment(String prefix) {
        return getData(prefix, true);
    }

    /**
     * Get configuration data with specified prefix
     */
    private Map<String, Object> getData(String prefix, boolean _comment) {
        Map<String, Object> result = new TreeMap<>();
        for (String key : defaultvalueMap.keySet()) {
            if (!key.startsWith(prefix)) continue;
            processData(key, result, _comment);
        }
        return result;
    }

    /**
     * Get configuration data for specified keys
     */
    public Map<String, Object> getData(List<String> list) {
        return getData(list, false);
    }

    /**
     * Get configuration data with comments for specified keys
     */
    public Map<String, Object> getDataWithComment(List<String> list) {
        return getData(list, true);
    }

    /**
     * Get configuration data for specified keys
     */
    private Map<String, Object> getData(List<String> list, boolean _comment) {
        Map<String, Object> result = new TreeMap<>();
        for (String key : list) {
            processData(key, result, _comment);
        }
        return result;
    }

    /**
     * Process configuration data for a key
     */
    private void processData(String key, Map<String, Object> result, boolean _comment) {
        String _key = key;
        Object value = configFileInstance.get(key);
        if (value instanceof List list1) {
            value = parseStringFromList(list1);
        }
        if (_comment) {
            String comment = configFileInstance.getComment(key);
            if (comment != null && !comment.isEmpty()) {
                _key += SPLIT + comment;
            }
        }
        result.put(_key, value);
    }

    /**
     * Clean up unused configuration entries
     */
    public void clean() {
        Map<String, Object> validValues = new HashMap<>();
        Map<String, String> validComments = new HashMap<>();
        for (String key : defaultvalueMap.keySet()) {
            validValues.put(key, configFileInstance.get(key));
            validComments.put(key, configFileInstance.getComment(key));
        }
        configFileInstance.clear();
        validValues.forEach(configFileInstance::set);
        validComments.forEach(configFileInstance::setComment);
        saveConfigs();
    }
}
