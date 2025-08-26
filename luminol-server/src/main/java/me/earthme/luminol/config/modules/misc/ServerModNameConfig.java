package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "server_mod_name")
public class ServerModNameConfig implements IConfigModule {
    @ConfigInfo(baseName = "name")
    public static String serverModName = "Luminol";

    @ConfigInfo(baseName = "vanilla_spoof")
    public static boolean fakeVanilla = false;
}