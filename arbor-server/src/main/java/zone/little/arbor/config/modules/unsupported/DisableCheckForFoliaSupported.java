package zone.little.arbor.config.modules.unsupported;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.UNSUPPORTED, name = "disable_check_for_folia_supported")
public class DisableCheckForFoliaSupported implements IConfigModule {
    @ConfigInfo(name = "disable_for_paper", comments = """
            对 spigot/bukkit/paper 插件禁用 folia-supported 检查。
            注意：启用此项后将不会提供任何支持。""")
    public static boolean disableForPaper = false;

    @ConfigInfo(name = "disable_for_leaves", comments = """
            对 leaves 插件禁用 folia-supported 检查。
            注意：启用此项后将不会提供任何支持。""")
    public static boolean disableForLeaves = false;
}
