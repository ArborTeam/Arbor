package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class PetalReduceSensorWorkConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = true;
    @ConfigInfo(baseName = "delay_ticks")
    public static int delayTicks = 10;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.OPTIMIZATIONS;
    }

    @Override
    public String getBaseName() {
        return "reduce_sensor_work";
    }
}