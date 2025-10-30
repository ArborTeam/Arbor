package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(name = "portal_search_config", category = me.earthme.luminol.enums.EnumConfigCategory.FIXES)
public class PortalSearchConfig implements IConfigModule {
    @ConfigInfo(name = "instan_poi_updating_for_nether_portal", comments = "If true, when the portal created, it would force the poi update in this place call. Would fix duplicated portals.")
    public static boolean instanPoiUpdatingForNetherPortal = false;
}
