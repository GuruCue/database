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
package com.gurucue.recommendations.blender;

import java.util.function.Predicate;

/**
 * A stateless filter, thus filters each item independently of other
 * items. Functionally it is similar to {@link Predicate}, but operating
 * explicitly on DataValue, having logging support and the
 * {@link #onStart(DataSet)} and {@link #onEnd(DataSet)} methods.
 *
 * @see DataSet
 */
public interface StatelessFilter<V extends DataValue> extends LoggingSupport {

    default StatelessFilter<V> and(final StatelessFilter<V> other) {
        final StatelessFilter<V> original = this;
        return new StatelessFilter<V>() {
            @Override
            public boolean test(final V v) {
                return (original.test(v) && other.test(v));
            }
            @Override
            public void onStart(final DataSet<V> dataSet) {
                original.onStart(dataSet);
                other.onStart(dataSet);
            }
            @Override
            public void onEnd(final DataSet<V> dataSet) {
                original.onEnd(dataSet);
                other.onEnd(dataSet);
            }
            @Override
            public void writeLog(final StringBuilder output) {
                original.writeLog(output);
                other.writeLog(output);
            }
        };
    }

    default StatelessFilter<V> negate() {
        final StatelessFilter<V> original = this;
        return new StatelessFilter<V>() {
            @Override
            public boolean test(final V v) {
                return !original.test(v);
            }
            @Override
            public void onStart(final DataSet<V> dataSet) {
                original.onStart(dataSet);
            }
            @Override
            public void onEnd(final DataSet<V> dataSet) {
                original.onEnd(dataSet);
            }
            @Override
            public void writeLog(final StringBuilder output) {
                original.writeLog(output);
            }
        };
    }

    default StatelessFilter<V> or(final StatelessFilter<V> other) {
        final StatelessFilter<V> original = this;
        return new StatelessFilter<V>() {
            @Override
            public boolean test(final V v) {
                return (original.test(v) || other.test(v));
            }
            @Override
            public void onStart(final DataSet<V> dataSet) {
                original.onStart(dataSet);
                other.onStart(dataSet);
            }
            @Override
            public void onEnd(final DataSet<V> dataSet) {
                original.onEnd(dataSet);
                other.onEnd(dataSet);
            }
            @Override
            public void writeLog(final StringBuilder output) {
                original.writeLog(output);
                other.writeLog(output);
            }
        };
    }

    default void onStart(final DataSet<V> dataSet) {}

    default void onEnd(final DataSet<V> dataSet) {}

    boolean test(V v);
}
