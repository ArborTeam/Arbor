package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.CommandSuggestions;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumCollisionBehaviorMode;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "collision_behavior")
public class CollisionBehaviorConfig implements IConfigModule {
    @TransformedConfig(name = "mode", directory = {"misc", "collision_behavior"})
    @CommandSuggestions(suggest = {"VANILLA", "BLOCK_SHAPE_VANILLA", "PAPER"})
    @ConfigInfo(name = "mode", comments =
            """
                    Available Value:
                    VANILLA
                    BLOCK_SHAPE_VANILLA
                    PAPER""")
    public static EnumCollisionBehaviorMode behaviorMode = EnumCollisionBehaviorMode.BLOCK_SHAPE_VANILLA;
    @TransformedConfig(name = "vanilla_fluid_pushing", directory = {"misc", "vanilla_fluid_pushing"})
    @ConfigInfo(name = "vanilla_fluid_pushing")
    public static boolean vanillaFluidPushing = false;
}