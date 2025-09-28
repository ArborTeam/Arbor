package me.earthme.luminol.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.earthme.luminol.config.ConfigsInstance;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.functions.StringTemplate;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.action.CommandTemplate;
import net.minecraft.server.dialog.action.ParsedTemplate;
import net.minecraft.world.entity.player.Player;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandDialog {
    public static void openGui(Player player, String name, ConfigsInstance config) {
        openGui(player, name, config, "");
    }

    public static void openGui(Player player, String name, ConfigsInstance config, String[] args) {
        openGui(player, name, config, args.length == 1 ? "" : args[1]);
    }

    public static void openGui(Player player, String name, ConfigsInstance config, String prefix) {
        int dotCount = prefix.length() - prefix.replace(".", "").length();
        if (prefix.equals("full")) {
            player.openDialog(
                    DialogUtil.createHolder(
                            name + "config",
                            config.getAllDataWithComment(),
                            name + "config submit ",
                            config.SPLIT
                    ));
            return;
        }

        if ((!prefix.isEmpty() && !prefix.endsWith("."))
                || (config.completeConfigPath(prefix, dotCount + 1).size()
                == config.completeConfigPath(prefix, dotCount + 2).size())
                || (config.completeConfigPath(prefix, dotCount + 1).isEmpty())) {
            List<String> list = config.getSingleConfig(prefix);
            if (list.isEmpty() && !prefix.endsWith(".")) {
                prefix += ".";
            } else {
                player.openDialog(
                        DialogUtil.createHolder(
                                name + "config",
                                config.getDataWithComment(list),
                                name + "config submit ",
                                config.SPLIT
                        ));
                return;
            }
        }

        List<String> keyList = config.completeConfigPath(prefix);
        DialogUtil.DialogBuilder builder = new DialogUtil.DialogBuilder();
        for (String key : keyList) {
            if (config.completeConfigPath(key, dotCount + 1).size()
                    == config.completeConfigPath(key, dotCount + 2).size()) {
                if (config.completeConfigPath(key, -1).size() != config.completeConfigPath(key, dotCount + 1).size()
                        || config.completeConfigPath(key, dotCount + 1).isEmpty()) {
                    continue;
                }
            }
            String raw = name + "config open-gui " + key + ".$(missing)";
            StringTemplate template = StringTemplate.fromString(raw);
            CommandTemplate commandTemplate = new CommandTemplate(new ParsedTemplate(raw, template));
            builder.addButton(
                    DialogUtil.createButton(
                            Component.translatable(key),
                            300,
                            Optional.of(commandTemplate)
                    ));
        }
        if (prefix.isEmpty()) {
            String raw = name + "config open-gui full$(missing)";
            StringTemplate template = StringTemplate.fromString(raw);
            CommandTemplate commandTemplate = new CommandTemplate(new ParsedTemplate(raw, template));
            builder.addButton(
                    DialogUtil.createButton(
                            Component.translatable("Show all configs"),
                            300,
                            Optional.of(commandTemplate)
                    ));
        }
        List<String> singleConfigs = config.getSingleConfig(prefix);
        if (!singleConfigs.isEmpty()) {
            String raw = name + "config open-gui " + prefix.substring(0, prefix.length() - 1) + "$(missing)";
            StringTemplate template = StringTemplate.fromString(raw);
            CommandTemplate commandTemplate = new CommandTemplate(new ParsedTemplate(raw, template));
            builder.addButton(
                    DialogUtil.createButton(
                            Component.translatable("Show options at this level"),
                            300,
                            Optional.of(commandTemplate)
                    ));
        }
        builder.setTitle(name + "config")
                .setPause(false)
                .setColumns(1);
        player.openDialog(
                DialogUtil.transformToHolder(
                        builder.build()
                ));
    }

    public static void processSubmit(CommandSender sender, ConfigsInstance config, String[] args) {
        String fullText = String.join(" ", args);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(fullText, type);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            config.setConfig(entry.getKey(), entry.getValue());
        }
        config.reloadAsync(true).thenAccept(nullValue -> sender.sendMessage(
                net.kyori.adventure.text.Component
                        .text("Apply config update successfully!")
                        .color(TextColor.color(0, 255, 0))
        ));
    }
}
