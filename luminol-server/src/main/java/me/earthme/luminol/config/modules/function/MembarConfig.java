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
import me.earthme.luminol.functions.GlobalServerMemoryBar;
import org.bukkit.Bukkit;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

@ConfigClassInfo(configAttribution = EnumConfigCategory.FUNCTION, mainName = "membar")
public class MembarConfig implements IConfigModule {
    @DoNotLoad
    private static final Logger logger = LogUtils.getLogger();
    @TransformedConfig(name = "enabled", category = {"misc", "membar"})
    @ConfigInfo(baseName = "enabled")
    public static boolean memoryBarEnabled = false;
    @TransformedConfig(name = "format", category = {"misc", "membar"})
    @ConfigInfo(baseName = "format")
    public static String memBarFormat = "<gray>Memory usage <yellow>:</yellow> <used>MB<yellow>/</yellow><available>MB";
    @TransformedConfig(name = "memory_color_list", category = {"misc", "membar"})
    @ConfigInfo(baseName = "memory_color_list")
    public static List<String> memColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @TransformedConfig(name = "update_interval_ticks", category = {"misc", "membar"})
    @ConfigInfo(baseName = "update_interval_ticks")
    public static int updateInterval = 15;
    @TransformedConfig(name = "display", category = {"misc", "membar"})
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

        GlobalServerMemoryBar membar = GlobalServerBarManager.get("memory");
        if (memoryBarEnabled) {
            membar.init();
        } else {
            membar.cancelBarUpdateTask();
        }

        if (!inited) { // command has moved to CommandRegister
            inited = true;
        }
    }

    @Override
    public void onUnloaded(CommentedFileConfig configInstance) {
        GlobalServerMemoryBar membar = GlobalServerBarManager.get("memory");
        membar.cancelBarUpdateTask();
        membar.runUnloadTask();
        Bukkit.getCommandMap().getKnownCommands().remove("luminol:membar");
    }
}