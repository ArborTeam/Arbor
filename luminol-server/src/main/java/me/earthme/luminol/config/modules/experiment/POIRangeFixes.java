package me.earthme.luminol.config.modules.experiment;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(name = "poi_fixes_exp", category = EnumConfigCategory.EXPERIMENT)
public class POIRangeFixes implements IConfigModule {
    @ConfigInfo(name = "compete_poi_unloaded", comments = """
            Compete for no matter if it is loaded
            Could have some impacts on performance
            
            Note: Still in experiment, and may broken vanilla features!
            """)
    public static boolean competePoiUnloaded = true;
}
