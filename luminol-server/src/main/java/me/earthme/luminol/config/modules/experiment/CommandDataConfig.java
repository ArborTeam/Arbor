package me.earthme.luminol.config.modules.experiment;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;

public class CommandDataConfig implements IConfigModule {
    @ConfigInfo(baseName = "enable")
    public static boolean enabled = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.EXPERIMENT;
    }

    @Override
    public String getBaseName() {
        return "force_the_data_command_to_be_enabled";
    }
}