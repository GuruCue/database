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
package com.gurucue.recommendations.type;

/**
 * Abstracts a time interval between two timestamps, inclusive.
 */
@Deprecated
public class TimestampInterval implements Comparable<TimestampInterval> {
    public final long beginTimeMillis;
    public final long endTimeMillis;

    public TimestampInterval(final long beginTimeMillis, final long endTimeMillis) {
        if (endTimeMillis < beginTimeMillis) {
            this.beginTimeMillis = endTimeMillis;
            this.endTimeMillis = beginTimeMillis;
        }
        else {
            this.beginTimeMillis = beginTimeMillis;
            this.endTimeMillis = endTimeMillis;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof TimestampInterval)) return false;
        final TimestampInterval other = (TimestampInterval)obj;
        if (other.beginTimeMillis != beginTimeMillis) return false;
        return other.endTimeMillis == endTimeMillis;
    }

    @Override
    public int hashCode() {
        return 31 * ((31 * 17) + (int)(beginTimeMillis ^ (beginTimeMillis >>> 32))) + (int)(endTimeMillis ^ (endTimeMillis >>> 32));
    }

    @Override
    public String toString() {
        return "TimestampInterval[" + beginTimeMillis + "," + endTimeMillis + "]";
    }

    /**
     * Violates the contract as set forth in {@link java.lang.Comparable},
     * because we want sorting only by end time. The structure itself
     * looks two-dimensional, but it actually describes an interval of
     * one dimension. Thus we compare only one member - end time - as this
     * is the most practical solution given our problem domain.
     *
     * @param o the instance to which to compare this instance
     * @return whether this instance is less than, equal, or greater than the specified instance
     * @see java.lang.Comparable
     */
    @Override
    public int compareTo(final TimestampInterval o) {
        if (endTimeMillis < o.endTimeMillis) return -1;
        if (endTimeMillis > o.endTimeMillis) return 1;
        return 0;
    }

    private static int SPACE_CHAR = Character.codePointAt(new char[] {' '}, 0);

    public static TimestampInterval fromDatabaseValue(final String value) {
        final int spaceIndex = value.indexOf(SPACE_CHAR);
        if (spaceIndex <= 0) return null; // first timestamp missing
        final int n = value.length();
        int endTimeOffset = spaceIndex + 1;
        while ((endTimeOffset < n) && (value.codePointAt(endTimeOffset) == SPACE_CHAR)) endTimeOffset++;
        if (endTimeOffset >= n) return null; // second timestamp missing
        try {
            return new TimestampInterval(Long.parseLong(value.substring(0, spaceIndex), 10) * 1000L, Long.parseLong(value.substring(endTimeOffset, n), 10) * 1000L);
        }
        catch (NumberFormatException e) {
            return null; // not numbers
        }
    }
}
