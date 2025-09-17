package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "use_async_protocol_switching")
public class AsyncProtocolChangeConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled", comments = """
            Uses async protocol preparation for mc.
            Warn: Due to the packet sequence was changed by this optimization, it might be\s
             uncompatible with some plugins""")
    public static boolean enabled = false;
}
