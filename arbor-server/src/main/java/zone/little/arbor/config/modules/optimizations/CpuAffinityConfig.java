package zone.little.arbor.config.modules.optimizations;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.DoNotLoad;
import zone.little.arbor.config.flags.HotReloadUnsupported;
import zone.little.arbor.enums.EnumConfigCategory;
import zone.little.arbor.utils.AffinityRunnableWrapper;
import net.openhft.affinity.Affinity;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "cpu_affinity")
public class CpuAffinityConfig implements IConfigModule {
    @HotReloadUnsupported
    @ConfigInfo(name = "enabled_for_tickregion", comments = "使用此选项，你可以将tick区域调度器的线程（下同）绑定到下面配置项 'tickregion_affinity' 所列出的CPU核心上，\n" +
            "这对于拥有性能核（P核）和能效核（E核）的CPU（例如12/13/14代Intel酷睿CPU等）非常有用。")
    public static boolean enabledForTickRegion = false;
    @HotReloadUnsupported
    @ConfigInfo(name = "enable_for_chunksystem_worker")
    public static boolean enabledForChunkSystemWorker = false;
    @HotReloadUnsupported
    @ConfigInfo(name = "enable_for_chunksystem_io")
    public static boolean enabledForChunkSystemIo = false;

    @HotReloadUnsupported
    @ConfigInfo(name = "tickregion_affinity", comments = "你希望tick区域线程绑定的核心编号")
    public static List<String> tickRegionAffinity = Affinity.getAffinity()
            .stream()
            .mapToObj(String::valueOf)
            .toList();
    @HotReloadUnsupported
    @ConfigInfo(name = "chunksystem_worker_affinity")
    public static List<String> chunkSystemWorkerAffinity = Affinity.getAffinity()
            .stream()
            .mapToObj(String::valueOf)
            .toList();
    @HotReloadUnsupported
    @ConfigInfo(name = "chunksystem_io_affinity")
    public static List<String> chunkSystemIoAffinity = Affinity.getAffinity()
            .stream()
            .mapToObj(String::valueOf)
            .toList();

    @DoNotLoad
    private static boolean inited = false;
    @DoNotLoad
    private static final Logger LOGGER = LogUtils.getLogger();
    @DoNotLoad
    public static AffinityRunnableWrapper tickRegionRunnableWrapper;
    @DoNotLoad
    public static AffinityRunnableWrapper chunkSystemWorkerRunnableWrapper;
    @DoNotLoad
    public static AffinityRunnableWrapper chunkSystemIoRunnableWrapper;

    public static Runnable wrapForTickRegion(Runnable in) {
        return tickRegionRunnableWrapper == null ? in : tickRegionRunnableWrapper.wrap(in);
    }

    public static Runnable wrapForChunkSystemWorker(Runnable in) {
        return chunkSystemWorkerRunnableWrapper == null ? in : chunkSystemWorkerRunnableWrapper.wrap(in);
    }

    public static Runnable wrapForChunkSystemIo(Runnable in) {
        return chunkSystemIoRunnableWrapper == null ? in : chunkSystemIoRunnableWrapper.wrap(in);
    }

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        if (enabledForTickRegion) {
            tickRegionRunnableWrapper = new AffinityRunnableWrapper("tick_region", parseAffinity(tickRegionAffinity));
            LOGGER.info("Tick region thread now bound to: {}", tickRegionRunnableWrapper.getAffinity());
        }

        if (enabledForChunkSystemIo) {
            chunkSystemIoRunnableWrapper = new AffinityRunnableWrapper("chunk_system_io", parseAffinity(chunkSystemIoAffinity));
            LOGGER.info("Chunk system I/O thread now bound to: {}", chunkSystemIoRunnableWrapper.getAffinity());
        }

        if (enabledForChunkSystemWorker) {
            chunkSystemWorkerRunnableWrapper = new AffinityRunnableWrapper("chunk_system_worker", parseAffinity(chunkSystemWorkerAffinity));
            LOGGER.info("Chunk system worker thread now bound to: {}", chunkSystemIoRunnableWrapper.getAffinity());
        }

        if (!inited) {
            inited = true;
        }
    }

    private @NonNull BitSet parseAffinity(@NonNull List<String> affinity) {
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
