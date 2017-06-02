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

public final class JsonDouble implements JsonNumber {
    private static final long serialVersionUID = -7660029804467757867L;
    private final double number;

    public JsonDouble(final double number) {
        this.number = number;
    }

    @Override
    public void serialize(final StringBuilder output) {
        output.append(number);
    }

    @Override
    public String asString() {
        return Double.toString(number);
    }

    @Override
    public double asDouble() {
        return number;
    }

    @Override
    public long asLong() {
        return (long)number;
    }

    @Override
    public boolean asBoolean() {
        return number != 0.0;
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
        if (obj instanceof JsonDouble) {
            return number == ((JsonDouble)obj).number;
        }
        if (obj instanceof JsonLong) {
            return number == ((JsonLong)obj).asDouble();
        }
        return false;
    }

    @Override
    public int hashCode() {
        final long l = Double.doubleToLongBits(number);
        return (int)(l ^ (l >>> 32));
    }

    @Override
    public String toString() {
        return "JsonDouble(" + number + ")";
    }
}
