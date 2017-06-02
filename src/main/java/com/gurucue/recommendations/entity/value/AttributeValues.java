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
import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.data.DataProvider;
import com.gurucue.recommendations.entity.Attribute;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An immutable collection of attribute values.
 */
public final class AttributeValues implements Iterable<Map.Entry<Attribute, Value>> {
    public static final AttributeValues NO_VALUES = new AttributeValues(ImmutableMap.<Attribute, Value>of());
    private static final boolean[] EMPTY_BOOLEANS = new boolean[0];
    private static final long[] EMPTY_INTEGERS = new long[0];
    private static final double[] EMPTY_FLOATS = new double[0];
    private static final String[] EMPTY_STRINGS = new String[0];
    private static final TimestampIntervalValue[] EMPTY_TIMESTAMP_INTERVALS = new TimestampIntervalValue[0];
    private static final TranslatableValue[] EMPTY_TRANSLATABLE_VALUES = new TranslatableValue[0];

    public final ImmutableMap<Attribute, Value> values;

    public AttributeValues(final String jsonValues, final DataProvider provider, final Appendable log) throws ResponseException {
        values = Value.parse(jsonValues, provider, log == null ? NullAppendable.INSTANCE : log);
    }

    public AttributeValues(final ImmutableMap<Attribute, Value> values) {
        this.values = values;
    }

    public AttributeValues(final Map<Attribute, Value> values) {
        this(ImmutableMap.copyOf(values));
    }

    @Override
    public Iterator<Map.Entry<Attribute, Value>> iterator() {
        return new AttributeValuesIterator(values.entrySet().iterator());
    }

    /**
     * Creates a new collection of attribute values by first taking the
     * existing collection for the basis, removing from it the values in
     * <code>valuesToSet</code>, and adding the values in
     * <code>valuesToRemove</code>. Returns the new instance with the new
     * collection of attribute values.
     *
     * @param valuesToSet the values to set
     * @param valuesToRemove the values to remove
     * @return the resulting attribute-values instance with the specified modifications applied
     */
    public AttributeValues modify(final Map<Attribute, Value> valuesToSet, final Map<Attribute, Value> valuesToRemove) {
        final Map<Attribute, Value> newValues = new HashMap<>(values);
        // first remove any values
        if ((valuesToRemove != null) && !valuesToRemove.isEmpty()) {
            for (final Map.Entry<Attribute, Value> entry : valuesToRemove.entrySet()) {
                final Attribute attribute = entry.getKey();
                final Value valueToRemove = entry.getValue();
                if ((valueToRemove == null) || (valueToRemove.equals(null))) newValues.remove(attribute);
                else {
                    final Value existingValue = newValues.get(attribute);
                    if (existingValue != null && !existingValue.equals(null)) {
                        if (existingValue.isArray) {
                            final MultiValue newValue = ((MultiValue)existingValue).remove(valueToRemove);
                            if ((newValue == null) || (newValue.equals(null))) newValues.remove(attribute);
                            else newValues.put(attribute, newValue);
                        }
                        else if (existingValue.matches(valueToRemove)) {
                            newValues.remove(attribute);
                        }
                    }
                }
            }
        }
        // now set any values
        if ((valuesToSet != null) && !valuesToSet.isEmpty()) {
            for (final Map.Entry<Attribute, Value> entry : valuesToSet.entrySet()) {
                final Attribute attribute = entry.getKey();
                Value newValue = entry.getValue();
                final Value existingValue = newValues.get(attribute);
/*                if ((existingValue != null) && existingValue.isArray) {
                    newValue = ((MultiValue)existingValue).set(newValue); // additional value
                }
                newValues.put(attribute, newValue);*/ // either: add a nev value, replace an existing value, or set an additional value to a multi-value
                if (existingValue == null) newValues.put(attribute, newValue);
                else if (existingValue.isArray) newValues.put(attribute, ((MultiValue)existingValue).set(newValue)); // additional value
                else newValues.put(attribute, existingValue.replace(newValue)); // use existingValue.replace() to correctly handle translations
            }
        }
        return new AttributeValues(ImmutableMap.copyOf(newValues));
    }

    /**
     * Outputs all attribute values as JSON, suitable for storing it to a database.
     * {@link com.gurucue.recommendations.entity.value.Value#parse(String, com.gurucue.recommendations.data.DataProvider, Appendable)}
     * is guaranteed to parse the output of this method, which is then used as the
     * constructor parameter for {@link com.gurucue.recommendations.entity.value.AttributeValues#AttributeValues(com.google.common.collect.ImmutableMap)}.
     *
     * @param output where to output the JSON representation to
     */
    public void toJson(final StringBuilder output) {
        output.append("{");
        final Iterator<Map.Entry<Attribute, Value>> iterator = values.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Attribute, Value> entry = iterator.next();
            output.append("\"");
            Value.escapeJson(entry.getKey().getIdentifier(), output);
            output.append("\":");
            entry.getValue().toJson(output);
            while (iterator.hasNext()) {
                entry = iterator.next();
                output.append(",\"");
                Value.escapeJson(entry.getKey().getIdentifier(), output);
                output.append("\":");
                entry.getValue().toJson(output);
            }
        }
        output.append("}");
    }

    public void toPrettyJson(final StringBuilder output, final String indent, final String indentStep) {
        output.append("{\n");
        final Iterator<Map.Entry<Attribute, Value>> iterator = values.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Attribute, Value> entry = iterator.next();
            output.append(indent).append(indentStep).append("\"");
            Value.escapeJson(entry.getKey().getIdentifier(), output);
            output.append("\": ");
            entry.getValue().toJson(output);
            while (iterator.hasNext()) {
                entry = iterator.next();
                output.append(",\n").append(indent).append(indentStep).append("\"");
                Value.escapeJson(entry.getKey().getIdentifier(), output);
                output.append("\": ");
                entry.getValue().toJson(output);
            }
        }
        output.append("\n").append(indent).append("}");
    }

    /**
     * Returns all attribute values as JSON, using {@link #toJson(StringBuilder)}.
     *
     * @return the JSON representation of the attribute values
     * @see #toJson(StringBuilder)
     */
    public String toJson() {
        final StringBuilder output = new StringBuilder(5 + (values.size() * 50)); // some guesswork for the initial capacity
        toJson(output);
        return output.toString();
    }

    public Value get(final Attribute attribute) {
        return values.get(attribute);
    }

    public boolean getAsBoolean(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? false : v.asBoolean();
    }

    public boolean[] getAsBooleans(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? EMPTY_BOOLEANS : v.asBooleans();
    }

    public long getAsInteger(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? 0L : v.asInteger();
    }

    public long[] getAsIntegers(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? EMPTY_INTEGERS : v.asIntegers();
    }

    public double getAsFloat(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? 0.0 : v.asFloat();
    }

    public double[] getAsFloats(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? EMPTY_FLOATS : v.asFloats();
    }

    public String getAsString(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? null : v.asString();
    }

    public String[] getAsStrings(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? EMPTY_STRINGS : v.asStrings();
    }

    public TimestampIntervalValue getAsTimestampInterval(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? null : v.asTimestampInterval();
    }

    public TimestampIntervalValue[] getAsTimestampIntervals(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? EMPTY_TIMESTAMP_INTERVALS : v.asTimestampIntervals();
    }

    public TranslatableValue getAsTranslatable(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? null : v.asTranslatable();
    }

    public TranslatableValue[] getAsTranslatables(final Attribute attribute) {
        final Value v = values.get(attribute);
        return v == null ? EMPTY_TRANSLATABLE_VALUES : v.asTranslatables();
    }

    public boolean contains(final Attribute attribute) {
        return values.containsKey(attribute);
    }

    public static AttributeValues fromJson(final String jsonAttributes, final DataProvider provider, final Appendable log) throws ResponseException {
        return ((jsonAttributes == null) || (jsonAttributes.length() == 0)) ? AttributeValues.NO_VALUES : new AttributeValues(jsonAttributes, provider, log);
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int r = 17;
        // this is an unordered list, so first order it the way that the result will always be the same no matter how the values are laid out
        final int[] hashes = new int[values.size()];
        int i = 0;
        for (final Map.Entry<Attribute, Value> entry : values.entrySet()) {
            // ImmutableMap does not permit null values or keys
            hashes[i++] = entry.getKey().getId().hashCode() * 31 + entry.getValue().hashCode();
        }
        Arrays.sort(hashes);
        for (i = hashes.length - 1; i >= 0; i--) {
            r = 31 * r + hashes[i];
        }
        return r;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other instanceof AttributeValues) {
            final ImmutableMap<Attribute, Value> otherValues = ((AttributeValues)other).values;
            // comparing unordered lists == sets of values
            if (values.size() != otherValues.size()) return false;
            for (final Map.Entry<Attribute, Value> entry : values.entrySet()) {
                // ImmutableMap does not permit null values or keys
                final Value v = otherValues.get(entry.getKey());
                if ((v == null) || !v.equals(entry.getValue())) return false;
            }
            return true; // everything matched somewhere
        }
        return false;
    }

    private static final class NullAppendable implements Appendable {
        static final NullAppendable INSTANCE = new NullAppendable();

        @Override
        public Appendable append(final CharSequence csq) throws IOException {
            return this;
        }

        @Override
        public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
            return this;
        }

        @Override
        public Appendable append(final char c) throws IOException {
            return this;
        }
    }
}
