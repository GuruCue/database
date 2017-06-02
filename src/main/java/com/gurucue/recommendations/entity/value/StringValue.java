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

public final class StringValue extends Value {
    public final String value;

    public StringValue(final String value) {
        super(ValueType.STRING, false);
        this.value = value == null ? "" : value;
    }

    @Override
    public void toJson(final StringBuilder output) {
        output.append("\"");
        escapeJson(value, output);
        output.append("\"");
    }

    public boolean matches(final Value value) {
        if (value.isArray) return false;
        if (value instanceof TranslatableValue) return false;
        switch (value.valueType) {
            case INTEGER:
            case TIMESTAMP:
            case STRING:
            case FLOAT:
                return this.value.equals(value.asString());
            case BOOLEAN:
                return asBoolean() == value.asBoolean();
            case TIMESTAMP_INTERVAL:
                final TimestampIntervalValue t1 = asTimestampInterval();
                final TimestampIntervalValue t2 = value.asTimestampInterval();
                return (t1.beginMillis == t2.beginMillis) && (t1.endMillis == t2.endMillis);
        }
        return false; // timestamp interval
    }

    @Override
    public Value replace(final Value newValue) {
        return newValue;
    }

    @Override
    public boolean asBoolean() {
        return "true".equals(value) || "1".equals(value) || "yes".equals(value) || "on".equals(value);
    }

    @Override
    public boolean[] asBooleans() {
        return new boolean[]{asBoolean()};
    }

    @Override
    public long asInteger() {
        try {
            return Long.parseLong(value, 10);
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public long[] asIntegers() {
        return new long[]{asInteger()};
    }

    @Override
    public double asFloat() {
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public double[] asFloats() {
        return new double[]{asFloat()};
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public String[] asStrings() {
        return new String[]{value};
    }

    @Override
    public TimestampIntervalValue asTimestampInterval() {
        final int firstSpaceIndex = value.indexOf(' ');
        if (firstSpaceIndex > 0) {
            int i = firstSpaceIndex + 1;
            final int n = value.length();
            while ((i < n) && (value.charAt(i) == ' ')) i++;
            if (i < n) {
                long beginSeconds, endSeconds;
                try {
                    beginSeconds = Long.parseLong(value.substring(0, firstSpaceIndex), 10);
                    endSeconds = Long.parseLong(value.substring(i, n), 10);
                    return TimestampIntervalValue.fromSeconds(beginSeconds, endSeconds);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

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
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other instanceof StringValue) {
            return ((StringValue)other).value.equals(value);
        }
        return false;
    }

    @Override
    public String toString() {
        return "StringValue(valueType=" + valueType + ", value=\"" + value.replace("\"", "\\\"") + "\")";
    }
}
