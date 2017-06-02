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

public final class JsonBoolean implements JsonPrimitive {
    private static final long serialVersionUID = -8177762902565829566L;
    public static final JsonBoolean TRUE = new JsonBoolean(true);
    public static final JsonBoolean FALSE = new JsonBoolean(false);

    private final boolean value;

    public JsonBoolean(final boolean value) {
        this.value = value;
    }

    @Override
    public void serialize(final StringBuilder output) {
        if (value) output.append("true");
        else output.append("false");
    }

    @Override
    public String asString() {
        return value ? "true" : "false";
    }

    @Override
    public double asDouble() {
        return value ? 1.0 : 0.0;
    }

    @Override
    public long asLong() {
        return value ? 1L : 0L;
    }

    @Override
    public boolean asBoolean() {
        return value;
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
        if (obj instanceof JsonBoolean) {
            return value == ((JsonBoolean)obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }

    @Override
    public String toString() {
        return "JsonBoolean(" + value + ")";
    }
}
