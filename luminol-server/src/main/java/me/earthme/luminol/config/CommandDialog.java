package me.earthme.luminol.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.earthme.luminol.utils.DialogUtil;
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
    public static void openGui(Player player, String name, ConfigsInstance config, String[] args) {
        String prefix = args.length == 1 ? "" : args[1];
        int dotCount = prefix.length() - prefix.replace(".", "").length();
        if (prefix.equals("full")) {
            player.openDialog(
                    DialogUtil.createHolder(
                            name + "config",
                            config.getAllData(),
                            name + "config submit "
                    ));
        } else if (config.completeConfigPath(prefix, dotCount + 1).size()
                == config.completeConfigPath(prefix, dotCount + 2).size()) {
            player.openDialog(
                    DialogUtil.createHolder(
                            name + "config",
                            config.getData(prefix),
                            name + "config submit "
                    ));
        } else if (prefix.endsWith(".") || prefix.isEmpty()) {
            List<String> keyList = config.completeConfigPath(prefix);
            DialogUtil.DialogBuilder builder = new DialogUtil.DialogBuilder();
            for (String key : keyList) {
                String raw = name + "config open-gui " + key + ".$(missing)";
                StringTemplate template = StringTemplate.fromString(raw);
                CommandTemplate commandTemplate = new CommandTemplate(new ParsedTemplate(raw, template));
                builder.addButton(
                        DialogUtil.createButton(
                                Component.translatable(key),
                                150,
                                Optional.of(commandTemplate)
                        ));
            }
            if (args.length == 1) {
                String raw = name + "config open-gui full$(missing)";
                StringTemplate template = StringTemplate.fromString(raw);
                CommandTemplate commandTemplate = new CommandTemplate(new ParsedTemplate(raw, template));
                builder.addButton(
                        DialogUtil.createButton(
                                Component.translatable("Show all configs"),
                                150,
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
                                150,
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
        } else {
            List<String> list = config.getSingleConfig(prefix);
            player.openDialog(
                    DialogUtil.createHolder(
                            name + "config",
                            config.getData(list),
                            name + "config submit "
                    ));
        }
    }

    public static void processSubmit(CommandSender sender, ConfigsInstance config, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length - 1; i++) {
            sb.append(args[i]).append(" ");
        }
        sb.append(args[args.length - 1]);
        String fullText = sb.toString();
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(fullText, type);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            config.setConfig(entry.getKey(), entry.getValue());
        }
        config.reloadAsync().thenAccept(nullValue -> sender.sendMessage(
                net.kyori.adventure.text.Component
                        .text("Apply config update successfully!")
                        .color(TextColor.color(0, 255, 0))
        ));
    }
}
