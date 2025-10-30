package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "portal_search_config")
public class PortalSearchConfig implements IConfigModule {
    @TransformedConfig(name = "instan_poi_updating_for_nether_portal", directory = {"fixes", "portal_search_config"})
    @ConfigInfo(name = "instant_poi_updating_for_nether_portal", comments = "If it enabled, it would force the poi update in this place call when the portal created. Would fix duplicated portals.")
    public static boolean instantPoiUpdatingForNetherPortal = false;
}
