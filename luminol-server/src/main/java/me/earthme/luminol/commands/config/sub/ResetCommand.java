package me.earthme.luminol.commands.config.sub;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.earthme.luminol.commands.config.ConfigCommand;
import me.earthme.luminol.commands.config.ConfigSubcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.ArgumentNode;
import org.leavesmc.leaves.command.CommandContext;

import java.util.concurrent.CompletableFuture;

import static org.leavesmc.leaves.command.CommandUtils.getListClosestMatchingLast;

public class ResetCommand extends ConfigSubcommand {
    public ResetCommand(ConfigCommand father) {
        super("reset", father);
        children(new PathArgument(father));
    }

    private class PathArgument extends ArgumentNode<String> {
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
            for (String s : getListClosestMatchingLast(
                    path.substring(dotIndex + 1),
                    father.config.completeConfigPath(path)
            )) {
                builder.suggest(s.substring(path.lastIndexOf('.') + 1));
            }

            return builder.buildFuture();
        }

        @Override
        protected boolean execute(@NotNull CommandContext context) {
            String path = context.getArgumentOrDefault(PathArgument.class, "");
            father.config.resetConfig(path);
            father.config.reloadAsync().thenAccept(nullValue -> context.getSender().sendMessage(
                    Component
                            .text("Reset Config " + path + " to " + father.config.getConfig(path) + " successfully!")
                            .color(TextColor.color(0, 255, 0))
            ));
            return true;
        }
    }
}
