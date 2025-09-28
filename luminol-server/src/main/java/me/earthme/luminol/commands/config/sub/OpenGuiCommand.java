package me.earthme.luminol.commands.config.sub;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.earthme.luminol.commands.config.ConfigCommand;
import me.earthme.luminol.commands.config.ConfigSubcommand;
import me.earthme.luminol.utils.CommandDialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.ArgumentNode;
import org.leavesmc.leaves.command.CommandContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.leavesmc.leaves.command.CommandUtils.getListClosestMatchingLast;

public class OpenGuiCommand extends ConfigSubcommand {
    public OpenGuiCommand(ConfigCommand father) {
        super("open-gui", father);
        children(
                new PathArgument(father)
        );
    }

    @Override
    protected boolean execute(@NotNull CommandContext context) throws CommandSyntaxException {
        if (context.getSender() instanceof CraftPlayer cPlayer) {
            final Player player = cPlayer.getHandle();
            CommandDialog.openGui(player, father.name, father.config);
        } else {
            context.getSender().sendMessage(
                    Component
                            .text("Only player can use this command!")
                            .color(TextColor.color(255, 0, 0))
            );
        }
        return true;
    }

    static class PathArgument extends ArgumentNode<String> {
        protected final ConfigCommand father;

        PathArgument(ConfigCommand father) {
            super("path", StringArgumentType.string());
            this.father = father;
        }

        @Override
        protected CompletableFuture<Suggestions> getSuggestions(@NotNull CommandContext context, @NotNull SuggestionsBuilder builder) {
            String path = context.getArgumentOrDefault(PathArgument.class, "");
            int dotIndex = path.lastIndexOf(".");
            builder = builder.createOffset(builder.getInput().lastIndexOf(' ') + dotIndex + 2);
            List<String> list = father.config.completeConfigPath(path);
            list.add("full");
            for (String s : getListClosestMatchingLast(
                    path.substring(dotIndex + 1),
                    list
            )) {
                builder.suggest(s.substring(path.lastIndexOf('.') + 1));
            }
            return builder.buildFuture();
        }

        @Override
        protected boolean execute(@NotNull CommandContext context) {
            if (context.getSender() instanceof CraftPlayer cPlayer) {
                final Player player = cPlayer.getHandle();
                CommandDialog.openGui(player, father.name, father.config, context.getArgument(PathArgument.class));
            } else {
                context.getSender().sendMessage(
                        Component
                                .text("Only player can use this command!")
                                .color(TextColor.color(255, 0, 0))
                );
            }
            return true;
        }
    }
}
