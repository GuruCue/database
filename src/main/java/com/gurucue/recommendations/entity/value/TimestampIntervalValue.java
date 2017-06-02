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
import com.gurucue.recommendations.type.TimestampInterval;
import com.gurucue.recommendations.type.ValueType;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public final class TimestampIntervalValue extends Value {
    public final long beginMillis;
    public final long endMillis;

    private TimestampIntervalValue(final long beginMillis, final long endMillis) {
        super(ValueType.TIMESTAMP_INTERVAL, false);
        this.beginMillis = beginMillis;
        this.endMillis = endMillis;
    }

    public TimestampIntervalValue(final TimestampInterval timestampInterval) {
        super(ValueType.TIMESTAMP_INTERVAL, false);
        this.beginMillis = timestampInterval.beginTimeMillis;
        this.endMillis = timestampInterval.endTimeMillis;
    }

    @Override
    public void toJson(final StringBuilder output) {
        output.append("{\"begin\":");
        output.append(beginMillis / 1000L);
        output.append(",\"end\":");
        output.append(endMillis / 1000L);
        output.append("}");
    }

    @Override
    public boolean matches(final Value value) {
        if (value.isArray) return false;
        switch (value.valueType) {
            case INTEGER:
            case TIMESTAMP:
            case STRING:
            case TIMESTAMP_INTERVAL:
                final TimestampIntervalValue t1 = value.asTimestampInterval();
                return (t1.beginMillis == beginMillis) && (t1.endMillis == endMillis);
        }
        return false;
    }

    @Override
    public Value replace(final Value newValue) {
        return newValue;
    }

    @Override
    public boolean asBoolean() {
        return true;
    }

    @Override
    public boolean[] asBooleans() {
        return new boolean[]{asBoolean()};
    }

    @Override
    public long asInteger() {
        return beginMillis / 1000L;
    }

    @Override
    public long[] asIntegers() {
        return new long[]{asInteger()};
    }

    @Override
    public double asFloat() {
        return (double) beginMillis / 1000L;
    }

    @Override
    public double[] asFloats() {
        return new double[]{asFloat()};
    }

    @Override
    public String asString() {
        final StringBuilder sb = new StringBuilder(25);
        sb.append(beginMillis / 1000L);
        sb.append(" ");
        sb.append(endMillis / 1000L);
        return sb.toString();
    }

    @Override
    public String[] asStrings() {
        return new String[]{asString()};
    }

    @Override
    public TimestampIntervalValue asTimestampInterval() {
        return this;
    }

    @Override
    public TimestampIntervalValue[] asTimestampIntervals() {
        return new TimestampIntervalValue[]{this};
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
        int r = 31 * 17 + ((int)(beginMillis ^ (beginMillis >>> 32)));
        r = 31 * r + ((int)(endMillis ^ (endMillis >>> 32)));
        return r;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other instanceof TimestampIntervalValue) {
            final TimestampIntervalValue otherValue = (TimestampIntervalValue)other;
            return (otherValue.beginMillis == beginMillis) && (otherValue.endMillis == endMillis);
        }
        return false;
    }

    public void toString(final StringBuilder output) {
        output.append("[").append(beginMillis / 1000L).append(", ").append(endMillis / 1000L).append("]");
    }

    @Override
    public String toString() {
        return "TimestampIntervalValue(valueType=" + valueType + ", beginMillis=" + beginMillis + ", endMillis=" + endMillis + ")";
    }

    public static TimestampIntervalValue fromSeconds(final long beginSeconds, final long endSeconds) {
        return new TimestampIntervalValue(beginSeconds * 1000L, endSeconds * 1000L);
    }

    public static TimestampIntervalValue fromMillis(final long beginMillis, final long endMillis) {
        return new TimestampIntervalValue(beginMillis, endMillis);
    }

    public static TimestampIntervalValue[] cleanupIntervals(final TimestampIntervalValue[] intervals) {
        if (intervals == null) return new TimestampIntervalValue[0];
        if (intervals.length <= 1) return intervals;
        final TreeMap<Long, TimestampIntervalValue> mergingMap = new TreeMap<>();
        processIntervals(mergingMap, intervals);
        if (intervals.length == mergingMap.size()) return intervals; // no merging occurred, return the original array
        final Collection<TimestampIntervalValue> validityCollection = mergingMap.values();
        return validityCollection.toArray(new TimestampIntervalValue[validityCollection.size()]);
    }

    public static TimestampIntervalValue[] mergeIntervals(final TimestampIntervalValue[] intervals1, final TimestampIntervalValue[] intervals2) {
        final TreeMap<Long, TimestampIntervalValue> mergingMap = new TreeMap<>();
        processIntervals(mergingMap, intervals1);
        processIntervals(mergingMap, intervals2);
        final Collection<TimestampIntervalValue> validityCollection = mergingMap.values();
        return validityCollection.toArray(new TimestampIntervalValue[validityCollection.size()]);
    }

    private static void processIntervals(final TreeMap<Long, TimestampIntervalValue> mergingMap, final TimestampIntervalValue[] intervals) {
        if (intervals == null) return;
        for (int i = 0; i < intervals.length; i++) {
            TimestampIntervalValue interval = intervals[i];
            final Long key = Long.valueOf(interval.beginMillis);
            // check intervals that begin after this one
            Map.Entry<Long, TimestampIntervalValue> firstNextEntry = mergingMap.ceilingEntry(key);
            while (firstNextEntry != null) {
                final TimestampIntervalValue firstNext = firstNextEntry.getValue();
                if (firstNext.beginMillis <= interval.endMillis) {
                    // the first next interval overlaps the current interval, merge them
                    mergingMap.remove(firstNextEntry.getKey());
                    if (firstNext.endMillis > interval.endMillis) {
                        // extend the current interval to meet the end of the first next interval
                        interval = TimestampIntervalValue.fromMillis(interval.beginMillis, firstNext.endMillis);
                        break;
                    }
                }
                else break;
                firstNextEntry = mergingMap.ceilingEntry(key);
            }
            // check the interval that begins before this one
            final Map.Entry<Long, TimestampIntervalValue> firstPreviousEntry = mergingMap.floorEntry(key);
            if (firstPreviousEntry != null) {
                final TimestampIntervalValue firstPrevious = firstPreviousEntry.getValue();
                if (firstPrevious.endMillis >= interval.beginMillis) {
                    // the first previous interval overlaps the current interval, merge them
                    if (firstPrevious.endMillis < interval.endMillis) {
                        mergingMap.remove(firstPreviousEntry.getKey());
                        interval = TimestampIntervalValue.fromMillis(firstPrevious.beginMillis, interval.endMillis);
                    }
                    else interval = null; // nothing to merge/add, the current interval is a subset of the overlapping interval
                }
            }
            if (interval != null) {
                mergingMap.put(interval.beginMillis, interval);
            }
        }
    }
}
