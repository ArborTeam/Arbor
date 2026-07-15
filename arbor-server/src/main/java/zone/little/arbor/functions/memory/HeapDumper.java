package zone.little.arbor.functions.memory;

import com.mojang.logging.LogUtils;
import com.sun.management.HotSpotDiagnosticMXBean;
import org.slf4j.Logger;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class HeapDumper {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private HeapDumper() {
    }

    public static File dump(File directory, boolean liveOnly) {
        try {
            if (!directory.exists() && !directory.mkdirs()) {
                LOGGER.warn("Unable to create heap dump directory {}", directory.getAbsolutePath());
                return null;
            }
            File target = new File(directory, "arbor-heap-" + LocalDateTime.now().format(STAMP) + ".hprof");
            HotSpotDiagnosticMXBean bean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
            bean.dumpHeap(target.getAbsolutePath(), liveOnly);
            LOGGER.info("Wrote heap dump to {}", target.getAbsolutePath());
            return target;
        } catch (Exception e) {
            LOGGER.warn("Failed to write heap dump", e);
            return null;
        }
    }
}
