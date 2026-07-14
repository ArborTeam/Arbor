package zone.little.arbor.config.modules.misc;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "mojang_out_of_order_chat_check")
public class InorderChatConfig implements IConfigModule {
    @ConfigInfo(name = "enabled")
    public static boolean enabled = true;
}