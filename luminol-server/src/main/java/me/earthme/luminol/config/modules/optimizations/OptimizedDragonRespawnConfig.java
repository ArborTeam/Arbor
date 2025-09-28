package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "end_dragon")
public class OptimizedDragonRespawnConfig implements IConfigModule {
    @ConfigInfo(name = "optimized_dragon_respawn")
    public static boolean optimizedRespawn = false;
}
