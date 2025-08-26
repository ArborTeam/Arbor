package me.earthme.luminol.commands;

import me.earthme.luminol.config.modules.function.TpsBarConfig;
import me.earthme.luminol.functions.AbstractGlobalServerBar;
import me.earthme.luminol.functions.GlobalServerBarManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpsBarCommand extends Command {
    public TpsBarCommand() {
        super("tpsbar");
        this.setPermission("luminol.commands.tpsbar");
        this.setDescription("Show the tps and mspt through a bossbar");
        this.setUsage("/tpsbar");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (!TpsBarConfig.tpsbarEnabled) {
            sender.sendMessage(Component.text("Tpsbar was already disabled!").color(TextColor.color(255, 0, 0)));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only player can use this command!").color(TextColor.color(255, 0, 0)));
            return true;
        }

        AbstractGlobalServerBar tpsbar = GlobalServerBarManager.get("tps");
        if (tpsbar.isPlayerVisible(player)) {
            player.sendMessage(Component.text("Disabled tps bar").color(TextColor.color(0, 255, 0)));
            tpsbar.setVisibilityForPlayer(player, false);
            return true;
        }

        player.sendMessage(Component.text("Enabled tps bar").color(TextColor.color(0, 255, 0)));
        tpsbar.setVisibilityForPlayer(player, true);

        return true;
    }
}