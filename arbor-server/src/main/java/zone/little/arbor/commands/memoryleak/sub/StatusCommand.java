package zone.little.arbor.commands.memoryleak.sub;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import zone.little.arbor.commands.memoryleak.MemoryLeakSubcommand;
import zone.little.arbor.config.modules.misc.MemoryLeakConfig;
import zone.little.arbor.functions.memory.LeakReport;
import zone.little.arbor.functions.memory.MemoryLeakMonitor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.CommandContext;

import java.util.Locale;

public class StatusCommand extends MemoryLeakSubcommand {
    public StatusCommand() {
        super("status");
    }

    @Override
    protected boolean execute(@NotNull CommandContext context) throws CommandSyntaxException {
        MemoryLeakMonitor monitor = MemoryLeakMonitor.get();
        if (!MemoryLeakConfig.enabled) {
            context.getSender().sendMessage(Component.text("Memory leak detector is disabled in config.", NamedTextColor.RED));
            return true;
        }

        LeakReport report = monitor.lastReport();
        context.getSender().sendMessage(Component.text("Memory leak detector", NamedTextColor.GOLD));
        context.getSender().sendMessage(Component.text("  Running: " + monitor.isRunning(), NamedTextColor.GRAY));
        if (report == null) {
            context.getSender().sendMessage(Component.text("  Collecting samples, no data yet.", NamedTextColor.GRAY));
            return true;
        }

        context.getSender().sendMessage(Component.text("  Samples: " + report.sampleCount(), NamedTextColor.GRAY));
        context.getSender().sendMessage(Component.text("  Tenured usage: " + MemoryLeakMonitor.humanBytes(report.usedBytes())
                + " (" + String.format(Locale.ROOT, "%.1f", report.usedRatio() * 100.0D) + "%)", NamedTextColor.GRAY));
        context.getSender().sendMessage(Component.text("  Growth: " + MemoryLeakMonitor.humanBytes((long) report.slopeBytesPerSecond()) + "/s", NamedTextColor.GRAY));
        context.getSender().sendMessage(report.suspected()
                ? Component.text("  Leak suspected!", NamedTextColor.RED)
                : Component.text("  No leak detected.", NamedTextColor.GREEN));
        return true;
    }
}
