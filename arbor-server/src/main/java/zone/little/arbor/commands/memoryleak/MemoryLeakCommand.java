package zone.little.arbor.commands.memoryleak;

import zone.little.arbor.commands.memoryleak.sub.DumpCommand;
import zone.little.arbor.commands.memoryleak.sub.ReportCommand;
import zone.little.arbor.commands.memoryleak.sub.StatusCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.RootNode;

public class MemoryLeakCommand extends RootNode {
    private static final String PERM_BASE = "arbor.commands.memoryleak";

    public MemoryLeakCommand() {
        super("memoryleak", PERM_BASE);
        children(
                new StatusCommand(),
                new ReportCommand(),
                new DumpCommand()
        );
    }

    public static boolean hasPermission(@NotNull CommandSender sender, String... subcommand) {
        return hasPermission(PERM_BASE, sender, subcommand);
    }
}
