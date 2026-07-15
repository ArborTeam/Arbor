package zone.little.arbor.functions.memory;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public final class ClassHistogram {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DIAGNOSTIC_BEAN = "com.sun.management:type=DiagnosticCommand";
    private static final String OPERATION = "gcClassHistogram";

    private ClassHistogram() {
    }

    public record Entry(long instances, long bytes) {
    }

    public static Map<String, Entry> capture() {
        Map<String, Entry> result = new HashMap<>();
        String raw = invoke();
        if (raw == null) {
            return result;
        }
        for (String line : raw.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("num") || trimmed.startsWith("-") || trimmed.startsWith("Total")) {
                continue;
            }
            String[] parts = trimmed.split("\\s+");
            if (parts.length < 4) {
                continue;
            }
            try {
                long instances = Long.parseLong(parts[1]);
                long bytes = Long.parseLong(parts[2]);
                String className = parts[3];
                result.merge(className, new Entry(instances, bytes), (a, b) -> new Entry(a.instances + b.instances, a.bytes + b.bytes));
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private static String invoke() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName(DIAGNOSTIC_BEAN);
            return (String) server.invoke(name, OPERATION, new Object[]{new String[0]}, new String[]{String[].class.getName()});
        } catch (Exception e) {
            LOGGER.debug("Unable to capture class histogram", e);
            return null;
        }
    }
}
