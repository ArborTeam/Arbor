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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.leavesmc.leaves.command.CommandUtils.getListClosestMatchingLast;

public class SetCommand extends ConfigSubcommand {
    public SetCommand(ConfigCommand father) {
        super("set", father);
        children(new PathArgument(father));
    }

    static class PathArgument extends ArgumentNode<String> {
        protected final ConfigCommand father;

        PathArgument(ConfigCommand father) {
            super("path", StringArgumentType.string());
            this.father = father;
            children(
                    new ValueArgument(father)
            );
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
            context.getSender().sendMessage(
                    Component
                            .text("Config " + path + " is " + father.config.getConfig(path) + "!")
                            .color(TextColor.color(0, 255, 0))
            );
            return true;
        }

        private class ValueArgument extends ArgumentNode<String> {
            private final ConfigCommand father;

            private ValueArgument(ConfigCommand father) {
                super("value", StringArgumentType.greedyString());
                this.father = father;
            }

            @Override
            protected CompletableFuture<Suggestions> getSuggestions(@NotNull CommandContext context, @NotNull SuggestionsBuilder builder) {
                String path = context.getArgument(PathArgument.class);
                if (!father.config.getAllConfigPaths("").contains(path)) {
                    return builder
                            .suggest("<ERROR CONFIG>", net.minecraft.network.chat.Component.literal("This config path does not exist."))
                            .buildFuture();
                }
                Object value = father.config.getConfigOrigin(path);
                String[] suggestions = father.config.getConfigSuggestions(path);
                builder.suggest(value.toString(), net.minecraft.network.chat.Component.literal("Default value")
                        .withStyle(style -> style.withColor(net.minecraft.network.chat.TextColor.fromLegacyFormat(net.minecraft.ChatFormatting.GRAY))));
                if (suggestions == null) {
                    if (value instanceof Boolean) {
                        builder.suggest(String.valueOf(!(Boolean) value));
                    }
                } else {
                    for (String s : suggestions) {
                        if (!Objects.equals(s, value.toString())) {
                            builder.suggest(s);
                        }
                    }
                }

                return builder.buildFuture();
            }

            @Override
            protected boolean execute(@NotNull CommandContext context) {
                String path = context.getArgument(PathArgument.class);
                String value = context.getArgument(ValueArgument.class);
                if (father.config.setConfig(path, value)) {
                    father.config.reloadAsync(true).thenAccept(nullValue -> context.getSender().sendMessage(
                            Component
                                    .text("Set Config " + path + " to " + value + " successfully!")
                                    .color(TextColor.color(0, 255, 0))
                    ));
                } else {
                    context.getSender().sendMessage(
                            Component
                                    .text("Failed to set config " + path + " to " + value + "!")
                                    .color(TextColor.color(255, 0, 0))
                    );
                }
                return true;
            }
        }
    }
}
