package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;

public class CollisionBehaviorConfig implements IConfigModule {
    @ConfigInfo(baseName = "mode", comments =
            """
                    Available Value:
                    VANILLA
                    BLOCK_SHAPE_VANILLA
                    PAPER""")
    public static String behaviorMode = "BLOCK_SHAPE_VANILLA";

    @ConfigInfo(baseName = "vanilla_fluid_pushing")
    public static boolean vanillaFluidPushing = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "collision_behavior";
    }
}