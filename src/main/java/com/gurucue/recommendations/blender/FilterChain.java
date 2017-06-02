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
 * A proxy to store filters in a filter chain, to be used later by a DataSet.
 *
 * @see DataSet
 */
public class FilterChain<V extends DataValue> implements Filterable<V>, Iterable<Object> {
    final List<Object> filters = new ArrayList<>();

    @Override
    public FilterChain<V> filter(final StatelessFilter<V> filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public FilterChain<V> filter(final StatefulFilter<V> filter) {
        filters.add(filter);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FilterChain<V> filter(final FilterChain<V> filterChain) {
        filters.addAll(filterChain.filters);
        return this;
    }

    @Override
    public Filterable<V> filter(final StatelessFilterChain<V> filterChain) {
        filters.addAll(filterChain.filters);
        return this;
    }

    @Override
    public Iterator<Object> iterator() {
        return filters.iterator();
    }

    @Override
    public void forEach(final Consumer<? super Object> action) {
        filters.forEach(action);
    }

    @Override
    public Spliterator<Object> spliterator() {
        return filters.spliterator();
    }
}
