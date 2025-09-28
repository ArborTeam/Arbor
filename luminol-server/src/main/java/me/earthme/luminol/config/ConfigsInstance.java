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

public class ConfigsInstance {
    public final Logger logger = LogUtils.getClassLogger();
    private final File baseConfigFolder;
    private final File baseConfigFile;
    private final String name; // used to transform config to another config system
    private final String commandName; // used to register command
    private final String pack; // used to find all classes
    private final Set<IConfigModule> allInstanced = new HashSet<>();
    private final Map<String, Object> stagedConfigMap = new HashMap<>();
    private final Map<String, Object> defaultvalueMap = new HashMap<>();
    private final Map<String, String[]> suggestionsMap = new HashMap<>();
    public final String SPLIT = " # ";
    public boolean alreadyInit = false;
    private CommentedFileConfig configFileInstance;

    private ConfigsInstance(@NotNull File base, @NotNull String name, @NotNull String file_name, @NotNull String command_name, @NotNull String pack) {
        this.baseConfigFolder = base;
        this.name = name;
        this.pack = pack;
        this.commandName = command_name;
        this.baseConfigFile = new File(base, file_name);
    }

    public static ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String pack) {
        return ConfigsInstance.of(base, name, name + "_global_config.toml", pack);
    }

    public static ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String file_name, @NotNull String pack) {
        return ConfigsInstance.of(base, name, file_name, name + "config", pack);
    }

    public static ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String file_name, @NotNull String command_name, @NotNull String pack) {
        return new ConfigsInstance(base, name, file_name, command_name, pack);
    }

    public void setupLatch() {
        ConfigCommand command = new ConfigCommand(name, commandName, this);
        command.register();
        alreadyInit = true;
    }

    public void reload() {
        reload(true);
    }

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

    public @NotNull CompletableFuture<Void> reloadAsync(boolean keepComments) {
        return CompletableFuture.runAsync(() -> reload(keepComments), task -> RegionizedServer.getInstance().addTask(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("Fail to reload config of {}", name, e);
            }
        }));
    }

    public void dropAllInstanced() {
        allInstanced.clear();
    }

    public void runUnloadTasks() {
        for (IConfigModule module : allInstanced) {
            module.onUnloaded(configFileInstance);
        }
    }

    public void finalizeLoadConfig() {
        for (IConfigModule module : allInstanced) {
            module.onLoaded(configFileInstance);
        }
        setupLatch();
    }

    public void preLoadConfig() throws IOException {
        preLoadConfig(true);
    }

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

    private void loadAllModules(boolean keepComments) throws IllegalAccessException {
        for (IConfigModule instanced : allInstanced) {
            loadForSingle(instanced, keepComments);
        }
    }

    private void instanceAllModule() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Class<?> clazz : ClassLoadUtil.getClasses(pack)) {
            if (IConfigModule.class.isAssignableFrom(clazz)) {
                allInstanced.add((IConfigModule) clazz.getConstructor().newInstance());
            }
        }
    }

    private void loadForSingle(@NotNull IConfigModule singleConfigModule, boolean keepComments) throws IllegalAccessException {
        ConfigClassInfo configClassInfo = singleConfigModule.getClass().getAnnotation(ConfigClassInfo.class);
        if (configClassInfo == null) {
            return;
        }
        final List<String> category = new ArrayList<>();
        category.add(configClassInfo.category().getBaseKeyName());
        category.addAll(List.of(configClassInfo.directory()));
        category.add(configClassInfo.name());

        final String fullConfigBasePath = String.join(".", category);

        final String comment = configFileInstance.getComment(fullConfigBasePath);
        if (comment == null || comment.isBlank()) {
            String comments0 = configClassInfo.comments();
            if (!comments0.isBlank()) {
                configFileInstance.setComment(fullConfigBasePath, comments0);
            }
        }

        Field[] fields = singleConfigModule.getClass().getDeclaredFields();

        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                boolean skipLoad = field.getAnnotation(DoNotLoad.class) != null;
                boolean doNotReload = alreadyInit && field.getAnnotation(HotReloadUnsupported.class) != null;
                ConfigInfo configInfo = field.getAnnotation(ConfigInfo.class);

                if (skipLoad || configInfo == null) {
                    continue;
                }

                final List<String> keys = new ArrayList<>(List.of(configInfo.directory()));
                keys.add(configInfo.name());

                final String fullConfigKeyName = fullConfigBasePath + "." + String.join(".", keys);

                field.setAccessible(true);
                final Object currentValue = field.get(null);
                boolean removed = configClassInfo.category() == EnumConfigCategory.REMOVED;
                if (!alreadyInit && !removed) defaultvalueMap.put(fullConfigKeyName, currentValue);

                if (!configFileInstance.contains(fullConfigKeyName) || removed) {
                    for (TransformedConfig transformedConfig : field.getAnnotationsByType(TransformedConfig.class)) {
                        final String oldConfigKeyName = String.join(".", transformedConfig.directory()) + "." + transformedConfig.name();
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

                                if (success) removeConfig(oldConfigKeyName, transformedConfig.directory());
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
                        throw new UnsupportedOperationException("Config " + configInfo.name() + "tried to add an null default value!");
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
                    if (actuallyValue instanceof String v) {
                        actuallyValue = parseListFromString(v);
                    }
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
                if (!doNotReload) {
                    field.set(null, actuallyValue);
                }

                if (!keepComments) {
                    final String comments = configInfo.comments();
                    configFileInstance.setComment(fullConfigKeyName, comments);
                }

                if (!alreadyInit) {
                    CommandSuggestions commandSuggestions = field.getAnnotation(CommandSuggestions.class);
                    if (commandSuggestions != null) {
                        suggestionsMap.put(fullConfigKeyName, commandSuggestions.suggest());
                    }
                }
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

    public String getDefaultConfig(String key) {
        return defaultvalueMap.get(key).toString();
    }

    public String getConfig(String[] keys) {
        return getConfig(String.join(".", keys));
    }

    public String getConfig(String key) {
        return getConfigOrigin(key).toString();
    }

    public <T> T getConfigOrigin(String[] keys) {
        return getConfigOrigin(String.join(".", keys));
    }

    public <T> T getConfigOrigin(String key) {
        return configFileInstance.get(key);
    }

    public String[] getConfigSuggestions(String[] keys) {
        return getConfigSuggestions(String.join(".", keys));
    }

    public String[] getConfigSuggestions(String key) {
        return suggestionsMap.get(key);
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

    public List<String> getAllConfigPaths(String currentPath) {
        return defaultvalueMap.keySet().stream()
                .filter(k -> k.startsWith(currentPath))
                .toList();
    }

    public Map<String, Object> getAllData() {
        return getData("", false);
    }

    public Map<String, Object> getData(String prefix) {
        return getData(prefix, false);
    }

    public Map<String, Object> getAllDataWithComment() {
        return getData("", true);
    }

    public Map<String, Object> getDataWithComment(String prefix) {
        return getData(prefix, true);
    }

    private Map<String, Object> getData(String prefix, boolean _comment) {
        Map<String, Object> result = new TreeMap<>();
        for (String key : defaultvalueMap.keySet()) {
            if (!key.startsWith(prefix)) continue;
            processData(key, result, _comment);
        }
        return result;
    }

    public Map<String, Object> getData(List<String> list) {
        return getData(list, false);
    }

    public Map<String, Object> getDataWithComment(List<String> list) {
        return getData(list, true);
    }

    private Map<String, Object> getData(List<String> list, boolean _comment) {
        Map<String, Object> result = new TreeMap<>();
        for (String key : list) {
            processData(key, result, _comment);
        }
        return result;
    }

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