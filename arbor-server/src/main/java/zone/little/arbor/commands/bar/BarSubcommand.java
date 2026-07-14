package zone.little.arbor.commands.bar;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import zone.little.arbor.commands.bar.sub.ConfigEditCommand;
import zone.little.arbor.commands.bar.sub.ToggleCommand;
import zone.little.arbor.enums.EnumBarType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.CommandNode;
import org.leavesmc.leaves.command.LiteralNode;

import java.util.List;

public class BarSubcommand extends LiteralNode {
    public BarSubcommand(EnumBarType barType) {
        super(barType.getCommandName());
        children(
                new ToggleCommand(barType),
                new ConfigEditCommand(barType)
        );
    }

    @Override
    public boolean requires(@NotNull CommandSourceStack source) {
        return hasPermission(source.getSender());
    }

    protected boolean hasPermission(CommandSender sender) {
        return BarCommand.hasPermission(sender, this.name);
    }

    public List<CommandNode> getChildren() {
        return children;
    }
}
