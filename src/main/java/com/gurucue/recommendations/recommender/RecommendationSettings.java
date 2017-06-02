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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to specify properties in getRecommendation call. 
 * 1. maxResults specify the number of recommended products.
 * 2. attributeValues are used to filter products.
 * 3. randomizeResults indicate whether recommended products should be randomized (or ordered by recommendation value)
 * 4. tags are specified by users (correspond to tags from Web API getRecommendations call)
 */
public final class RecommendationSettings implements Externalizable {
    private static final long serialVersionUID = 2334011148726544670L;
    public int maxResults;
    public boolean randomizeResults;
    public Map<String,String> tags;

    public RecommendationSettings() {}

    public RecommendationSettings(
            final int maxResults,
            final Object[] attributeValues,
            final boolean randomizeResults,
            final Map <String,String> tags
    )
    {
        this.maxResults = maxResults;
        this.randomizeResults = randomizeResults;
        this.tags = tags;
    }

    public RecommendationSettings(
            final int maxResults,
            final boolean randomizeResults,
            final Map <String,String> tags
    )
    {
        this(maxResults, null, randomizeResults, tags);
    }

    public RecommendationSettings(
            final int maxResults,
            final boolean randomizeResults
    )
    {
        this(maxResults, null, randomizeResults, Collections.emptyMap());
    }

    public int getMaxResults() {
        return maxResults;
    }

    public boolean isRandomizeResults() {
        return randomizeResults;
    }

    public Map<String,String> getTags() {
        return tags;
    }

    public void setTags(final Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(maxResults);
        out.writeBoolean(randomizeResults);
        if ((tags == null) || (tags.size() == 0)) out.writeInt(0);
        else {
            out.writeInt(tags.size());
            for (final Map.Entry<String, String> entry : tags.entrySet()) {
                out.writeUTF(entry.getKey() == null ? "" : entry.getKey());
                out.writeUTF(entry.getValue() == null ? "" : entry.getValue());
            }
        }
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        maxResults = in.readInt();
        randomizeResults = in.readBoolean();
        final int n = in.readInt();
        tags = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            final String key = in.readUTF();
            final String value = in.readUTF();
            tags.put(key, value);
        }
    }
}
