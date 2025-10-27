package me.earthme.luminol.commands.config.sub;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.earthme.luminol.commands.config.ConfigCommand;
import me.earthme.luminol.commands.config.ConfigSubcommand;
import me.earthme.luminol.utils.CommandDialog;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.leaves.command.ArgumentNode;
import org.leavesmc.leaves.command.CommandContext;

import java.util.Arrays;

public class SubmitCommand extends ConfigSubcommand {
    public SubmitCommand(ConfigCommand parent) {
        super("submit", parent);
        children(
                new PathArgument(parent)
        );
    }

    private class PathArgument extends ArgumentNode<String> {
        protected final ConfigCommand parent;

        PathArgument(ConfigCommand parent) {
            super("path", StringArgumentType.greedyString());
            this.parent = parent;
        }

        @Override
        protected boolean execute(@NotNull CommandContext context) {
            String content = context.getRange().get(context.getInput());
            String[] args = org.apache.commons.lang3.StringUtils.split(content, ' ');
            CommandDialog.processSubmit(context.getSender(), parent.config, Arrays.copyOfRange(args, 2, args.length));
            return true;
        }
    }
}
