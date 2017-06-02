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

import com.google.common.collect.ImmutableMap;
import com.gurucue.recommendations.data.DataManager;
import com.gurucue.recommendations.entity.Language;
import com.gurucue.recommendations.type.ValueType;

public final class BooleanValue extends Value {
    public final boolean value;

    public BooleanValue(final boolean value) {
        super(ValueType.BOOLEAN, false);
        this.value = value;
    }

    @Override
    public void toJson(final StringBuilder output) {
        if (value) output.append("true");
        else output.append("false");
    }

    public boolean matches(final Value value) {
        if (value.isArray) return false;
        if (value instanceof TranslatableValue) return false;
        switch (value.valueType) {
            case INTEGER:
            case TIMESTAMP:
                if (this.value) return value.asInteger() != 0;
                return value.asInteger() == 0;
            case STRING:
                final String v = value.asString();
                return "true".equals(v) || "1".equals(v) || "yes".equals(v) || "on".equals(v);
            case BOOLEAN:
                return this.value == value.asBoolean();
            case FLOAT:
                if (this.value) return value.asFloat() != 0;
                return value.asFloat() == 0;
        }
        return false; // timestamp interval
    }

    @Override
    public Value replace(final Value newValue) {
        return newValue;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public boolean[] asBooleans() {
        return new boolean[]{value};
    }

    @Override
    public long asInteger() {
        return value ? 1L : 0L;
    }

    @Override
    public long[] asIntegers() {
        return new long[]{asInteger()};
    }

    @Override
    public double asFloat() {
        return value ? 1.0 : 0.0;
    }

    @Override
    public double[] asFloats() {
        return new double[]{asFloat()};
    }

    @Override
    public String asString() {
        return value ? "true" : "false";
    }

    @Override
    public String[] asStrings() {
        return new String[]{asString()};
    }

    @Override
    public TimestampIntervalValue asTimestampInterval() {
        final long l = asInteger();
        return TimestampIntervalValue.fromSeconds(l, l);
    }

    @Override
    public TimestampIntervalValue[] asTimestampIntervals() {
        return new TimestampIntervalValue[]{asTimestampInterval()};
    }

    @Override
    public TranslatableValue asTranslatable() {
        final String s = asString();
        final Language l = DataManager.getProvider().getLanguageCodes().unknown;
        return new TranslatableValue(s, l, ImmutableMap.of(l, s));
    }

    @Override
    public TranslatableValue[] asTranslatables() {
        return new TranslatableValue[]{asTranslatable()};
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        return 31 * 17 + (value ? 1 : 0);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other instanceof BooleanValue) {
            return ((BooleanValue)other).value == value;
        }
        return false;
    }

    @Override
    public String toString() {
        return "BooleanValue(valueType=" + valueType + ", value=" + value + ")";
    }
}
