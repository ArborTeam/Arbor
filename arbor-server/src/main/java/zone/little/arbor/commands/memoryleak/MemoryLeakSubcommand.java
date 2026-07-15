package zone.little.arbor.commands.memoryleak;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.LiteralNode;

public abstract class MemoryLeakSubcommand extends LiteralNode {
    protected MemoryLeakSubcommand(String name) {
        super(name);
    }

    @Override
    public boolean requires(@NotNull CommandSourceStack source) {
        return hasPermission(source.getSender());
    }

    protected boolean hasPermission(CommandSender sender) {
        return MemoryLeakCommand.hasPermission(sender, this.name);
    }
}
