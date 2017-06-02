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

import com.gurucue.recommendations.ProcessingException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A HashSet with the ability to resolve duplicates as they are added.
 * It uses two underlying containers to perform its operations efficiently:
 * a HashMap and an array of objects. Thus mutation methods are somewhat
 * slower, but iteration is fast and ordered.
 */
public final class DataSet<V extends DataValue> implements Set<V>, LoggingSupport, Filterable<V> {
    protected static final int ARRAY_INCREMENT = 10000;

    protected final DuplicateResolver<V> duplicateResolver;
    protected final Map<V, V> productMap = new HashMap<>();
    protected V[] data; // a sparse array
    protected int length; // right border of the array
    protected int size; // actual number of items in the set, must be equal to productMap.size()
    protected int modificationCount = 0; // for detecting mutation during (spl)iteration
    protected final LinkedList<StatelessFilter<V>> filters = new LinkedList<>();
    protected final LinkedList<LoggingSupport> subLoggers = new LinkedList<>();
    protected final HashMap<String, Integer> limitTags = new HashMap<>();

    /**
     * A constructor for an empty dataset.
     *
     * @param duplicateResolver the resolver for duplicate items
     * @param parentDataSet the DataSet from which logically follows this new DataSet, or null if this is the first DataSet
     */
    @SuppressWarnings("unchecked")
    public DataSet(final DuplicateResolver<V> duplicateResolver, final DataSet<V> parentDataSet) {
        this.duplicateResolver = duplicateResolver;
        this.data = (V[]) new DataValue[ARRAY_INCREMENT];
        size = length = 0;
        if ((parentDataSet != null) && (!parentDataSet.subLoggers.isEmpty())) {
            subLoggers.addAll(parentDataSet.subLoggers);
        }
    }

    /**
     * A constructor supplying the initial content for the dataset.
     *
     * @param duplicateResolver the resolver for duplicate items
     * @param items the items with which to initialize the content of the DataSet
     * @param parentDataSet the DataSet from which logically follows this new DataSet, or null if this is the first DataSet
     */
    @SuppressWarnings("unchecked")
    public DataSet(final DuplicateResolver<V> duplicateResolver, V[] items, final DataSet<V> parentDataSet) {
        this.duplicateResolver = duplicateResolver;
        final int N = items.length;
        final int l = ((N / ARRAY_INCREMENT) + 1) * ARRAY_INCREMENT; // round to the next ARRAY_INCREMENT multiple
        final V[] data = (V[]) new DataValue[l];
        int i = 0;
        for (int j = 0; j < N; j++) {
            final V item = items[j];
            if (item == null) continue;
            item.arrayIndex = i;
            data[i] = item;
            i++;
        }
        this.data = data;
        size = length = i;
        if ((parentDataSet != null) && (!parentDataSet.subLoggers.isEmpty())) {
            subLoggers.addAll(parentDataSet.subLoggers);
        }
    }

    /**
     * The clone constructor.
     *
     * @param source the DataSet to clone
     */
    public DataSet(final DataSet<V> source) {
        this.duplicateResolver = source.duplicateResolver;
        this.productMap.putAll(source.productMap);
        this.data = Arrays.copyOf(source.data, source.data.length);
        this.length = source.length;
        this.size = source.size;
        this.filters.addAll(source.filters);
        this.subLoggers.addAll(source.subLoggers);
    }

    /**
     * The private clone constructor, used to create a DataSet instance by
     * cloning the given DataSet but not copying its data.
     *
     * @param source the DataSet to clone
     * @param copyData whether to copy the source DataSet's data
     */
    @SuppressWarnings("unchecked")
    private DataSet(final DataSet<V> source, final boolean copyData) {
        this.duplicateResolver = source.duplicateResolver;
        this.subLoggers.addAll(source.subLoggers); // preserve the logs
        if (copyData) {
            // full cloning
            this.productMap.putAll(source.productMap);
            this.data = Arrays.copyOf(source.data, source.data.length);
            this.length = source.length;
            this.size = source.size;
            this.filters.addAll(source.filters);
        }
        else {
            // zero data
            this.data = (V[]) new DataValue[ARRAY_INCREMENT];
            size = length = 0;
        }
    }

    @SuppressWarnings("unchecked")
    protected DataSet(final DuplicateResolver<V> duplicateResolver, final V[] data, final Map<V, V> builderMap, final DataSet<V> parentDataSet) {
        this.duplicateResolver = duplicateResolver;
        final long timeStart = System.nanoTime();
        productMap.putAll(builderMap);
        this.data = data;
        size = length = productMap.size();
        final long timeEnd = System.nanoTime();
        if ((parentDataSet != null) && (!parentDataSet.subLoggers.isEmpty())) {
            subLoggers.addAll(parentDataSet.subLoggers);
        }
        subLoggers.add(new InfoLogStringBuilder(new StringBuilder(64).append("----- DataSet created from ").append(size).append(" items in ").append(timeEnd - timeStart).append(" ns\n")));
    }

    @Override
    public int size() {
        applyFilters();
        return size;
    }

    @Override
    public boolean isEmpty() {
        applyFilters();
        return size == 0;
    }

    @Override
    public boolean contains(final Object o) {
        if (o instanceof DataValue) {
            applyFilters();
            return productMap.containsKey(o);
        }
        return false;
    }

    @Override
    public Iterator<V> iterator() {
        applyFilters();
        return new DataSetIterator<>(this);
    }

    @Override
    public Object[] toArray() {
        applyFilters();
        final Object[] result = new Object[size];
        int i = 0;
        int j = 0;
        while (i < length) {
            final V o = data[i++];
            if (o != null) result[j++] = o;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        applyFilters();
        final T[] result = a.length < size ? (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size) : a;
        int i = 0;
        int j = 0;
        while (i < length) {
            final V o = data[i++];
            if (o != null) result[j++] = (T)o;
        }
        while (j < result.length) result[j++] = null;
        return result;
    }

    @Override
    public boolean add(final V value) {
        if (value == null) throw new NullPointerException();
        applyFilters();
        final V existing = productMap.put(value, value);
        if (existing == null) {
            // a new item has been added
            modificationCount++;
            if (length >= data.length) {
                data = Arrays.copyOf(data, length + ARRAY_INCREMENT);
            }
            value.arrayIndex = length;
            data[length] = value;
            length++;
            size++;
            return true;
        }
        // an existing item has been replaced, check if this is the correct action
        if (value == existing) return false; // both are the same instance
        final V correct = duplicateResolver.resolve(value, existing);
        if (correct == value) {
            // retain the new value (a replacement has occurred)
            modificationCount++;
            data[value.arrayIndex = existing.arrayIndex] = value;
            return true;
        }
        // retain the old value (no replacement occurred)
        productMap.put(correct, correct);
        return false;
    }

    protected void compactData() {
        modificationCount++;
        int i = 0;
        final int l = length; // local variables to make dereferencing faster
        final V[] d = data;
        while ((i < l) && (d[i] != null)) {
            i++;
        }
        int j = i;
        while (i < l) {
            final V o = d[i++];
            if (o == null) continue;
            o.arrayIndex = j;
            d[j++] = o;
        }
        length = j;
    }

    private boolean internalRemove(final V o) {
        final V existing = productMap.remove(o);
        if (existing == null) return false;
        data[o.arrayIndex = existing.arrayIndex] = null;
        modificationCount++;
        size--;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(final Object o) {
        if (o == null) return false;
        applyFilters();
        if ((size + size) < length) compactData();
        return internalRemove((V)o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        applyFilters();
        for (final Object o : c) {
            if (!productMap.containsKey(o)) return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(final Collection<? extends V> c) {
        applyFilters();
        final int oldModificationCount = modificationCount;
        if (c instanceof DataSet) {
            // optimized implementation
            final DataSet<V> ds = (DataSet<V>)c;
            final V[] d = ds.data;
            final int l = ds.length;
            for (int i = 0; i < l; i++) {
                final V v = d[i];
                if (v == null) continue;
                add(v);
            }
        }
        else {
            for (final Object o : c) {
                add((V) o);
            }
        }
        return modificationCount != oldModificationCount;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        applyFilters();
        final Set<?> s = c instanceof Set ? (Set<?>)c : new HashSet<>(c);
        final int previousModificationCount = modificationCount;
        final V[] d = data;
        final int l = length;
        for (int i = 0; i < l; i++) {
            final V v = d[i];
            if (v == null) continue;
            if (!s.contains(v)) {
                internalRemove(v);
            }
        }
        if ((size + size) < length) compactData();
        return previousModificationCount != modificationCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAll(final Collection<?> c) {
        applyFilters();
        final int previousModificationCount = modificationCount;
        for (final Object o : c) {
            if (o == null) continue;
            internalRemove((V)o);
        }
        if ((size + size) < length) compactData();
        return previousModificationCount != modificationCount;
    }

    @Override
    public void clear() {
        modificationCount = 0;
        productMap.clear();
        for (int i = length - 1; i >= 0; i--) {
            data[i] = null;
        }
        length = 0;
        size = 0;
    }

    @Override
    public int hashCode() {
        applyFilters();
        int h = 0;
        final V[] d = data;
        final int l = length;
        for (int i = 0; i < l; i++) {
            final V v = d[i];
            if (v == null) continue;
            h += v.hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Set)) return false;
        applyFilters();
        final Set<?> s = (Set<?>) o;
        if (s.size() != size) return false;
        try {
            return containsAll(s);
        }
        catch (ClassCastException|NullPointerException unused) {
            return false;
        }
    }

    @Override
    public void forEach(final Consumer<? super V> action) {
        if (action == null) throw new NullPointerException();
        applyFilters();
        final V[] d = data;
        final int l = length;
        for (int i = 0; i < l; i++) {
            final V v = d[i];
            if (v == null) continue;
            action.accept(v);
        }
    }

    @Override
    public boolean removeIf(final Predicate<? super V> filter) {
        if (filter == null) throw new NullPointerException();
        applyFilters();
        final int previousModificationCount = modificationCount;
        final V[] d = data;
        final int l = length;
        for (int i = 0; i < l; i++) {
            final V v = d[i];
            if (v == null) continue;
            if (filter.test(v)) {
                internalRemove(v);
            }
        }
        if ((size + size) < length) compactData();
        return previousModificationCount != modificationCount;
    }

    @Override
    public Spliterator<V> spliterator() {
        return new DataSetSpliterator<>(this);
    }

    // custom methods, for recommendations filtering

    protected void applyFilters() {
        // because filters are being invoked below in this method, it is essential to watch for endless recursion,
        // therefore the first thing we do is create a recursion stop condition and fulfill it in case a recursion does happen
        if (filters.isEmpty()) return;
        @SuppressWarnings("unchecked")
        final StatelessFilter<V>[] filters = this.filters.toArray(new StatelessFilter[this.filters.size()]);
        this.filters.clear(); // make a copy of filters list and clear the original list, thereby stopping any recursion that may follow
        int j;
        final int fc = filters.length;
        final V[] d = data;
        final int l = length;
        final long timeStart = System.nanoTime();
        int removeCount = 0;
        for (j = 0; j < fc; j++) filters[j].onStart(this);
        for (int i = 0; i < l; i++) {
            final V v = d[i];
            if (v == null) continue;
            for (j = 0; j < fc; j++) {
                if (!filters[j].test(v)) {
                    // fast remove
                    productMap.remove(v);
                    d[i] = null;
                    removeCount++;
                    break;
                }
            }
        }
        for (j = 0; j < fc; j++) filters[j].onEnd(this);
        final long timeEnd = System.nanoTime();
        if (removeCount > 0) {
            size -= removeCount;
            modificationCount++;
            if ((size + size) < length) compactData();
        }
        // store loggers of all the filters after they're done
        subLoggers.add(new InfoLogStringBuilder(new StringBuilder(64).append("----- Stateless filtering chain begins\n")));
        for (int i = 0; i < fc; i++) {
            final StatelessFilter<V> f = filters[i];
            subLoggers.add(f);
        }
        subLoggers.add(new InfoLogStringBuilder(new StringBuilder(128).append("----- Stateless filtering chain done, timing: ").append(timeEnd - timeStart).append(" ns\n")));
    }

    @Override
    public DataSet<V> filter(final StatelessFilter<V> filter) {
        if ((filter == null) || (filter == NullStatelessFilter.INSTANCE)) return this; // ignore the null filter
        filters.add(filter);
        return this;
    }

    @Override
    public DataSet<V> filter(final StatefulFilter<V> filter) {
        if (filter == null) return this;
        applyFilters();
        final long timeStart = System.nanoTime();
        final DataSet<V> result = filter.transform(this);
        final long timeEnd = System.nanoTime();
        result.subLoggers.add(filter);
        result.subLoggers.add(new InfoLogStringBuilder(new StringBuilder(128).append("----- Stateful filter ").append(filter.getClass().getSimpleName()).append(" done, timing: ").append(timeEnd - timeStart).append(" ns\n")));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataSet<V> filter(final FilterChain<V> filterChain) {
        DataSet<V> dataSet = this;
        for (final Object f : filterChain) {
            if (f instanceof StatelessFilter) {
                dataSet = dataSet.filter((StatelessFilter<V>)f);
            }
            else if (f instanceof StatefulFilter) {
                dataSet = dataSet.filter((StatefulFilter<V>)f);
            }
            else {
                throw new ProcessingException("Unsupported filter in a filtering chain: " + f.getClass().getCanonicalName());
            }
        }
        return dataSet;
    }

    public DataSet<V> filter(final StatelessFilterChain<V> filterChain) {
        filterChain.forEach((final StatelessFilter<V> filter) -> {
            if ((filter != null) && (filter != NullStatelessFilter.INSTANCE)) {
                filters.add(filter);
            }
        });
        return this;
    }

    public DataSet<V> sort(final Comparator<V> comparator) {
        applyFilters();
        compactData();
        final V[] d = data;
        final int l = length;
        final long timeStart = System.nanoTime();
        Arrays.sort(d, 0, l, comparator);
        for (int i = 0; i < l; i++) {
            d[i].arrayIndex = i;
        }
        final long timeEnd = System.nanoTime();
        subLoggers.add(new InfoLogStringBuilder(new StringBuilder(128).append("----- Sorting done, timing: ").append(timeEnd - timeStart).append(" ns\n")));
        return this;
    }

    public DuplicateResolver<V> getDuplicateResolver() {
        return duplicateResolver;
    }

    @Override
    public void writeLog(final StringBuilder output) {
        subLoggers.forEach((final LoggingSupport l) -> l.writeLog(output));
    }

    public DataSet<V> log(final LoggingSupport log) {
        subLoggers.add(log);
        return this;
    }

    public DataSet<V> log(final String line) {
        subLoggers.add(new InfoLogString(line));
        return this;
    }

    public DataSet<V> log(final StringBuilder content) {
        subLoggers.add(new InfoLogStringBuilder(content));
        return this;
    }

    public void setLimit(final String key, final Integer value) {
        limitTags.put(key, value);
    }

    public Map<String, Integer> getLimitTags() {
        return limitTags;
    }

    /**
     * A utility method that returns the result of blending, to be used as a
     * filter chain terminator.
     *
     * @param blenderName the name of the blender doing the blending
     * @param blenderFeedback the feedback of the blender
     * @return the result of blending, where this dataset is the result dataset, and with the supplied blender name and feedback
     */
    public BlenderResult<V> result(final String blenderName, final Map<String, Object> blenderFeedback) {
        applyFilters();
        return new BlenderResult<>(this, blenderName, blenderFeedback);
    }

    /**
     * A utility method that returns the result of blending with empty blender
     * feedback, to be used as a filter chain terminator.
     *
     * @param blenderName the name of the blender doing the blending
     * @return the result of blending, where this dataset is the result dataset, and with the supplied blender name
     */
    public BlenderResult<V> result(final String blenderName) {
        applyFilters();
        return new BlenderResult<>(this, blenderName);
    }

    // support classes

    public static final class DataSetIterator<V extends DataValue> implements Iterator<V> {
        private final DataSet<V> dataSet;
        private int nextIndex;
        private V current = null;
        private int requiredModificationCount;

        DataSetIterator(final DataSet<V> dataSet) {
            this.dataSet = dataSet;
            final V[] data = dataSet.data;
            final int length = dataSet.length;
            // position the index of the next value at the first non-null value
            int i = 0;
            while ((i < length) && (data[i] == null)) i++;
            nextIndex = i;
            requiredModificationCount = dataSet.modificationCount;
        }

        @Override
        public boolean hasNext() {
            return nextIndex < dataSet.length;
        }

        @Override
        public V next() {
            if (requiredModificationCount != dataSet.modificationCount) throw new ConcurrentModificationException();
            final int length = dataSet.length;
            if (nextIndex >= length) throw new NoSuchElementException("No more data");
            final V[] data = dataSet.data;
            current = data[nextIndex++];
            while ((nextIndex < length) && (data[nextIndex] == null)) nextIndex++;
            return current;
        }

        @Override
        public void remove() {
            if (current == null) throw new IllegalStateException("The next() method has not yet been called, or the current entry has already been removed");
            if (requiredModificationCount != dataSet.modificationCount) throw new ConcurrentModificationException();
            final int beforeIndex = current.arrayIndex;
            if (dataSet.remove(current) && (beforeIndex != current.arrayIndex)) {
                // the indexes changed, recompute next index
                nextIndex = current.arrayIndex + 1;
                final int length = dataSet.length;
                final V[] data = dataSet.data;
                while ((nextIndex < length) && (data[nextIndex] == null)) nextIndex++;
            }
            requiredModificationCount = dataSet.modificationCount;
            current = null;
        }

        @Override
        public void forEachRemaining(final Consumer<? super V> action) {
            if (action == null) throw new NullPointerException();
            if (requiredModificationCount != dataSet.modificationCount) throw new ConcurrentModificationException();
            final int length = dataSet.length;
            final V[] data = dataSet.data;
            for (int i = nextIndex; i < length; i++) {
                final V v = data[i];
                if (v == null) continue;
                try {
                    action.accept(v);
                }
                catch (RuntimeException e) {
                    nextIndex = i + 1;
                    current = v;
                    throw e;
                }
            }
            nextIndex = length;
            current = null;
            if (requiredModificationCount != dataSet.modificationCount) throw new ConcurrentModificationException();
        }
    }

    /**
     * Spliterator for DataSet. It binds late: it is bound to its DataSet when
     * a method is invoked that requires some information about the DataSet, i.e.
     * the traversing and sizing methods. Any successive modification of the DataSet
     * will result in a ConcurrentModificationException of this Spliterator
     * upon invoking tryAdvance() or forEachRemaining().
     * @param <V> the type of DataValue, inherited from DataSet
     */
    public static final class DataSetSpliterator<V extends DataValue> implements Spliterator<V> {
        private static final int CHARACTERISTICS = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED;
        private final DataSet<V> dataSet;
        private int currentIndex;
        private int requiredModificationCount;
        private int fence;

        DataSetSpliterator(final DataSet<V> dataSet) {
            if (dataSet.size != dataSet.length) throw new IllegalStateException("Spliterator created on an un-compacted DataSet; invoke compactData() before spliterator()");
            this.dataSet = dataSet;
            requiredModificationCount = 0;
            fence = -1; // support late binding
            currentIndex = 0;
        }

        private DataSetSpliterator(final DataSet<V> dataSet, final int startingIndex, final int fence) {
            this.dataSet = dataSet;
            requiredModificationCount = dataSet.modificationCount;
            this.currentIndex = startingIndex;
            this.fence = fence;
        }

        private int getFence() {
            int result;
            if ((result = fence) < 0) {
                // this is late binding: first make the DataSet reflect the latest and compact data, then initialize our accounting variables
                dataSet.applyFilters();
                if (dataSet.size != dataSet.length) dataSet.compactData();
                requiredModificationCount = dataSet.modificationCount;
                result = fence = dataSet.length;
            }
            return result;
        }

        @Override
        public boolean tryAdvance(final Consumer<? super V> action) {
            if (action == null) throw new NullPointerException("action is null");
            if (currentIndex >= getFence()) return false;
            if (requiredModificationCount != dataSet.modificationCount) throw new ConcurrentModificationException();
            action.accept(dataSet.data[currentIndex++]);
            return true;
        }

        @Override
        public void forEachRemaining(final Consumer<? super V> action) {
            final int f = getFence();
            if (requiredModificationCount != dataSet.modificationCount) throw new ConcurrentModificationException();
            final V[] d = dataSet.data;
            for (int i = currentIndex; i < f; i++) {
                try {
                    action.accept(d[i]);
                }
                catch (RuntimeException e) {
                    currentIndex = i;
                    throw e;
                }
            }
            currentIndex = f;
            if ((requiredModificationCount != dataSet.modificationCount) || (f != fence)) throw new ConcurrentModificationException();
        }

        @Override
        public Spliterator<V> trySplit() {
            final int startingIndex = currentIndex;
            final int middleIndex = (startingIndex + getFence()) >>> 1;
            if (middleIndex <= startingIndex) return null;
            currentIndex = middleIndex;
            return new DataSetSpliterator<>(dataSet, startingIndex, middleIndex);
        }

        @Override
        public long estimateSize() {
            return getFence() - currentIndex;
        }

        @Override
        public int characteristics() {
            return CHARACTERISTICS;
        }
    }

    /**
     * A utility to build a DataSet faster than using DataSet's own
     * add() method.
     * @param <V> the type of items in the DataSet
     */
    public static final class Builder<V extends DataValue> {
        protected final DuplicateResolver<V> duplicateResolver;
        protected final Map<V, V> productMap = new HashMap<>();
        protected final DataSet<V> parentDataSet;
        protected V[] data = (V[]) new DataValue[ARRAY_INCREMENT];
        protected int size = 0;

        public Builder(final DuplicateResolver<V> duplicateResolver, final DataSet<V> parentDataSet) {
            this.duplicateResolver = duplicateResolver;
            this.parentDataSet = parentDataSet;
        }

        public void add(final V value) {
            if (value == null) throw new NullPointerException();
            final V existing = productMap.put(value, value);
            if (existing == null) {
                if (size >= data.length) {
                    data = Arrays.copyOf(data, size + ARRAY_INCREMENT);
                }
                data[size] = value;
                value.arrayIndex = size;
                size++;
            }
            else if (value != existing) {
                final V correct = duplicateResolver.resolve(value, existing);
                if (correct == value) {
                    // retain the new value (a replacement has occurred)
                    data[value.arrayIndex = existing.arrayIndex] = value;
                }
                else {
                    // retain the old value (no replacement occurred)
                    productMap.put(correct, correct);
                }
            }
        }

        public DataSet<V> build() {
            return new DataSet<V>(duplicateResolver, data, productMap, parentDataSet);
        }

        public int size() {
            return productMap.size();
        }
    }

    static final class InfoLogStringBuilder implements LoggingSupport {
        private final StringBuilder log;
        InfoLogStringBuilder(final StringBuilder log) {
            this.log = log;
        }
        @Override
        public void writeLog(final StringBuilder output) {
            output.append(log);
        }
    }

    static final class InfoLogString implements LoggingSupport {
        private final String log;
        InfoLogString(final String log) {
            this.log = log;
        }
        @Override
        public void writeLog(final StringBuilder output) {
            output.append(log);
        }
    }
}
