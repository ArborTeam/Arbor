package zone.little.arbor.commands.bar.sub;

import com.mojang.brigadier.arguments.BoolArgumentType;
import zone.little.arbor.api.config.ArborConfigsInstance;
import zone.little.arbor.config.ConfigManager;
import zone.little.arbor.config.modules.function.MembarConfig;
import zone.little.arbor.config.modules.function.RegionBarConfig;
import zone.little.arbor.config.modules.function.TpsBarConfig;
import zone.little.arbor.enums.EnumBarType;
import zone.little.arbor.functions.bars.TickableStatusBarList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
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

        // TODO
        @Contract(pure = true)
        private static boolean isEnabledInGlobal(@NonNull EnumBarType type) {
            return switch (type) {
                case TPS -> TpsBarConfig.tpsbarEnabled;
                case MEMORY -> MembarConfig.memoryBarEnabled;
                case REGION -> RegionBarConfig.regionbarEnabled;
            };
        }

        @Override
        protected boolean execute(@NotNull CommandContext context) {
            boolean enabled = isEnabledInGlobal(barType);

            boolean value = context.getArgument(BooleanArgument.class);
            if (value == enabled) {
                context.getSender().sendMessage(
                        Component
                                .text("Bar type with " + barType.getName() + " was already " + (value ? "enabled" : "disabled") + "!")
                                .color(TextColor.color(255, 0, 0)));
            } else {
                ArborConfigsInstance config = ConfigManager.getConfigs(barType.getConfigOrigin());
                if (config.setConfig(barType.getConfigPath(), value)) {

                    context.getSender().sendMessage(
                            Component
                                    .text("Bar type with " + barType.getName() + (value ? " enabled" : " disabled") + " successfully!")
                                    .color(TextColor.color(0, 255, 0))
                    );

                    config.reloadAsync(true).thenAccept(_ -> {
                        TickableStatusBarList.raiseGlobalReload();
                    });
                }
            }
            return true;
        }
    }
}
