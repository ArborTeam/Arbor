package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class LobotomizeVillageConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean villagerLobotomizeEnabled = false;
    @ConfigInfo(baseName = "check_interval")
    public static int villagerLobotomizeCheckInterval = 100;
    @ConfigInfo(baseName = "wait_until_trade_locked")
    public static boolean villagerLobotomizeWaitUntilTradeLocked = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.OPTIMIZATIONS;
    }

    @Override
    public String getBaseName() {
        return "lobotomize_villager";
    }
}