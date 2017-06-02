/*
 * This file is part of Guru Cue Search & Recommendation Engine.
 * Copyright (C) 2017 Guru Cue Ltd.
 *
 * Guru Cue Search & Recommendation Engine is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Guru Cue Search & Recommendation Engine is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Guru Cue Search & Recommendation Engine. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.gurucue.recommendations.json;

import com.gurucue.recommendations.entity.value.Value;

public final class JsonString implements JsonPrimitive {
    private static final long serialVersionUID = 3835217357631047873L;
    private final String text;

    public JsonString(final String text) {
        this.text = text;
    }

    @Override
    public void serialize(final StringBuilder output) {
        output.append('"');
        Value.escapeJson(text, output);
        output.append('"');
    }

    @Override
    public String asString() {
        return text;
    }

    @Override
    public double asDouble() {
        return Double.parseDouble(text);
    }

    @Override
    public long asLong() {
        return Long.parseLong(text);
    }

    @Override
    public boolean asBoolean() {
        return Boolean.parseBoolean(text);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public JsonType getType() {
        return JsonType.PRIMITIVE;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof JsonString) {
            final String other = ((JsonString)obj).text;
            return (text == other) || ((text != null) && text.equals(other));
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (text == null) return 17;
        return text.hashCode();
    }

    @Override
    public String toString() {
        if (text == null) return "JsonString(null)";
        return "JsonString(\"" + text.replace("\"", "\\\"") + "\")";
    }
}
