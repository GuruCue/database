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

/**
 * Something that can be filtered, and return the filtered result.
 *
 * @see DataSet
 */
public interface Filterable<V extends DataValue> {
    /**
     * Filter using the provided stateless filter. Stateless filters are
     * chained together in the order they are set using this method, but
     * the actual filtering isn't done until an operation is done, e.g.
     * accessing an item. This way filters can be ordered into a chain that
     * is traversed for each item only when it is absolutely necessary to
     * do so.
     *
     * @param filter the filter
     * @return the filtered instance
     */
    Filterable<V> filter(StatelessFilter<V> filter);

    /**
     * Filter using the provided stateful filter. Stateful filtering requires
     * random access to items of a DataSet, therefore filter chaining, as is
     * done with stateless filters, is not
     * possible. A stateful filter is given the whole DataSet and must return
     * a filtered DataSet, which is then returned by this method to enable
     * further (virtual) chaining of filters.
     *
     * @param filter the filter
     * @return the filtered instance
     */
    Filterable<V> filter(StatefulFilter<V> filter);

    /**
     * Apply the provided filtering chain. The chain is traversed and
     * appropriate filter() methods invoked, depending on
     * statefulness of a filter.
     *
     * @param filterChain the chain of stateless and/or stateful filters to apply
     * @return the filtered instance
     */
    Filterable<V> filter(FilterChain<V> filterChain);

    /**
     * Apply the provided filtering chain. The chain is traversed and
     * the filter() method for stateless filters invoked.
     *
     * @param filterChain the chain of stateless and/or stateful filters to apply
     * @return the filtered instance
     */
    Filterable<V> filter(StatelessFilterChain<V> filterChain);
}
