package zone.little.arbor.functions.memory;

import com.mojang.logging.LogUtils;
import zone.little.arbor.config.modules.misc.MemoryLeakConfig;
import org.slf4j.Logger;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MemoryLeakMonitor {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MemoryLeakMonitor INSTANCE = new MemoryLeakMonitor();
    private static final File DUMP_DIR = new File("arbor-heap-dumps");

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final LinkedList<long[]> samples = new LinkedList<>();

    private Worker worker;
    private volatile boolean running;
    private volatile LeakReport lastReport;
    private Map<String, ClassHistogram.Entry> baseline;
    private int suspiciousStreak;

    public static MemoryLeakMonitor get() {
        return INSTANCE;
    }

    public void reload() {
        this.stop();

        if (MemoryLeakConfig.enabled) {
            this.start();
        }
    }

    public void start() {
        if (this.running) {
            return;
        }

        this.running = true;
        this.worker = new Worker();
        this.worker.start();

        LOGGER.info("Memory leak monitor started, sampling every {}s", MemoryLeakConfig.sampleIntervalSeconds);
    }

    public void stop() {
        this.running = false;

        if (this.worker != null) {
            this.worker.interrupt();
            this.worker = null;
        }

        synchronized (this.samples) {
            this.samples.clear();
        }

        this.baseline = null;
        this.suspiciousStreak = 0;
    }

    public boolean isRunning() {
        return this.running;
    }

    public LeakReport lastReport() {
        return this.lastReport;
    }

    public LeakReport captureReport() {
        final LeakReport report = this.buildReport(this.tenured());
        this.lastReport = report;
        return report;
    }

    public File dumpHeap() {
        return HeapDumper.dump(DUMP_DIR, true);
    }

    private void tick() {
        final long now = System.currentTimeMillis();
        final MemoryUsage tenured = this.tenured();
        final long window = MemoryLeakConfig.trendWindowSeconds * 1000L;

        synchronized (this.samples) {
            this.samples.addLast(new long[]{now, tenured.getUsed()});

            while (this.samples.size() > 2 && now - this.samples.getFirst()[0] > window) {
                this.samples.removeFirst();
            }
        }

        final LeakReport report = this.buildReport(tenured);
        this.lastReport = report;

        if (!report.suspected()) {
            this.suspiciousStreak = 0;
            return;
        }

        this.suspiciousStreak++;
        if (this.suspiciousStreak < MemoryLeakConfig.trendsBeforeWarn) {
            return;
        }

        this.printWarning(report);
        if (MemoryLeakConfig.autoHeapDump) {
            HeapDumper.dump(DUMP_DIR, true);
        }

        this.suspiciousStreak = 0;
    }

    private LeakReport buildReport(MemoryUsage tenured) {
        final long used = tenured.getUsed();
        final long max = tenured.getMax();
        final double ratio = max > 0L ? (double) used / max : 0.0D;
        final double slope = this.growthPerSecond();

        final int count;
        synchronized (this.samples) {
            count = this.samples.size();
        }

        final boolean suspected = count >= MemoryLeakConfig.minSamplesForTrend
                && ratio * 100.0D >= MemoryLeakConfig.minHeapUsageRatioPercent
                && slope >= MemoryLeakConfig.growthThresholdBytesPerSecond;

        final List<ClassGrowth> growth = suspected ? this.diffHistogram() : List.of();
        return new LeakReport(suspected, slope, used, max, ratio, this.fullGcCount(), count, growth);
    }

    private double growthPerSecond() {
        synchronized (this.samples) {
            final int n = this.samples.size();
            if (n < 2) {
                return 0.0D;
            }

            final long base = this.samples.getFirst()[0];
            double sumX = 0.0D;
            double sumY = 0.0D;
            double sumXY = 0.0D;
            double sumXX = 0.0D;

            for (long[] point : this.samples) {
                final double x = (point[0] - base) / 1000.0D;
                final double y = point[1];
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumXX += x * x;
            }

            final double denom = n * sumXX - sumX * sumX;
            if (denom == 0.0D) {
                return 0.0D;
            }

            return (n * sumXY - sumX * sumY) / denom;
        }
    }

    private List<ClassGrowth> diffHistogram() {
        final Map<String, ClassHistogram.Entry> current = ClassHistogram.capture();
        if (current.isEmpty()) {
            return List.of();
        }

        if (this.baseline == null) {
            this.baseline = current;
            return List.of();
        }

        final List<ClassGrowth> result = new ArrayList<>();
        for (Map.Entry<String, ClassHistogram.Entry> entry : current.entrySet()) {
            final ClassHistogram.Entry old = this.baseline.get(entry.getKey());
            final long instances = entry.getValue().instances() - (old == null ? 0L : old.instances());
            final long bytes = entry.getValue().bytes() - (old == null ? 0L : old.bytes());

            if (bytes > 0L) {
                result.add(new ClassGrowth(entry.getKey(), instances, bytes, entry.getValue().instances(), entry.getValue().bytes()));
            }
        }

        result.sort((a, b) -> Long.compare(b.byteDelta(), a.byteDelta()));

        final int limit = Math.min(MemoryLeakConfig.topClassCount, result.size());
        return new ArrayList<>(result.subList(0, limit));
    }

    private void printWarning(LeakReport report) {
        LOGGER.warn("Suspected memory leak: tenured heap growing at {}/s, usage {} ({}), full GC count {}",
                humanBytes((long) report.slopeBytesPerSecond()), humanBytes(report.usedBytes()),
                percent(report.usedRatio()), report.fullGcCount());

        for (ClassGrowth growth : report.topGrowth()) {
            LOGGER.warn("  {} +{} ({} instances), now {}",
                    growth.className(), humanBytes(growth.byteDelta()), growth.instanceDelta(), humanBytes(growth.currentBytes()));
        }
    }

    private MemoryUsage tenured() {
        long used = 0L;
        long committed = 0L;
        long max = 0L;
        boolean found = false;

        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() != MemoryType.HEAP || !pool.isValid()) {
                continue;
            }

            final String name = pool.getName().toLowerCase(Locale.ROOT);
            if (!name.contains("old") && !name.contains("tenured")) {
                continue;
            }

            final MemoryUsage usage = pool.getUsage();
            if (usage == null) {
                continue;
            }

            used += usage.getUsed();
            committed += usage.getCommitted();
            if (usage.getMax() > 0L) {
                max += usage.getMax();
            }
            found = true;
        }

        if (!found) {
            return this.memoryBean.getHeapMemoryUsage();
        }

        return new MemoryUsage(-1L, used, committed, max > 0L ? max : -1L);
    }

    private int fullGcCount() {
        int count = 0;

        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            final String name = gc.getName().toLowerCase(Locale.ROOT);
            if (name.contains("old") || name.contains("marksweep") || name.contains("g1 old") || name.contains("global")) {
                final long value = gc.getCollectionCount();
                if (value > 0L) {
                    count += (int) value;
                }
            }
        }

        return count;
    }

    private static String percent(double ratio) {
        return String.format(Locale.ROOT, "%.1f%%", ratio * 100.0D);
    }

    public static String humanBytes(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        }

        final String[] units = {"KB", "MB", "GB", "TB"};
        double value = bytes;
        int index = -1;

        do {
            value /= 1024.0D;
            index++;
        } while (value >= 1024.0D && index < units.length - 1);

        return String.format(Locale.ROOT, "%.2f %s", value, units[index]);
    }

    private final class Worker extends Thread {
        private Worker() {
            super("Arbor Memory Leak Monitor");
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (MemoryLeakMonitor.this.running) {
                try {
                    Thread.sleep(MemoryLeakConfig.sampleIntervalSeconds * 1000L);
                } catch (InterruptedException e) {
                    return;
                }

                if (!MemoryLeakMonitor.this.running) {
                    return;
                }

                try {
                    MemoryLeakMonitor.this.tick();
                } catch (Exception e) {
                    LOGGER.warn("Memory leak monitor sampling failed", e);
                }
            }
        }
    }
}
