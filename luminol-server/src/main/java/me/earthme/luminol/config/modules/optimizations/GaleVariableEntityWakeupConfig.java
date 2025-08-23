package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "variable_entity_waking_up")
public class GaleVariableEntityWakeupConfig implements IConfigModule {
    @ConfigInfo(baseName = "entity_wakeup_duration_ratio_standard_deviation")
    public static double entityWakeUpDurationRatioStandardDeviation = 0.2;
}