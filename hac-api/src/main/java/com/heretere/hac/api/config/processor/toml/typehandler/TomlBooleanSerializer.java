/*
 * MIT License
 *
 * Copyright (c) 2021 Justin Heflin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.heretere.hac.api.config.processor.toml.typehandler;

import com.google.common.collect.Lists;
import com.heretere.hac.api.config.processor.MultiSerializer;
import com.heretere.hac.api.config.processor.exception.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlParseResult;

import java.util.List;

/**
 * Used for boolean serialization in toml files.
 */
public final class TomlBooleanSerializer implements MultiSerializer<TomlParseResult, Boolean> {
    @Override public @NotNull Boolean deserialize(
        final @NotNull TomlParseResult parser,
        final @NotNull Class<?> exactType,
        final @NotNull String key
    ) throws InvalidTypeException {
        /* Make sure key is a valid boolean */
        if (!parser.isBoolean(key)) {
            throw new InvalidTypeException();
        }

        Boolean output = parser.getBoolean(key);

        /* No values should ever be null */
        if (output == null) {
            throw new InvalidTypeException();
        }

        return output;
    }

    @Override public @NotNull Boolean deserializeRaw(
        final @NotNull Class<?> exactType,
        final @NotNull Object value
    ) {
        return Boolean.valueOf(value.toString());
    }

    @Override public @NotNull List<String> serialize(final @NotNull Object value) {
        return Lists.newArrayList(this.getGenericType().cast(value).toString());
    }

    @Override @NotNull public Class<Boolean> getGenericType() {
        return Boolean.class;
    }
}
