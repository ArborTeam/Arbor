package me.earthme.luminol.config.modules.experiment;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.EXPERIMENT, name = "command")
public class CommandConfig implements IConfigModule {
    @TransformedConfig(name = "enable", directory = {"experiment", "force_the_data_command_to_be_enabled"})
    @ConfigInfo(name = "enable_data_command")
    public static boolean data = false;
    @TransformedConfig(name = "enabled", directory = {"experiment", "force_enable_command_block_command_execution"})
    @ConfigInfo(name = "enable_command_block")
    public static boolean commandBlock = false;
}
