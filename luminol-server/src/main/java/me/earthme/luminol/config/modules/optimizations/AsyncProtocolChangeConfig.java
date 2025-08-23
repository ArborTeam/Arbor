package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class AsyncProtocolChangeConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.OPTIMIZATIONS;
    }

    @Override
    public String getBaseName() {
        return "use_async_protocol_switching";
    }
}
