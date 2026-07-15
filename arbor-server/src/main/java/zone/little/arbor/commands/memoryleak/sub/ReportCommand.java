package zone.little.arbor.commands.memoryleak.sub;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import zone.little.arbor.commands.memoryleak.MemoryLeakSubcommand;
import zone.little.arbor.config.modules.misc.MemoryLeakConfig;
import zone.little.arbor.functions.memory.ClassGrowth;
import zone.little.arbor.functions.memory.LeakReport;
import zone.little.arbor.functions.memory.MemoryLeakMonitor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.CommandContext;

public class ReportCommand extends MemoryLeakSubcommand {
    public ReportCommand() {
        super("report");
    }

    @Override
    protected boolean execute(@NotNull CommandContext context) throws CommandSyntaxException {
        if (!MemoryLeakConfig.enabled) {
            context.getSender().sendMessage(Component.text("Memory leak detector is disabled in config.", NamedTextColor.RED));
            return true;
        }

        LeakReport report = MemoryLeakMonitor.get().captureReport();
        if (report == null) {
            context.getSender().sendMessage(Component.text("Not enough samples collected yet.", NamedTextColor.YELLOW));
            return true;
        }

        context.getSender().sendMessage(Component.text("Memory diagnostic report", NamedTextColor.GOLD));
        context.getSender().sendMessage(Component.text("  Tenured usage: " + MemoryLeakMonitor.humanBytes(report.usedBytes())
                + " / " + MemoryLeakMonitor.humanBytes(report.maxBytes()), NamedTextColor.GRAY));
        context.getSender().sendMessage(Component.text("  Growth rate: " + MemoryLeakMonitor.humanBytes((long) report.slopeBytesPerSecond()) + "/s", NamedTextColor.GRAY));
        context.getSender().sendMessage(Component.text("  Full GC count: " + report.fullGcCount(), NamedTextColor.GRAY));

        if (report.topGrowth().isEmpty()) {
            context.getSender().sendMessage(Component.text("  No class histogram diff available yet.", NamedTextColor.GRAY));
            return true;
        }

        context.getSender().sendMessage(Component.text("  Fastest growing classes:", NamedTextColor.YELLOW));
        for (ClassGrowth entry : report.topGrowth()) {
            context.getSender().sendMessage(Component.text("   " + entry.className()
                    + "  +" + MemoryLeakMonitor.humanBytes(entry.byteDelta())
                    + " (" + entry.instanceDelta() + " inst)", NamedTextColor.GRAY));
        }
        return true;
    }
}
