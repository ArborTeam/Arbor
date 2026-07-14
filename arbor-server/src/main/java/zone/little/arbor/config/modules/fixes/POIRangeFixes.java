package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(name = "poi_range_fixes", category = EnumConfigCategory.FIXES)
public class POIRangeFixes implements IConfigModule {
    @ConfigInfo(name = "do_not_compete_poi_if_unloaded", comments = """
            当 POI 已卸载时，不去竞争该 POI
            与 https://github.com/PaperMC/Folia/issues/292 相关
            """)
    public static boolean doNotCompetePOIIfUnloaded = false;
}
