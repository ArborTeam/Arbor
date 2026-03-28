package me.earthme.luminol.config.modules.function;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "item_multitask")
public class ItemMultitaskConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
            Prevent the server from interrupting the state of items
            during block interactions or hotbar slot changes.""")
    public static boolean enabled = false;
}
