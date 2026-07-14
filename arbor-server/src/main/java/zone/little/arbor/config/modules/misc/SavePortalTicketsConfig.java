package zone.little.arbor.config.modules.misc;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "save_portal_tickets")
public class SavePortalTicketsConfig implements IConfigModule {
    @ConfigInfo(name = "do_save", comments = "是否在服务器停止时保存传送门的 ticket，" +
            " 这会使其行为与 1.21.5 之前的 mc 一致，" +
            " 并且在服务器再次启动时不会自动激活传送门的区块加载器。")
    public static boolean doSave = true;
}
