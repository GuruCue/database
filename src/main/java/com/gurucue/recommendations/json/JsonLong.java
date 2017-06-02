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

public final class JsonLong implements JsonNumber {
    private static final long serialVersionUID = 7810180574581551901L;
    private final long number;

    public JsonLong(final long number) {
        this.number = number;
    }

    @Override
    public void serialize(final StringBuilder output) {
        output.append(number);
    }

    @Override
    public String asString() {
        return Long.toString(number);
    }

    @Override
    public double asDouble() {
        return (double)number;
    }

    @Override
    public long asLong() {
        return number;
    }

    @Override
    public boolean asBoolean() {
        return number != 0L;
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
        if (obj instanceof JsonLong) {
            return number == ((JsonLong)obj).number;
        }
        if (obj instanceof JsonDouble) {
            return ((double)number) == ((JsonDouble)obj).asDouble();
        }
        return false;
    }

    @Override
    public int hashCode() {
        // to be compatible with JsonDouble (as in equals()), we have to compute a hash of a double, not of a long
        final long l = Double.doubleToLongBits((double)number);
        return (int)(l ^ (l >>> 32));
    }

    @Override
    public String toString() {
        return "JsonLong(" + number + ")";
    }
}
