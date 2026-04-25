package ca.spottedleaf.concurrentutil.scheduler;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class SchedulableTickHack {
    public static boolean setState(@NonNull SchedulableTick tick, Object state) {
        return tick.setState(state);
    }

    @Contract(pure = true)
    public static <T> T getState(@NonNull SchedulableTick tick) {
        return (T) tick.state;
    }
}
