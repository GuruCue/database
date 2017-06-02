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
package com.gurucue.recommendations.recommender;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents one recommendation item. Output from a recommender is a set
 * of recommendation items.
 */
public final class Recommendation implements Externalizable {
    private static final long serialVersionUID = -580859189570018789L;
    public long productId;
    public Map<String, String> tags;
    public double prediction;
    public String explanation;
    public Map<String, Float> prettyExplanations; // maps an explanation to its weight

    public Recommendation() {}

    public Recommendation(final long productId, final Map<String, String> tags, final double prediction, final String explanation, final Map<String, Float> prettyExplanations) {
        this.productId = productId;
        this.tags = tags;
        this.prediction = prediction;
        this.explanation = explanation;
        this.prettyExplanations = prettyExplanations;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(productId);
        out.writeDouble(prediction);
        if ((tags == null) || tags.isEmpty()) out.writeInt(0);
        else {
            out.writeInt(tags.size());
            for (final Map.Entry<String, String> entry : tags.entrySet()) {
                out.writeUTF(entry.getKey() == null ? "" : entry.getKey());
                out.writeUTF(entry.getValue() == null ? "" : entry.getValue());
            }
        }
        if (explanation == null) out.writeBoolean(false);
        else {
            out.writeBoolean(true);
            out.writeUTF(explanation);
        }
        if ((prettyExplanations == null) || prettyExplanations.isEmpty()) out.writeInt(0);
        else {
            out.writeInt(prettyExplanations.size());
            for (final Map.Entry<String, Float> entry : prettyExplanations.entrySet()) {
                out.writeUTF(entry.getKey() == null ? "" : entry.getKey());
                out.writeFloat(entry.getValue() == null ? 0f : entry.getValue().floatValue());
            }
        }
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        productId = in.readLong();
        prediction = in.readDouble();
        final int nTags = in.readInt();
        tags = new HashMap<>(nTags);
        for (int i = nTags; i > 0; i--) {
            final String key = in.readUTF();
            final String value = in.readUTF();
            tags.put(key, value);
        }
        if (in.readBoolean()) explanation = in.readUTF();
        else explanation = null;
        final int nExplanations = in.readInt();
        prettyExplanations = new HashMap<>(nExplanations);
        for (int i = nExplanations; i > 0; i--) {
            final String explanation = in.readUTF();
            final float weight = in.readFloat();
            prettyExplanations.put(explanation, weight);
        }
    }
}
