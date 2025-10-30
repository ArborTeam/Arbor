package me.earthme.luminol.commands.bar.sub;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.earthme.luminol.commands.bar.BarCommand;
import me.earthme.luminol.enums.EnumBarType;
import me.earthme.luminol.functions.bars.AbstractGlobalServerBar;
import me.earthme.luminol.functions.bars.GlobalServerBarManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.ArgumentNode;
import org.leavesmc.leaves.command.CommandContext;
import org.leavesmc.leaves.command.LiteralNode;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ToggleCommand extends LiteralNode {
    private final String bar_name;

    public ToggleCommand(String barName) {
        super("toggle");
        this.bar_name = barName;
        children(
                PlayerArg::new
        );
    }

    @Override
    protected boolean execute(@NotNull CommandContext context) throws CommandSyntaxException {
        if (!(context.getSender() instanceof Player player)) {
            context.getSender().sendMessage(Component.text("Only player can display bars!").color(TextColor.color(255, 0, 0)));
            return true;
        }
        return execute0(context, player);
    }

    public boolean execute0(@NotNull CommandContext context, Player player) {
        AbstractGlobalServerBar bar;

        try {
            bar = GlobalServerBarManager.get(EnumBarType.valueOf(bar_name.toUpperCase()));
        } catch (IllegalArgumentException e) {
            context.getSender().sendMessage(Component.text(e.getMessage()).color(TextColor.color(255, 0, 0)));
            return true;
        }

        if (!bar.enabled()) {
            context.getSender().sendMessage(Component.text("Bar type with " + bar_name + " was already disabled!").color(TextColor.color(255, 0, 0)));
        }

        if (bar.isPlayerVisible(player)) {
            context.getSender().sendMessage(Component.text("Disabled Bar type with " + bar_name + " for " + player.getName()).color(TextColor.color(0, 255, 0)));
            bar.setVisibilityForPlayer(player, false);
            return true;
        }

        context.getSender().sendMessage(Component.text("Enabled Bar type with " + bar_name + " for " + player.getName()).color(TextColor.color(0, 255, 0)));
        bar.setVisibilityForPlayer(player, true);
        return true;
    }

    private class PlayerArg extends ArgumentNode<String> {
        protected PlayerArg() {
            super("player", StringArgumentType.string());
        }

        @Override
        protected CompletableFuture<Suggestions> getSuggestions(@NotNull CommandContext context, @NotNull SuggestionsBuilder builder) {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> builder.suggest(player.getName()));
            return builder.buildFuture();
        }

        @Override
        protected boolean execute(@NotNull CommandContext context) {
            String name = context.getArgument(PlayerArg.class);
            Player player = Bukkit.getServer().getPlayer(name);
            if (player == null) {
                player = Bukkit.getServer().getPlayer(UUID.fromString(name));
                if (player == null) {
                    context.getSender().sendMessage(Component.text("Player " + name + " was not found!").color(TextColor.color(255, 0, 0)));
                    return true;
                }
            }
            return execute0(context, player);
        }
    }

    private String getOldName() {
        String oldName;
        switch (bar_name) {
            case "memory" -> oldName = "membar";
            case "tps" -> oldName = "tpsbar";
            case "region" -> oldName = "regionbar";
            default -> oldName = bar_name;
        }
        return oldName;
    }

    protected ArgumentBuilder<CommandSourceStack, ?> compile0() {
        ArgumentBuilder<CommandSourceStack, ?> builder = Commands.literal(getOldName()).requires(this::requires);

        if (canExecute()) {
            builder = builder.executes(mojangCtx -> {
                CommandContext ctx = new CommandContext(mojangCtx);
                return execute(ctx) ? 1 : 0;
            });
        }

        return builder;
    }

    @Override
    public boolean requires(@NotNull CommandSourceStack source) {
        return BarCommand.hasPermission(source.getSender(), this.bar_name, this.name);
    }

    @SuppressWarnings("unchecked")
    public void register() { // register for old version command
        MinecraftServer.getServer()
                .getCommands()
                .getDispatcher()
                .register((LiteralArgumentBuilder<CommandSourceStack>) compile0());
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    public void unregister() {
        CommandDispatcher<CommandSourceStack> dispatcher = MinecraftServer.getServer()
                .getCommands()
                .getDispatcher();
        dispatcher.getRoot().removeCommand(getOldName());
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }
}
