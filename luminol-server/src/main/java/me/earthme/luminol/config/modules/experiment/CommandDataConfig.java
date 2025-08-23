package me.earthme.luminol.config.modules.experiment;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.EXPERIMENT, mainName = "force_the_data_command_to_be_enabled")
public class CommandDataConfig implements IConfigModule {
    @ConfigInfo(baseName = "enable")
    public static boolean enabled = false;
}