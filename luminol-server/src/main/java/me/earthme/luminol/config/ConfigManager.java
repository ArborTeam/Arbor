package me.earthme.luminol.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.earthme.luminol.commands.CommandRegister;
import me.earthme.luminol.config.flags.TransformedConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    public static final Map<String, ConfigsInstance> configfiles = new ConcurrentHashMap<>();
    public static final Map<TransformedConfig, String[]> needTransformedConfigs = new ConcurrentHashMap<>();
    // String[]:
    // 0 -> origin key
    // 1 -> target key
    // 2 -> origin full path
    // 3 -> target full path

    public static void initConfigs() {
        configfiles.put("luminol", ConfigsInstance.of(new File("luminol_config"), "luminol", "me.earthme.luminol.config.modules"));
        preLoad();
    }

    public static void preLoad() {
        CompletableFuture<?>[] futures = configfiles.values().stream()
                .map(config -> CompletableFuture.runAsync(() -> {
                    try {
                        config.preLoadConfig();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to preload config", e);
                    }
                }))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        acceptTransformedConfigs();
    }

    public static void loadConfigFiles() {
        CompletableFuture<?>[] futures = configfiles.values().stream()
                .map(config -> CompletableFuture.runAsync(config::finalizeLoadConfig))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        CommandRegister.register(); // register command after config loaded to enable some command didn't depend on config files
    }

    public static void registerTransformedConfig(@NotNull String origin, @NotNull String target, @NotNull String originKey, @NotNull String targetKey, TransformedConfig transformedConfig) {
        needTransformedConfigs.put(transformedConfig, new String[]{origin, target, originKey, targetKey});
    }

    private static ConfigsInstance getConfigs(String name) {
        return configfiles.get(name);
    }

    private static void acceptTransformedConfigs() {
        for (Map.Entry<TransformedConfig, String[]> entry : needTransformedConfigs.entrySet()) {
            String[] config = entry.getValue();
            TransformedConfig transformedConfig = entry.getKey();
            ConfigsInstance origin = getConfigs(config[0]);
            ConfigsInstance target = getConfigs(config[1]);
            if (origin == null || target == null) continue;
            CommentedFileConfig originConfig = origin.getFileInstance();
            CommentedFileConfig targetConfig = target.getFileInstance();

            final String oldConfigKeyName = config[2];
            final String newConfigKeyName = config[3];
            Object oldValue = originConfig.get(oldConfigKeyName);
            if (oldValue != null) {
                boolean success = true;
                if (transformedConfig.transform()) {
                    try {
                        for (Class<? extends DefaultTransformLogic> logic : transformedConfig.transformLogic()) {
                            oldValue = logic.getDeclaredConstructor().newInstance().transform(oldValue);
                        }
                        oldValue = new DefaultTransformLogic().transform(oldValue);
                        targetConfig.set(newConfigKeyName, oldValue);
                        if (transformedConfig.transformComments()) {
                            targetConfig.setComment(newConfigKeyName, originConfig.getComment(oldConfigKeyName));
                        }
                    } catch (Exception e) {
                        success = false;
                        target.logger.error("Failed to transform removed config {}!", transformedConfig.name());
                    }
                }

                if (success) origin.removeConfig(oldConfigKeyName, transformedConfig.category());
            }
            needTransformedConfigs.remove(transformedConfig); // free space
            origin.saveConfigs();
            target.saveConfigs();
        }
    }
}