package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "lobotomize_villager")
public class LobotomizeVillageConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean villagerLobotomizeEnabled = false;
    @ConfigInfo(baseName = "check_interval")
    public static int villagerLobotomizeCheckInterval = 100;
    @ConfigInfo(baseName = "wait_until_trade_locked")
    public static boolean villagerLobotomizeWaitUntilTradeLocked = false;
}