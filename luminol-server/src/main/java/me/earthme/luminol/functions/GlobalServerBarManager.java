package me.earthme.luminol.functions;

import java.util.HashMap;
import java.util.Map;

public class GlobalServerBarManager {
    private final static Map<String, AbstractGlobalServerBar> bars = new HashMap<>();

    static {
        bars.put("tps", new GlobalServerTpsBar());
        bars.put("memory", new GlobalServerMemoryBar());
        bars.put("region", new GlobalServerRegionBar());
    } // initial data

    @SuppressWarnings("unchecked")
    public static <T extends AbstractGlobalServerBar> T get(String key) {
        AbstractGlobalServerBar bar = bars.get(key);
        if (bar == null) throw new IllegalArgumentException("No bar found for " + key);
        return (T) bar;
    }

    public static void cancelAll() {
        bars.values().forEach(AbstractGlobalServerBar::cancelBarUpdateTask);
    }
}
