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

public final class JsonNull implements JsonPrimitive {
    private static final long serialVersionUID = 2645360900379194670L;
    public static final JsonNull INSTANCE = new JsonNull();

    @Override
    public boolean equals(final Object o) {
        return (o == null) || o.equals(null);
    }

    @Override
    public int hashCode() {
        return 7;
    }

    @Override
    public void serialize(final StringBuilder output) {
        output.append("null");
    }

    @Override
    public String asString() {
        throw new NullPointerException("A JSON null value");
    }

    @Override
    public double asDouble() {
        throw new NullPointerException("A JSON null value");
    }

    @Override
    public long asLong() {
        throw new NullPointerException("A JSON null value");
    }

    @Override
    public boolean asBoolean() {
        throw new NullPointerException("A JSON null value");
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public JsonType getType() {
        return JsonType.PRIMITIVE;
    }

    @Override
    public String toString() {
        return "JsonNull()";
    }
}
