package me.earthme.luminol.commands;

import me.earthme.luminol.config.modules.function.RegionBarConfig;
import me.earthme.luminol.functions.GlobalServerBarManager;
import me.earthme.luminol.functions.GlobalServerRegionBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegionBarCommand extends Command {
    public RegionBarCommand() {
        super("regionbar");
        this.setPermission("luminol.commands.regionbar");
        this.setDescription("Show info about your current region through a bossbar");
        this.setUsage("/regionbar");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (!RegionBarConfig.regionbarEnabled) {
            sender.sendMessage(Component.text("Regionbar was already disabled!").color(TextColor.color(255, 0, 0)));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only player can use this command!").color(TextColor.color(255, 0, 0)));
            return true;
        }

        GlobalServerRegionBar regionbar = GlobalServerBarManager.get("region");
        if (regionbar.isPlayerVisible(player)) {
            player.sendMessage(Component.text("Disabled region bar").color(TextColor.color(0, 255, 0)));
            regionbar.setVisibilityForPlayer(player, false);
            return true;
        }

        player.sendMessage(Component.text("Enabled region bar").color(TextColor.color(0, 255, 0)));
        regionbar.setVisibilityForPlayer(player, true);

        return true;
    }
}