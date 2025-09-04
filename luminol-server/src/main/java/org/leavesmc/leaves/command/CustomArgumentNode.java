/*
 * This file is part of Leaves (https://github.com/LeavesMC/Leaves)
 *
 * Leaves is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Leaves is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Leaves. If not, see <https://www.gnu.org/licenses/>.
 */

package org.leavesmc.leaves.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CustomArgumentNode<T, B> extends ArgumentNode<B> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends CustomArgumentNode>, CustomArgumentType<?, ?>> TYPES = new HashMap<>();

    protected CustomArgumentNode(String name, @NotNull CustomArgumentType<T, B> argumentType) {
        super(name, argumentType.getBaseArgumentType());
        TYPES.put(getClass(), argumentType);
    }

    public static <T, B> T transform(Class<? extends CustomArgumentNode<T, B>> nodeClass, B base) throws CommandSyntaxException {
        @SuppressWarnings("unchecked")
        CustomArgumentType<T, B> type = (CustomArgumentType<T, B>) TYPES.get(nodeClass);
        if (type == null) {
            throw new IllegalArgumentException("No custom argument type registered for " + nodeClass.getName());
        }
        return type.transform(base);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ArgumentBuilder<CommandSourceStack, ?> compileBase() {
        RequiredArgumentBuilder<CommandSourceStack, T> argumentBuilder = (RequiredArgumentBuilder<CommandSourceStack, T>) super.compileBase();

        if (!overrideSuggestions()) {
            CustomArgumentType<T, B> customArgumentType = (CustomArgumentType<T, B>) TYPES.get(getClass());
            argumentBuilder.suggests(
                    (context, builder) -> customArgumentType.getSuggestions(new CommandContext(context), builder)
            );
        }

        return argumentBuilder;
    }
}
