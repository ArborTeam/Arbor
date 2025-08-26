package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "mojang_out_of_order_chat_check")
public class InorderChatConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = true;
}