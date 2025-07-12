package me.earthme.luminol.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    public static final Map<String, ConfigsInstance> configfiles = new HashMap<>();

    public static void initConfigs() throws IOException {
        configfiles.put("luminol", new ConfigsInstance(new File("luminol_config"), "luminol", "me.earthme.luminol.config.modules"));
        preLoad();
    }

    public static void preLoad() throws IOException {
        for (ConfigsInstance config : configfiles.values()) {
            config.preLoadConfig();
        }
    }

    public static void loadConfigFiles() {
        for (ConfigsInstance config : configfiles.values()) {
            config.finalizeLoadConfig();
        }
    }
}