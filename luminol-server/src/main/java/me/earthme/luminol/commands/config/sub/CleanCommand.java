package me.earthme.luminol.commands.config.sub;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

public class CleanCommand extends ConfigSubcommand {
    public CleanCommand(ConfigCommand father) {
        super("clean", father);
        children(
                new PathArgument(father)
        );
    }

    @Override
    protected boolean execute(@NotNull CommandContext context) throws CommandSyntaxException {
        context.getSender().sendMessage(
                Component
                        .text("If you want to clean up useless items in the configuration file, please use /" + father.name + "config clean confirm")
                        .color(TextColor.color(255, 0, 0))
        );
        return true;
    }

    static class PathArgument extends ArgumentNode<String> {
        protected final ConfigCommand father;

        PathArgument(ConfigCommand father) {
            super("confirm", StringArgumentType.string());
            this.father = father;
        }

        @Override
        protected CompletableFuture<Suggestions> getSuggestions(@NotNull CommandContext context, @NotNull SuggestionsBuilder builder) {
            builder.suggest("confirm");
            return builder.buildFuture();
        }

        @Override
        protected boolean execute(@NotNull CommandContext context) throws CommandSyntaxException {
            String confirm = context.getArgument(PathArgument.class);
            if (!"confirm".equals(confirm)) {
                context.getSender().sendMessage(
                        Component
                                .text("Please use /" + father.name + "config clean confirm to confirm!")
                                .color(TextColor.color(255, 0, 0))
                );
                return true;
            }
            father.config.clean();
            context.getSender().sendMessage(
                    Component
                            .text("Clean up in the configuration file successfully!")
                            .color(TextColor.color(0, 255, 0))
            );
            return true;
        }
    }
}
