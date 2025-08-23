package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "projectile")
public class ProjectileChunkReduceConfig implements IConfigModule {
    @ConfigInfo(baseName = "max-loads-per-tick")
    public static int maxProjectileLoadsPerTick;
    @ConfigInfo(baseName = "max-loads-per-projectile")
    public static int maxProjectileLoadsPerProjectile;
}