package me.earthme.luminol.config.modules.function;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.DoNotLoad;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;
import me.earthme.luminol.enums.EnumStatusBarDisplay;
import me.earthme.luminol.functions.GlobalServerBarManager;
import me.earthme.luminol.functions.GlobalServerRegionBar;
import org.bukkit.Bukkit;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

@ConfigClassInfo(configAttribution = EnumConfigCategory.FUNCTION, mainName = "regionbar")
public class RegionBarConfig implements IConfigModule {
    @DoNotLoad
    private static final Logger logger = LogUtils.getLogger();
    @TransformedConfig(name = "enabled", category = {"misc", "regionbar"})
    @ConfigInfo(baseName = "enabled")
    public static boolean regionbarEnabled = false;
    @TransformedConfig(name = "format", category = {"misc", "regionbar"})
    @ConfigInfo(baseName = "format")
    public static String regionBarFormat = "<gray>Util<yellow>:</yellow> <util> Chunks<yellow>:</yellow> <green><chunks></green> Players<yellow>:</yellow> <green><players></green> Entities<yellow>:</yellow> <green><entities></green>";
    @TransformedConfig(name = "util_color_list", category = {"misc", "regionbar"})
    @ConfigInfo(baseName = "util_color_list")
    public static List<String> utilColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @TransformedConfig(name = "update_interval_ticks", category = {"misc", "regionbar"})
    @ConfigInfo(baseName = "update_interval_ticks")
    public static int updateInterval = 15;
    @TransformedConfig(name = "display", category = {"misc", "regionbar"})
    @ConfigInfo(baseName = "display")
    public static String displayString = "BOSS_BAR";

    @DoNotLoad
    public static EnumStatusBarDisplay display = EnumStatusBarDisplay.BOSS_BAR;

    @DoNotLoad
    private static boolean inited = false;

    @Override
    public void onLoaded(CommentedFileConfig configInstance) {
        if (Arrays.stream(EnumStatusBarDisplay.values()).map(Enum::name).noneMatch(s -> s.equals(displayString))) {
            logger.warn("Could not found display : {} ! Falling back to default", displayString);
            display = EnumStatusBarDisplay.BOSS_BAR;
        } else {
            display = EnumStatusBarDisplay.valueOf(displayString);
        }

        GlobalServerRegionBar regionbar = GlobalServerBarManager.get("region");
        if (regionbarEnabled) {
            regionbar.init();
        } else {
            regionbar.cancelBarUpdateTask();
        }

        if (!inited) { // command has moved to CommandRegister
            inited = true;
        }
    }

    @Override
    public void onUnloaded(CommentedFileConfig configInstance) {
        GlobalServerRegionBar regionbar = GlobalServerBarManager.get("region");
        regionbar.cancelBarUpdateTask();
        regionbar.runUnloadTask();
        Bukkit.getCommandMap().getKnownCommands().remove("luminol:regionbar");
    }
}