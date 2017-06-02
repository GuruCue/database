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

import com.gurucue.recommendations.ProcessingException;
import com.gurucue.recommendations.ResponseStatus;
import com.gurucue.recommendations.entity.value.Value;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class JsonObject extends LinkedHashMap<String, Json> implements Json, Externalizable {
    private static final long serialVersionUID = -1639389547777568246L;

    public JsonObject() {
        super();
    }

    public JsonObject(final LinkedHashMap<String, Json> mapping) {
        super(mapping);
    }

    @Override
    public void serialize(final StringBuilder output) {
        output.append('{');
        final Iterator<Map.Entry<String, Json>> it = entrySet().iterator();
        if (it.hasNext()) {
            final Map.Entry<String, Json> firstEntry = it.next();
            output.append('"');
            Value.escapeJson(firstEntry.getKey(), output);
            output.append("\":");
            firstEntry.getValue().serialize(output);
            while (it.hasNext()) {
                final Map.Entry<String, Json> entry = it.next();
                output.append(",\"");
                Value.escapeJson(entry.getKey(), output);
                output.append("\":");
                entry.getValue().serialize(output);
            }
        }
        output.append('}');
    }

    public String serialize() {
        final StringBuilder sb = new StringBuilder(size() * 64); // guesstimate
        serialize(sb);
        return sb.toString();
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public JsonType getType() {
        return JsonType.OBJECT;
    }

    public String getString(final String key) {
        final Json json = get(key);
        if ((json == null) || json.isNull()) return null;
        if (json.getType() == JsonType.PRIMITIVE) {
            return ((JsonPrimitive) json).asString();
        }
        throw new ProcessingException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "The value at key \"" + key + "\" is not a JSON primitive value");
    }

    public Long getLong(final String key) {
        final Json json = get(key);
        if ((json == null) || json.isNull()) return null;
        if (json.getType() == JsonType.PRIMITIVE) {
            return ((JsonPrimitive) json).asLong();
        }
        throw new ProcessingException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "The value at key \"" + key + "\" is not a JSON primitive value");
    }

    public Boolean getBoolean(final String key) {
        final Json json = get(key);
        if ((json == null) || json.isNull()) return null;
        if (json.getType() == JsonType.PRIMITIVE) {
            return ((JsonPrimitive) json).asBoolean();
        }
        throw new ProcessingException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "The value at key \"" + key + "\" is not a JSON primitive value");
    }

    public Double getDouble(final String key) {
        final Json json = get(key);
        if ((json == null) || json.isNull()) return null;
        if (json.getType() == JsonType.PRIMITIVE) {
            return ((JsonPrimitive) json).asDouble();
        }
        throw new ProcessingException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "The value at key \"" + key + "\" is not a JSON primitive value");
    }

    public JsonPrimitive getJsonPrimitive(final String key) {
        final Json json = get(key);
        if (json == null) return JsonNull.INSTANCE;
        if (json.getType() == JsonType.PRIMITIVE) {
            return (JsonPrimitive) json;
        }
        throw new ProcessingException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "The value at key \"" + key + "\" is not a JSON primitive value");
    }

    public JsonObject getJsonObject(final String key) {
        final Json json = get(key);
        if ((json == null) || json.isNull()) return null;
        if (json.getType() == JsonType.OBJECT) {
            return (JsonObject) json;
        }
        throw new ProcessingException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "The value at key \"" + key + "\" is not a JSON object");
    }

    public JsonArray getJsonArray(final String key) {
        final Json json = get(key);
        if ((json == null) || json.isNull()) return null;
        if (json.getType() == JsonType.ARRAY) {
            return (JsonArray) json;
        }
        throw new ProcessingException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "The value at key \"" + key + "\" is not a JSON array");
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof JsonObject) {
            final JsonObject that = (JsonObject)obj;
            if (size() != that.size()) return false;
            for (final Map.Entry<String, Json> entry : entrySet()) {
                final Json thisJson = entry.getValue();
                final Json thatJson = that.get(entry.getKey());
                if (thisJson == null) {
                    if ((thatJson != null) && !thatJson.equals(thisJson)) return false;
                }
                else {
                    if (!thisJson.equals(thatJson)) return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        final List<String> keys = new ArrayList<>(keySet());
        Collections.sort(keys);
        for (final String key : keys) {
            result = (result * 31) + key.hashCode();
            final Json json = get(key);
            if (json != null) result = (result * 31) + json.hashCode();
        }
        return result;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        // make a snapshot, so we're not bothered with accesses from other threads while serializing
        final List<Map.Entry<String, Json>> content = new LinkedList<>(entrySet());
        out.writeInt(content.size());
        for (final Map.Entry<String, Json> entry : content) {
            final Json value = entry.getValue();
            if (value == null) continue; // skip nulls, it's as if the key were not there in the first place
            out.writeUTF(entry.getKey());
            out.writeObject(value);
        }
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final int n = in.readInt();
        for (int i = n; i > 0; i--) {
            final String key = in.readUTF();
            final Object value = in.readObject();
            // ignore non-Json values
            if (value instanceof Json) put(key, (Json)value); // TODO: else log an error
        }
    }
}
