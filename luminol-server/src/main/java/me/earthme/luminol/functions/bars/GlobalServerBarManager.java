package me.earthme.luminol.functions.bars;

import me.earthme.luminol.enums.EnumBarType;

import java.util.HashMap;
import java.util.Map;

public class GlobalServerBarManager {
    private final static Map<EnumBarType, AbstractGlobalServerBar> bars = new HashMap<>();

    public static AbstractGlobalServerBar get(EnumBarType type) {
        AbstractGlobalServerBar bar = bars.get(type);
        if (bar == null) {
            bar = type.newInstance();
            bars.put(type, bar);
        }
        return bar;
    }

    public static void cancelAll() {
        bars.values().forEach(AbstractGlobalServerBar::cancelBarUpdateTask);
    }
}
