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
    private final EnumBarType barType;

    public ConfigEditCommand(EnumBarType barType) {
        super("config");
        this.barType = barType;
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
                bar = GlobalServerBarManager.get(barType);
            } catch (IllegalArgumentException e) {
                context.getSender().sendMessage(Component.text(e.getMessage()).color(TextColor.color(255, 0, 0)));
                return true;
            }

            boolean value = context.getArgument(BooleanArgument.class);
            if (value == bar.enabled()) {
                context.getSender().sendMessage(
                        Component
                                .text("Bar type with " + barType.getName() + " was already " + (value ? "enabled" : "disabled") + "!")
                                .color(TextColor.color(255, 0, 0)));
            } else {
                ConfigsInstance config = ConfigManager.getConfigs(barType.getConfigOrigin());
                if (config.setConfig(barType.getConfigPath(), value)) {
                    config.reloadAsync(true).thenAccept(nullValue -> context.getSender().sendMessage(
                            Component
                                    .text("Bar type with " + barType.getName() + (value ? " enabled" : " disabled") + " successfully!")
                                    .color(TextColor.color(0, 255, 0))
                    ));
                }
            }
            return true;
        }
    }
}
