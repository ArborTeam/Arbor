package zone.little.arbor.commands.bar;

import zone.little.arbor.commands.bar.sub.ToggleCommand;
import zone.little.arbor.enums.EnumBarType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.RootNode;

public class BarCommand extends RootNode {
    private static final String PERM_BASE = "arbor.commands.bar";

    public BarCommand() {
        super("bar", PERM_BASE);
        children(
                new BarSubcommand(EnumBarType.TPS),
                new BarSubcommand(EnumBarType.MEMORY),
                new BarSubcommand(EnumBarType.REGION)
        );
    }

    public static boolean hasPermission(@NotNull CommandSender sender, String... subcommand) {
        return hasPermission(PERM_BASE, sender, subcommand);
    }

    @Override
    public void register() {
        super.register();
        children.forEach(child -> {
            if (child instanceof BarSubcommand barSubcommand) {
                barSubcommand.getChildren().forEach(subChild -> {
                    if (subChild instanceof ToggleCommand toggleCommand) {
                        toggleCommand.register();
                    }
                });
            }
        });
    }

    @Override
    public void unregister() {
        super.unregister();
        children.forEach(child -> {
            if (child instanceof BarSubcommand barSubcommand) {
                barSubcommand.getChildren().forEach(subChild -> {
                    if (subChild instanceof ToggleCommand toggleCommand) {
                        toggleCommand.unregister();
                    }
                });
            }
        });
    }
}
