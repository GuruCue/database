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

import java.util.Arrays;

public final class MultiValue extends Value {
    public final Value[] values;

    public MultiValue(final Value[] values) {
        super(values.length == 0 ? null : values[0].valueType, true);
        this.values = values;
    }

    public MultiValue remove(final Value value) {
        final boolean[] flags = new boolean[values.length];
        for (int i = flags.length - 1; i >= 0; i--) flags[i] = true;
        int leftover = values.length;

        if (value instanceof MultiValue) {
            final Value[] valuesToRemove = ((MultiValue)value).values;
            for (int i = valuesToRemove.length - 1; i >= 0; i--) {
                final Value v = valuesToRemove[i];
                for (int j = values.length - 1; j >= 0; j--) {
                    if (flags[j] && v.matches(values[j])) {
                        flags[j] = false;
                        leftover--;
                    }
                }
            }
        }
        else {
            for (int j = values.length - 1; j >= 0; j--) {
                if (value.matches(values[j])) {
                    flags[j] = false;
                    leftover--;
                }
            }
        }

        if (leftover <= 0) return null;
        final Value[] newValues = new Value[leftover];
        int i = 0;
        for (int j = 0; j < values.length; j++) {
            if (flags[j]) newValues[i++] = values[j];
        }
        return new MultiValue(newValues);
    }

    public MultiValue set(final Value value) {
        if (value instanceof MultiValue) {
            // the given value is actually an array of values
            final Value[] otherValues = ((MultiValue)value).values;
/*            final boolean[] flags = new boolean[otherValues.length];
            for (int i = flags.length - 1; i >= 0; i--) flags[i] = true;
            int additionalValues = otherValues.length;
            outer:
            for (int i = otherValues.length - 1; i >= 0; i--) {
                final Value v = otherValues[i];
                for (int j = values.length - 1; j >= 0; j--) {
                    if (v.matches(values[j])) {
                        flags[i] = false;
                        additionalValues--;
                        continue outer;
                    }
                }
            }
            if (additionalValues <= 0) return this;
            final Value[] newValues = new Value[values.length + additionalValues];
            for (int i = 0; i < values.length; i++) newValues[i] = values[i];
            int j = values.length;
            for (int i = 0; i < otherValues.length; i++) {
                if (flags[i]) newValues[j++] = otherValues[i];
            }
            return new MultiValue(newValues);*/
            final Value[] tempValues = new Value[values.length + otherValues.length]; // the result will contain at most this much values
            final Value[] thisValues = Arrays.copyOf(values, values.length);
            final Value[] remainingValues = Arrays.copyOf(otherValues, otherValues.length);
            int l = 0;
            for (int i = 0; i < thisValues.length; i++) {
                Value tval = thisValues[i];
                if (tval == null) continue;
                for (int j = 0; j < remainingValues.length; j++) {
                    final Value o = remainingValues[j];
                    if (o == null) continue;
                    if (tval.matches(o)) {
                        tval = tval.replace(o);
                        remainingValues[j] = null;
                    }
                }
                for (int j = i + 1; j < thisValues.length; j++) {
                    final Value o = thisValues[j];
                    if (o == null) continue;
                    if (tval.matches(o)) {
                        tval = tval.replace(o);
                        thisValues[j] = null;
                    }
                }
                tempValues[l++] = tval;
            }
            for (int i = 0; i < remainingValues.length; i++) {
                final Value tval = remainingValues[i];
                if (tval == null) continue;
                tempValues[l++] = tval;
            }
            if (l == 0) return null;
            return new MultiValue(Arrays.copyOf(tempValues, l));
        }

        // the new value is a single value, first try to see if replacing it is enough
        for (int j = values.length - 1; j >= 0; j--) {
            if (value.matches(values[j])) {
                final Value[] newValues = new Value[values.length];
                for (int k = 0; k < j; k++) newValues[k] = values[k];
                newValues[j] = values[j].replace(value);
                for (int k = j + 1; k < newValues.length; k++) newValues[k] = values[k];
                return new MultiValue(newValues);
            }
        }

        // the new value doesn't match any existing value, lengthen the Value[] array and add the new Value
        final Value[] newValues = new Value[values.length + 1];
        for (int i = 0; i < values.length; i++) newValues[i] = values[i];
        newValues[values.length] = value;
        return new MultiValue(newValues);
    }

    @Override
    public void toJson(final StringBuilder output) {
        output.append("[");
        final int n = values.length;
        if (n > 0) {
            values[0].toJson(output);
            for (int i = 1; i < n; i++) {
                output.append(",");
                values[i].toJson(output);
            }
        }
        output.append("]");
    }

    @Override
    public boolean matches(final Value value) {
        if (!(value instanceof MultiValue)) return false;
        final Value[] otherValues = ((MultiValue)value).values;
        if (values.length != otherValues.length) return false;
        final boolean[] otherMatched = new boolean[values.length];
        for (int i = otherMatched.length - 1; i >= 0; i--) otherMatched[i] = false;
        // TODO: implement backtracking on failures to match
        outer:
        for (int i = values.length - 1; i >= 0; i--) {
            final Value v = values[i];
            for (int j = otherValues.length - 1; j >= 0; j--) {
                if (otherMatched[j]) continue; // this one was already matched
                if (v.matches(otherValues[j])) {
                    otherMatched[j] = true;
                    continue outer;
                }
            }
            return false; // none matched in the current iteration
        }
        return true; // every value matched
    }

    @Override
    public Value replace(final Value newValue) {
        throw new UnsupportedOperationException("MultiValue does not support replace()");
    }

    @Override
    public boolean asBoolean() {
        final boolean[] r = asBooleans();
        return r.length > 0 ? r[0] : false;
    }

    @Override
    public boolean[] asBooleans() {
        final boolean[] bools = new boolean[values.length];
        for (int i = bools.length - 1; i >= 0; i--) bools[i] = values[i].asBoolean();
        return bools;
    }

    @Override
    public long asInteger() {
        final long[] r = asIntegers();
        return r.length > 0 ? r[0] : 0L;
    }

    @Override
    public long[] asIntegers() {
        final long[] longs = new long[values.length];
        for (int i = longs.length - 1; i >= 0; i--) longs[i] = values[i].asInteger();
        return longs;
    }

    @Override
    public double asFloat() {
        final double[] r = asFloats();
        return r.length > 0 ? r[0] : 0.0;
    }

    @Override
    public double[] asFloats() {
        final double[] floats = new double[values.length];
        for (int i = floats.length - 1; i >= 0; i--) floats[i] = values[i].asFloat();
        return floats;
    }

    @Override
    public String asString() {
        final String[] r = asStrings();
        return r.length > 0 ? r[0] : "";
    }

    @Override
    public String[] asStrings() {
        final String[] strings = new String[values.length];
        for (int i = strings.length - 1; i >= 0; i--) strings[i] = values[i].asString();
        return strings;
    }

    @Override
    public TimestampIntervalValue asTimestampInterval() {
        final TimestampIntervalValue[] r = asTimestampIntervals();
        return r.length > 0 ? r[0] : TimestampIntervalValue.fromMillis(0L, 0L);
    }

    @Override
    public TimestampIntervalValue[] asTimestampIntervals() {
        final TimestampIntervalValue[] vals = new TimestampIntervalValue[values.length];
        for (int i = vals.length - 1; i >= 0; i--) vals[i] = values[i].asTimestampInterval();
        return vals;
    }

    @Override
    public TranslatableValue asTranslatable() {
        final TranslatableValue[] r = asTranslatables();
        if (r.length > 0) return r[0];
        final Language l = DataManager.getProvider().getLanguageCodes().unknown;
        return new TranslatableValue("", l, ImmutableMap.of(l, ""));
    }

    @Override
    public TranslatableValue[] asTranslatables() {
        final TranslatableValue[] vals = new TranslatableValue[values.length];
        for (int i = vals.length - 1; i >= 0; i--) vals[i] = values[i].asTranslatable();
        return vals;
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int r = 17;
        // this is an unordered list, so first order it the way that the result will always be the same no matter how the values are laid out
        final int[] hashes = new int[values.length];
        for (int i = values.length - 1; i >= 0; i--) hashes[i] = values[i] == null ? 0 : values[i].hashCode();
        Arrays.sort(hashes);
        for (int i = hashes.length - 1; i >= 0; i--) {
            r = 31 * r + hashes[i];
        }
        return r;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other instanceof MultiValue) {
            final Value[] otherValues = ((MultiValue)other).values;
            // comparing unordered lists == sets of values
            if (values.length != otherValues.length) return false;
            matcher:
            for (int i = values.length - 1; i >= 0; i--) {
                final Value v = values[i];
                for (int j = otherValues.length - 1; j >= 0; j--) {
                    final Value o = otherValues[j];
                    if (o == null) {
                        if (v == null) continue matcher;
                    }
                    else if (o.equals(v)) continue matcher;
                }
                return false; // other values exhausted -> no match
            }
            return true; // everything matched somewhere
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(256);
        sb.append("MultiValue(valueType=").append(valueType).append(", values=");
        if (values == null) sb.append("null");
        else {
            sb.append("[");
            if (values.length > 0) {
                sb.append(values[0].toString());
                for (int i = 1; i < values.length; i++) sb.append(", ").append(values[i].toString());
            }
            sb.append("]");
        }
        return sb.append(")").toString();
    }
}
