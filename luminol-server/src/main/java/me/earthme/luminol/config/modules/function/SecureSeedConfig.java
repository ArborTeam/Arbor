package me.earthme.luminol.config.modules.function;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.HotReloadUnsupported;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "secure_seed")
public class SecureSeedConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", directory = {"misc", "secure_seed"})
    @ConfigInfo(name = "enabled", comments = """
            Once you enable secure seed, all ores and structures are generated with 1024-bit seed
            instead of using 64-bit seed in vanilla, made seed cracker become impossible.
            ***** WARN: You need keep it enabled if your old world are also using secure seed! Or it will kill your save *****""")
    @HotReloadUnsupported
    public static boolean enabled = false;
}