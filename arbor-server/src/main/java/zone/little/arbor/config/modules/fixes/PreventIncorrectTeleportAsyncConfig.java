package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "prevent_incorrect_teleport_async_calls_during_move_event")
public class PreventIncorrectTeleportAsyncConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
            启用后，服务器会在移动事件期间拒绝某些不正确的 teleportAsync 调用。
            这将减少由插件（如 Residence 等）引起的崩溃。
            但你应当注意，这可能会破坏与某些插件的兼容性。""")
    public static boolean enabled = false;

    @ConfigInfo(name = "throw_when_caught")
    public static boolean throwWhenCaught = true;
}
