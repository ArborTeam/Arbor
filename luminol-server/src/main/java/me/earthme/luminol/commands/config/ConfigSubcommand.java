package me.earthme.luminol.commands.config;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.LiteralNode;

public abstract class ConfigSubcommand extends LiteralNode {
    protected final ConfigCommand father;

    protected ConfigSubcommand(String name, ConfigCommand father) {
        super(name);
        this.father = father;
    }

    @Override
    public boolean requires(@NotNull CommandSourceStack source) {
        return hasPermission(source.getSender());
    }

    protected boolean hasPermission(CommandSender sender) {
        return father.hasPermission(sender, this.name);
    }
}
