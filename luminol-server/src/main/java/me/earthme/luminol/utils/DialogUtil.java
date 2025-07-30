package me.earthme.luminol.utils;

import net.minecraft.commands.functions.StringTemplate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.action.CommandTemplate;
import net.minecraft.server.dialog.action.ParsedTemplate;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.input.BooleanInput;
import net.minecraft.server.dialog.input.NumberRangeInput;
import net.minecraft.server.dialog.input.TextInput;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DialogUtil {
    public static Holder<Dialog> createHolder(String title, Map<String, Object> map, String commandPrefix) {
        return transformToHolder(
                createDialog(title, map, commandPrefix)
        );
    }

    public static Holder<Dialog> createHolder(String title, List<String> list, String commandPrefix) {
        return transformToHolder(
                createDialog(title, list, commandPrefix)
        );
    }

    public static Holder<Dialog> transformToHolder(Dialog dialog) {
        return Holder.direct(dialog);
    }

    public static MultiActionDialog createDialog(String title, List<String> options, String commandPrefix) {
        DialogBuilder builder = new DialogBuilder();
        for (String option : options) {
            builder.addButton(
                    createButton(
                            Component.translatable(option),
                            300,
                            Optional.empty()
                    ));
        }

        builder.setTitle(title)
                .setPause(false)
                .setColumns(1);

        return builder.build();
    }

    public static MultiActionDialog createDialog(String title, Map<String, Object> map, String commandPrefix) {
        DialogBuilder builder = new DialogBuilder();
        JSONObject valueBuilder = new JSONObject();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String label = entry.getKey();
            Object value = entry.getValue();
            String key = label.replace(".", "___")
                    .replace("-", "__");
            valueBuilder.put(label, "$(" + key + ")");

            switch (value) {
                case Boolean boolValue -> {
                    Input checkbox = createCheckbox(label, key, boolValue, "true", "false");
                    builder.addInput(checkbox);
                }
                case String stringValue -> {
                    Input textbox = createTextInput(label, key, stringValue, 300, true, 2147483647, null);
                    builder.addInput(textbox);
                }
                case Number numberValue -> {
                    Input numberInput = createTextInput(label, key, value.toString(), 300, true, 2147483647, null);
                    builder.addInput(numberInput);
                }
                default -> {
                }
            }
        }
        String raw = commandPrefix + valueBuilder.toJSONString();
        StringTemplate template = StringTemplate.fromString(raw);
        CommandTemplate confirmTemplate = new CommandTemplate(new ParsedTemplate(raw, template));
        builder.addButton(createButton(
                        Component.translatable("Confirm"),
                        150,
                        Optional.of(confirmTemplate)
                ))
                .addButton(createButton(
                        Component.translatable("Cancel"),
                        150,
                        Optional.empty()
                ));

        builder.setTitle(title)
                .setPause(false)
                .setColumns(1);

        return builder.build();
    }

    public static Input createCheckbox(String label, String key, boolean value, String trueText, String falseText) {
        return new Input(key, new BooleanInput(
                Component.translatable(label),
                value,
                trueText,
                falseText
        ));
    }

    public static Input createTextInput(String label, String key, String value, int width, boolean labelVisible, int maxLength, TextInput.MultilineOptions multilineOptions) {
        return new Input(key, new TextInput(
                width,
                Component.translatable(label),
                labelVisible,
                value,
                maxLength,
                Optional.ofNullable(multilineOptions)
        ));
    }

    // TODO: number input is not work now
    public static Input createNumberInput(String label, String key, Number value, NumberRangeInput.RangeInfo rangeInfo) {
        return new Input(key, new NumberRangeInput(
                300,
                Component.translatable(label),
                value.getClass().getName(),
                rangeInfo
        ));
    }


    public static ActionButton createButton(Component key, int width, Optional<Action> action) {
        CommonButtonData buttonData = new CommonButtonData(
                key,
                width
        );
        return new ActionButton(buttonData, action);
    }

    public static class DialogBuilder {
        String title = "";
        Optional<Component> externalTitle = Optional.empty();
        boolean canCloseWithEscape = true;
        boolean pause = true;
        int actionClose = 0;
        int columns = 2;
        List<ActionButton> buttons = new ArrayList<>();
        List<Input> inputs = new ArrayList<>();
        List<DialogBody> bodies = new ArrayList<>();
        Optional<ActionButton> exitButton = Optional.empty();

        public DialogBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public DialogBuilder setExternalTitle(Component externalTitle) {
            this.externalTitle = Optional.of(externalTitle);
            return this;
        }

        public DialogBuilder setCanCloseWithEscape(boolean canCloseWithEscape) {
            this.canCloseWithEscape = canCloseWithEscape;
            return this;
        }

        public DialogBuilder setPause(boolean pause) {
            this.pause = pause;
            return this;
        }

        // 0 for close, 1 for none, 2 for wait
        public DialogBuilder setActionClose(int actionClose) {
            this.actionClose = actionClose;
            return this;
        }

        public DialogBuilder setColumns(int columns) {
            this.columns = columns;
            return this;
        }

        public DialogBuilder addButton(ActionButton button) {
            this.buttons.add(button);
            return this;
        }

        public int getButtonCount() {
            return this.buttons.size();
        }

        public DialogBuilder addInput(Input input) {
            this.inputs.add(input);
            return this;
        }

        public int getInputCount() {
            return this.inputs.size();
        }

        public DialogBuilder addBody(DialogBody body) {
            this.bodies.add(body);
            return this;
        }

        public DialogBuilder setExitButton(ActionButton exitButton) {
            this.exitButton = Optional.of(exitButton);
            return this;
        }

        public MultiActionDialog build() {
            CommonDialogData data = new CommonDialogData(
                    Component.translatable(title),
                    externalTitle,
                    canCloseWithEscape,
                    pause,
                    DialogAction.values()[actionClose], // if paused you must do something
                    bodies,
                    inputs
            );
            return new MultiActionDialog(data, buttons, exitButton, columns);
        }
    }
}
