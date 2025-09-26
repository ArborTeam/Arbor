package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "end_dragon")
public class OptimizedDragonRespawnConfig implements IConfigModule {
    @ConfigInfo(baseName = "optimized_dragon_respawn")
    public static boolean optimizedRespawn = false;
}
