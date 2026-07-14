package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.CommandSuggestions;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumCollisionBehaviorMode;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "collision_behavior")
public class CollisionBehaviorConfig implements IConfigModule {
    @CommandSuggestions(suggest = {"VANILLA", "BLOCK_SHAPE_VANILLA", "PAPER"})
    @ConfigInfo(name = "mode", comments =
            """
                    决定使用哪种碰撞逻辑（Moonrise 和 Paper 为优化修改了它，但同时也会破坏一些原版行为）。
                    对于修复某些大型红石机器的异常行为会很有用
                    可选值：
                    VANILLA
                    BLOCK_SHAPE_VANILLA
                    PAPER""")
    public static EnumCollisionBehaviorMode behaviorMode = EnumCollisionBehaviorMode.BLOCK_SHAPE_VANILLA;
}