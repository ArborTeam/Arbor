package zone.little.arbor.commands.memoryleak.sub;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import zone.little.arbor.commands.memoryleak.MemoryLeakSubcommand;
import zone.little.arbor.functions.memory.MemoryLeakMonitor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.CommandContext;

import java.io.File;

public class DumpCommand extends MemoryLeakSubcommand {
    public DumpCommand() {
        super("dump");
    }

    @Override
    protected boolean execute(@NotNull CommandContext context) throws CommandSyntaxException {
        context.getSender().sendMessage(Component.text("Writing heap dump, this may freeze the server briefly...", NamedTextColor.YELLOW));
        try {
            File file = MemoryLeakMonitor.get().dumpHeap();
            if (file == null) {
                context.getSender().sendMessage(Component.text("Heap dump failed, see server log for details.", NamedTextColor.RED));
            } else {
                context.getSender().sendMessage(Component.text("Heap dump written to " + file.getPath(), NamedTextColor.GREEN));
            }
        } catch (Throwable throwable) {
            context.getSender().sendMessage(Component.text("Heap dump failed: " + throwable.getMessage(), NamedTextColor.RED));
        }
        return true;
    }
}
