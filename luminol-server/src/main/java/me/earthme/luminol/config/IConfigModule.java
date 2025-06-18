package me.earthme.luminol.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import org.jetbrains.annotations.NotNull;

public interface IConfigModule {

    EnumConfigCategory getCategory();

    String getBaseName();

    default void onLoaded(CommentedFileConfig configInstance) {
    }

    default <T> T get(String keyName, T defaultValue, @NotNull CommentedFileConfig config) {
        if (!config.contains(keyName)) {
            config.set(keyName, defaultValue);
            return defaultValue;
        }

        return config.get(keyName);
    }
}