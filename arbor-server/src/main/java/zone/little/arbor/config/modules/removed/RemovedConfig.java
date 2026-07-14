package zone.little.arbor.config.modules.removed;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.REMOVED, name = "removed_config")
public class RemovedConfig implements IConfigModule {
    @ConfigInfo(name = "removed", comments =
            """
                    RemovedConfig 已重定向至此，不具备任何功能。""")
    public static boolean enabled = true;
}