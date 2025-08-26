package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "reduce_sensor_work")
public class PetalReduceSensorWorkConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = true;
    @ConfigInfo(baseName = "delay_ticks")
    public static int delayTicks = 10;
}