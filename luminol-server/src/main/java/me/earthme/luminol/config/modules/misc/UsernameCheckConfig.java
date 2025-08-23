package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class UsernameCheckConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = true;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "username_checks";
    }
}