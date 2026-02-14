package me.earthme.luminol.config.modules.optimizations;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.*;
import me.earthme.luminol.enums.EnumConfigCategory;
import net.openhft.affinity.Affinity;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "cpu_affinity")
public class CpuAffinityConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", directory = {"misc", "cpu_affinity"})
    @HotReloadUnsupported
    @ConfigInfo(name = "enabled", comments = "Using this you could pin the threads of tick region scheduler to cpu cores listed in the config 'tickregion_affinity' following, \n" +
            "which is useful for those CPU with P and E cores (such as 12/13/14 gen Intel Core CPUs and so on.)")
    public static boolean cpuAffinityEnabled = false;
    @HotReloadUnsupported
    @TransformedConfig(name = "enabled", directory = {"misc", "tickregion_affinity"})
    @ConfigInfo(name = "tickregion_affinity", comments = "The core number you want the tick region threads to bind on")
    public static List<String> tickRegionAffinity = Affinity.getAffinity()
            .stream()
            .mapToObj(String::valueOf)
            .toList();

    @DoNotLoad
    private static boolean inited = false;
    @DoNotLoad
    private static final Logger LOGGER = LogUtils.getLogger();
    @DoNotLoad
    public static BitSet tickRegionAffinityBitSet;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        if (!cpuAffinityEnabled) return;

        tickRegionAffinityBitSet = parseAffinity(tickRegionAffinity);
        LOGGER.info("Tick region thread now bound to: {}", tickRegionAffinityBitSet);

        if (!inited) {
            inited = true;
        }
    }

    private BitSet parseAffinity(List<String> affinity) {
        int maxAvailable = Runtime.getRuntime().availableProcessors();
        BitSet affinitySet = new BitSet(affinity.size());
        affinity.stream()
                .mapToInt(str -> {
                    try {
                        return Integer.parseInt(str);
                    } catch (NumberFormatException ignored) {
                        LOGGER.warn("Unable to parse cpu id {} to a valid number, falling back to 0.", str);
                        return 0;
                    }
                })
                .distinct()
                .filter(cpuId -> {
                    if (cpuId >= 0 && cpuId < maxAvailable) {
                        return true;
                    } else {
                        LOGGER.warn("Invalid cpu id {}, ignoring.", cpuId);
                        return false;
                    }
                })
                .forEach(affinitySet::set);
        return affinitySet;
    }
}
