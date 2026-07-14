package zone.little.arbor.config.modules.experiment;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.EXPERIMENT, name = "disable_entity_exception_catchers")
public class DisableEntityCatchConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
            若启用此配置，当实体 tick 出现错误时服务器将直接崩溃，而不是移除该实体以保持服务器运行。
            它可以防止实体消失，但可能导致更多的服务器崩溃。
            除非你清楚自己在做什么，否则不要启用！！！""")
    public static boolean enabled = false;
}