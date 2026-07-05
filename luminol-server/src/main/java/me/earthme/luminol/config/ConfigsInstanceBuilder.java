package me.earthme.luminol.config;

import me.earthme.luminol.api.config.LuminolConfigBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ConfigsInstanceBuilder implements LuminolConfigBuilder {
    // Factory methods for creating ConfigsInstance objects
    // ========================================================================

    public ConfigsInstance of(@NotNull String name, @NotNull String pack) {
        return this.of(new File(name + "_config"), name, pack);
    }

    public ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String pack) {
        return this.of(base, name, name + "_global_config.toml", pack);
    }

    public ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String file_name, @NotNull String pack) {
        return this.of(base, name, file_name, name + "config", pack);
    }

    public ConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String file_name,
                                     @NotNull String command_name, @NotNull String pack) {
        return new ConfigsInstance(base, name, file_name, command_name, pack);
    }
}
