package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;

public class GaleVariableEntityWakeupConfig implements IConfigModule {
    @ConfigInfo(baseName = "entity_wakeup_duration_ratio_standard_deviation")
    public static double entityWakeUpDurationRatioStandardDeviation = 0.2;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.OPTIMIZATIONS;
    }

    @Override
    public String getBaseName() {
        return "variable_entity_waking_up";
    }
}