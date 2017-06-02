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
package com.gurucue.recommendations.entity.value;

import com.gurucue.recommendations.type.ValueType;

/**
 * A special class abstracting a null value. Do not use it for an actual
 * value in a database or in a response; it is intended for internal processing
 * to mark a null value where a null value is not allowed by the underlying
 * implementation (such as an ImmutableMap).
 */
public final class NullValue extends Value {
    public static final NullValue INSTANCE = new NullValue(); // there can only be one null

    private NullValue() {
        super(ValueType.BOOLEAN, false); // TODO : would a null ValueType be a better choice?
    }

    @Override
    public void toJson(final StringBuilder output) {
        throw new UnsupportedOperationException("NullValue cannot be used in an output");
    }

    @Override
    public boolean matches(final Value value) {
        return (value == null) || (value instanceof NullValue);
    }

    @Override
    public Value replace(final Value newValue) {
        return newValue;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public boolean[] asBooleans() {
        return new boolean[0];
    }

    @Override
    public long asInteger() {
        return 0;
    }

    @Override
    public long[] asIntegers() {
        return new long[0];
    }

    @Override
    public double asFloat() {
        return 0;
    }

    @Override
    public double[] asFloats() {
        return new double[0];
    }

    @Override
    public String asString() {
        return null;
    }

    @Override
    public String[] asStrings() {
        return new String[0];
    }

    @Override
    public TimestampIntervalValue asTimestampInterval() {
        return null;
    }

    @Override
    public TimestampIntervalValue[] asTimestampIntervals() {
        return new TimestampIntervalValue[0];
    }

    @Override
    public TranslatableValue asTranslatable() {
        return null;
    }

    @Override
    public TranslatableValue[] asTranslatables() {
        return new TranslatableValue[0];
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return true;
        if (other instanceof NullValue) return true;
        return false;
    }

    @Override
    public String toString() {
        return "NullValue()";
    }
}
