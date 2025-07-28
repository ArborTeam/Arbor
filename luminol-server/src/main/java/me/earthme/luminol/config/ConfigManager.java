package me.earthme.luminol.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.earthme.luminol.config.flags.TransformedConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    public static final Map<String, ConfigsInstance> configfiles = new HashMap<>();
    public static final Map<TransformedConfig, String[]> needTransformedConfigs = new HashMap<>();
    // String[]:
    // 0 -> origin key
    // 1 -> target key
    // 2 -> origin full path
    // 3 -> target full path

    public static void initConfigs() throws IOException {
        configfiles.put("luminol", ConfigsInstance.of(new File("luminol_config"), "luminol", "me.earthme.luminol.config.modules"));
        preLoad();
    }

    public static void preLoad() throws IOException {
        for (ConfigsInstance config : configfiles.values()) {
            config.preLoadConfig();
        }
        acceptTransformedConfigs();
    }

    public static void loadConfigFiles() {
        for (ConfigsInstance config : configfiles.values()) {
            config.finalizeLoadConfig();
        }
    }

    public static void registerTransformedConfig(String origin, String originKey, String target, String targetKey, TransformedConfig transformedConfig) {
        needTransformedConfigs.put(transformedConfig, new String[]{origin, originKey, target, targetKey});
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
                        targetConfig.set(newConfigKeyName, oldValue);
                    } catch (Exception e) {
                        success = false;
                        target.logger.error("Failed to transform removed config {}!", transformedConfig.name());
                    }

                    if (transformedConfig.transformComments()) {
                        targetConfig.setComment(newConfigKeyName, originConfig.getComment(oldConfigKeyName));
                    }
                }

                if (success) origin.removeConfig(oldConfigKeyName, transformedConfig.category());
            }
            origin.saveConfigs();
            target.saveConfigs();
        }
    }
}