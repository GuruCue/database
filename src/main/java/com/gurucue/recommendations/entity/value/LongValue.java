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

public final class LongValue extends Value {
    public final long value;

    public LongValue(final long value, final boolean isTimestamp) {
        super(isTimestamp ? ValueType.TIMESTAMP : ValueType.INTEGER, false);
        this.value = isTimestamp ? value * 1000L : value; // convert to milliseconds
    }

    private LongValue(final LongValue original) {
        super(original.valueType, false);
        this.value = original.value;
    }

    @Override
    public void toJson(final StringBuilder output) {
        if (valueType == ValueType.TIMESTAMP) output.append(value / 1000L); // convert back to seconds
        else output.append(value);
    }

    public boolean matches(final Value value) {
        if (value.isArray) return false;
        if (value instanceof TranslatableValue) return false;
        switch (value.valueType) {
            case INTEGER:
            case TIMESTAMP:
                return this.value == value.asInteger();
            case STRING:
                return asString().equals(value.asString());
            case BOOLEAN:
                return asBoolean() == value.asBoolean();
            case FLOAT:
                return asFloat() == value.asFloat();
            case TIMESTAMP_INTERVAL:
                final TimestampIntervalValue t = value.asTimestampInterval();
                final long v = valueType == ValueType.INTEGER ? this.value * 1000L : this.value;
                return (v == t.beginMillis) && (t.beginMillis == t.endMillis);
        }
        return false;
    }

    @Override
    public Value replace(final Value newValue) {
        return newValue;
    }

    @Override
    public boolean asBoolean() {
        return value != 0;
    }

    @Override
    public boolean[] asBooleans() {
        return new boolean[]{asBoolean()};
    }

    @Override
    public long asInteger() {
        return value;
    }

    @Override
    public long[] asIntegers() {
        return new long[]{value};
    }

    @Override
    public double asFloat() {
        return (double)value;
    }

    @Override
    public double[] asFloats() {
        return new double[]{asFloat()};
    }

    @Override
    public String asString() {
        return Long.toString(value, 10);
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
        return 31 * 17 + ((int)(value ^ (value >>> 32)));
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other instanceof LongValue) {
            return ((LongValue)other).value == value;
        }
        return false;
    }

    @Override
    public String toString() {
        return "LongValue(valueType=" + valueType + ", value=" + value + ")";
    }
}
