package me.earthme.luminol.commands;

import me.earthme.luminol.config.CommandDialog;
import me.earthme.luminol.config.ConfigsInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommand extends Command {
    private ConfigsInstance config;
    private final String name;

    public ConfigCommand(String name) {
        super(name + "config");
        this.name = name;
        this.setPermission(name + ".commands." + name + "config");
        this.setDescription("Manage config file");
        this.setUsage("/" + name + "config");
    }

    public void initConfig(ConfigsInstance config) {
        this.config = config;
    }

    public void wrongUse(CommandSender sender) {
        sender.sendMessage(
                Component
                        .text("Wrong use!")
                        .color(TextColor.color(255, 0, 0))
        );
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        final List<String> result = new ArrayList<>();

        if (args.length == 1) {
            result.add("set");
            result.add("reset");
            result.add("reload");
            result.add("open-gui");
            // result.add("submit"); // do not public, you can direct use by open-gui
        } else if (args.length == 2 && (args[0].equals("query") || args[0].equals("set") || args[0].equals("reset"))) {
            result.addAll(config.completeConfigPath(args[1]));
        }
        return result;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!this.testPermission(sender)) {
            sender.sendMessage(Component
                    .text("No permission to execute this command!")
                    .color(TextColor.color(255, 0, 0))
            );
        }

        if (args.length < 1) {
            wrongUse(sender);
            return true;
        }

        switch (args[0]) {
            case "reload" -> {
                config.reloadAsync().thenAccept(nullValue -> sender.sendMessage(
                        Component
                                .text("Reloaded config file!")
                                .color(TextColor.color(0, 255, 0))
                ));
            }
            case "set" -> {
                if (args.length > 3) {
                    wrongUse(sender);
                    return true;
                } else if (args.length == 2) {
                    sender.sendMessage(
                            Component
                                    .text("Config " + args[1] + " is " + config.getConfig(args[1]) + "!")
                                    .color(TextColor.color(0, 255, 0))
                    );
                } else if (config.setConfig(args[1], args[2])) {
                    config.reloadAsync().thenAccept(nullValue -> sender.sendMessage(
                            Component
                                    .text("Set Config " + args[1] + " to " + args[2] + " successfully!")
                                    .color(TextColor.color(0, 255, 0))
                    ));
                } else {
                    sender.sendMessage(
                            Component
                                    .text("Failed to set config " + args[1] + " to " + args[2] + "!")
                                    .color(TextColor.color(255, 0, 0))
                    );
                }
            }
            case "reset" -> {
                if (args.length != 2) {
                    wrongUse(sender);
                    return true;
                } else {
                    config.resetConfig(args[1]);
                    config.reloadAsync().thenAccept(nullValue -> sender.sendMessage(
                            Component
                                    .text("Reset Config " + args[1] + " to " + config.getConfig(args[1]) + " successfully!")
                                    .color(TextColor.color(0, 255, 0))
                    ));
                }
            }
            case "open-gui" -> {
                if (sender instanceof CraftPlayer cPlayer) {
                    final Player player = cPlayer.getHandle();
                    CommandDialog.openGui(player, name, config, args);
                } else {
                    sender.sendMessage(
                            Component
                                    .text("Only player can use this command!")
                                    .color(TextColor.color(255, 0, 0))
                    );
                }
            }
            case "submit" -> {
                CommandDialog.processSubmit(sender, config, args);
            }
            default -> sender.sendMessage(
                    Component
                            .text("Unknown action!")
                            .color(TextColor.color(255, 0, 0))
            );
        }

        return true;
    }
}