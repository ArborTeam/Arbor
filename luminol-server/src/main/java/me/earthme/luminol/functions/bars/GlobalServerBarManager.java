package me.earthme.luminol.functions.bars;

import me.earthme.luminol.enums.EnumBarType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalServerBarManager {
    private final static Map<EnumBarType, AbstractGlobalServerBar> bars = new ConcurrentHashMap<>();

    public static AbstractGlobalServerBar get(EnumBarType type) {
        return bars.computeIfAbsent(type, (unused) -> type.newInstance());
    }

    public static void cancelAll() {
        bars.values().forEach(AbstractGlobalServerBar::cancelBarUpdateTask);
    }
}
