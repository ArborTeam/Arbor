package me.earthme.luminol.commands.bar.sub;

import com.mojang.brigadier.arguments.BoolArgumentType;
import me.earthme.luminol.config.ConfigManager;
import me.earthme.luminol.config.ConfigsInstance;
import me.earthme.luminol.enums.EnumBarType;
import me.earthme.luminol.functions.bars.AbstractGlobalServerBar;
import me.earthme.luminol.functions.bars.GlobalServerBarManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.ArgumentNode;
import org.leavesmc.leaves.command.CommandContext;
import org.leavesmc.leaves.command.LiteralNode;

public class ConfigEditCommand extends LiteralNode {
    private final String bar_name;

    public ConfigEditCommand(String barName) {
        super("config");
        this.bar_name = barName;
        children(
                BooleanArgument::new
        );
    }

    private class BooleanArgument extends ArgumentNode<Boolean> {
        protected BooleanArgument() {
            super("boolean", BoolArgumentType.bool());
        }

        @Override
        protected boolean execute(@NotNull CommandContext context) {
            AbstractGlobalServerBar bar;

            try {
                bar = GlobalServerBarManager.get(EnumBarType.valueOf(bar_name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                context.getSender().sendMessage(Component.text(e.getMessage()).color(TextColor.color(255, 0, 0)));
                return true;
            }

            String configPath;
            switch (bar_name) {
                case "memory" -> configPath = "function.membar.enabled";
                case "region" -> configPath = "function.regionbar.enabled";
                case "tps" -> configPath = "function.tpsbar.enabled";
                default -> {
                    return false;
                }
            }

            boolean value = context.getArgument(BooleanArgument.class);
            if (value == bar.enabled()) {
                context.getSender().sendMessage(
                        Component
                                .text("Bar type with " + bar_name + " was already " + (value ? "enabled" : "disabled") + "!")
                                .color(TextColor.color(255, 0, 0)));
            } else {
                ConfigsInstance config = ConfigManager.configfiles.get("luminol");
                if (config.setConfig(configPath, value)) {
                    config.reloadAsync(true).thenAccept(nullValue -> context.getSender().sendMessage(
                            Component
                                    .text("Bar type with " + bar_name + (value ? " enabled" : " disabled") + " successfully!")
                                    .color(TextColor.color(0, 255, 0))
                    ));
                }
            }
            return true;
        }
    }
}
