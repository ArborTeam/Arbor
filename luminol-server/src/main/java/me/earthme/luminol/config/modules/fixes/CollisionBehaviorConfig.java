package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.FIXES, mainName = "collision_behavior")
public class CollisionBehaviorConfig implements IConfigModule {
    @TransformedConfig(name = "mode", category = {"misc", "collision_behavior"})
    @ConfigInfo(baseName = "mode", comments =
            """
                    Available Value:
                    VANILLA
                    BLOCK_SHAPE_VANILLA
                    PAPER""")
    public static String behaviorMode = "BLOCK_SHAPE_VANILLA";
    @TransformedConfig(name = "vanilla_fluid_pushing", category = {"misc", "vanilla_fluid_pushing"})
    @ConfigInfo(baseName = "vanilla_fluid_pushing")
    public static boolean vanillaFluidPushing = false;
}