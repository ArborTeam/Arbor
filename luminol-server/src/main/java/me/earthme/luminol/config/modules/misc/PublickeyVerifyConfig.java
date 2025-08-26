package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "verify_publickey_only_in_online_mode")
public class PublickeyVerifyConfig implements IConfigModule {

    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = false;
}