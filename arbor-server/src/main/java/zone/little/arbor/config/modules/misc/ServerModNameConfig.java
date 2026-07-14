package zone.little.arbor.config.modules.misc;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "server_mod_name")
public class ServerModNameConfig implements IConfigModule {
    @ConfigInfo(name = "name", comments = "决定在 F3 调试界面中显示的服务器 mod 名称。")
    public static String serverModName = "Arbor";

    @ConfigInfo(name = "vanilla_spoof", comments = "忽略任何插件的修改以及本配置块中设置的服务器 mod 名称，仅强制发送 vanilla 的品牌名称")
    public static boolean fakeVanilla = false;
}