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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A proxy to store stateless filters in a filter chain, to be used by a DataSet or a filter.
 */
public class StatelessFilterChain<V extends DataValue> implements StatelessFilter<V>, Iterable<StatelessFilter<V>> {
    final List<StatelessFilter<V>> filters = new ArrayList<>();

    public StatelessFilterChain<V> filter(final StatelessFilter<V> filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public Iterator<StatelessFilter<V>> iterator() {
        return filters.iterator();
    }

    @Override
    public void forEach(final Consumer<? super StatelessFilter<V>> action) {
        filters.forEach(action);
    }

    @Override
    public Spliterator<StatelessFilter<V>> spliterator() {
        return filters.spliterator();
    }

    @Override
    public boolean test(final V v) {
        final int n = filters.size();
        for (int i = 0; i < n; i++) {
            if (!filters.get(i).test(v)) return false;
        }
        return true;
    }

    @Override
    public void writeLog(final StringBuilder output) {
        filters.forEach((final StatelessFilter<V> filter) -> filter.writeLog(output));
    }
}
