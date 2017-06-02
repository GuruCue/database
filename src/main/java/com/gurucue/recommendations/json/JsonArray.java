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
package com.gurucue.recommendations.json;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class JsonArray extends ArrayList<Json> implements Json, Externalizable {
    private static final long serialVersionUID = -8661874222089316962L;

    public JsonArray() {
        super();
    }

    public JsonArray(final List<Json> list) {
        super(list);
    }

    public JsonArray(final int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public void serialize(final StringBuilder output) {
        output.append('[');
        final Iterator<Json> it = iterator();
        if (it.hasNext()) {
            it.next().serialize(output);
            while (it.hasNext()) {
                output.append(",");
                it.next().serialize(output);
            }
        }
        output.append(']');
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public JsonType getType() {
        return JsonType.ARRAY;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof JsonArray) {
            final JsonArray that = (JsonArray)obj;
            if (size() != that.size()) return false;
            final Iterator<Json> thisIterator = iterator();
            final Iterator<Json> thatIterator = that.iterator();
            while (thisIterator.hasNext()) {
                if (!thatIterator.hasNext()) return false;
                final Json thisJson = thisIterator.next();
                final Json thatJson = thatIterator.next();
                if (thisJson == null) {
                    if ((thatJson != null) && !thatJson.equals(thisJson)) return false;
                }
                else {
                    if (!thisJson.equals(thatJson)) return false;
                }
            }
            if (thatIterator.hasNext()) return false;
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        for (final Json json : this) {
            if (json != null) result = (result * 31) + json.hashCode();
        }
        return result;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        // make a snapshot, so we're not bothered with accesses from other threads while serializing
        final List<Json> content = new LinkedList<>(this);
        out.writeInt(content.size());
        for (final Json value : content) {
            out.writeObject(value);
        }
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final int n = in.readInt();
        ensureCapacity(n);
        for (int i = n; i > 0; i--) {
            final Object value = in.readObject();
            // ignore non-Json values
            if (value instanceof Json) add((Json)value);
            else add(JsonNull.INSTANCE); // retain the same indexing, TODO: log an error
        }
    }
}
