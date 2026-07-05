package me.earthme.luminol.api.config;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface LuminolConfigBuilder {
    LuminolConfigsInstance of(@NotNull String name, @NotNull String pack);

    LuminolConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String pack);

    LuminolConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String file_name, @NotNull String pack);

    LuminolConfigsInstance of(@NotNull File base, @NotNull String name, @NotNull String file_name,
                                     @NotNull String command_name, @NotNull String pack);
}
