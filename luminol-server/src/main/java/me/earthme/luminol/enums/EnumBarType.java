package me.earthme.luminol.enums;

import com.mojang.datafixers.util.Pair;
import me.earthme.luminol.functions.bars.AbstractGlobalServerBar;
import me.earthme.luminol.functions.bars.GlobalServerMemoryBar;
import me.earthme.luminol.functions.bars.GlobalServerRegionBar;
import me.earthme.luminol.functions.bars.GlobalServerTpsBar;
import org.jetbrains.annotations.NotNull;

public enum EnumBarType {
    TPS(
            GlobalServerTpsBar.class,
            "tps",
            "function.tpsbar.enabled"
    ),
    MEMORY(
            GlobalServerMemoryBar.class,
            "memory",
            "membar",
            "function.membar.enabled"
    ),
    REGION(
            GlobalServerRegionBar.class,
            "region",
            "function.regionbar.enabled"
    );

    private final Class<? extends AbstractGlobalServerBar> clazz;
    private final String name;
    private final String commandName;
    private final String configPath;
    private final String configOrigin;

    EnumBarType(Class<? extends AbstractGlobalServerBar> clazz, String name, String configPath) {
        this(clazz, name, name + "bar", configPath);
    }

    EnumBarType(Class<? extends AbstractGlobalServerBar> clazz, String name, Pair<String, String> configPath) {
        this(clazz, name, name + "bar", configPath);
    }

    EnumBarType(Class<? extends AbstractGlobalServerBar> clazz, String name, String commandName, String configPath) {
        this(clazz, name, commandName, new Pair<>("luminol", configPath));
    }

    EnumBarType(Class<? extends AbstractGlobalServerBar> clazz, String name, String commandName, Pair<String, String> configPath) {
        this.clazz = clazz;
        this.name = name;
        this.commandName = commandName;
        this.configPath = configPath.getSecond();
        this.configOrigin = configPath.getFirst();
    }

    @NotNull
    public AbstractGlobalServerBar newInstance() {
        try {
            return this.clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public String getName() {
        return name;
    }

    public String getConfigOrigin() {
        return configOrigin;
    }

    public String getConfigPath() {
        return configPath;
    }
}
