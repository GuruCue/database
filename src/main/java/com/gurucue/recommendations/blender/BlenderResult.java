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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Blending result. Contains the resulting dataset, the name of the blender that
 * performed the blending, and any feedback it emitted.
 */
public final class BlenderResult<V extends DataValue> {
    /** The dataset output by the blender. */
    public final DataSet<V> dataSet;
    /** The name of the blender that produced the result. */
    public final String blenderName;
    /** Any feedback emitted by the blender. */
    public final Map<String, Object> blenderFeedback;

    /**
     * Constructs the blender result.
     *
     * @param dataSet dataset output by the blender
     * @param blenderName the name of the blender creating the result
     * @param blenderFeedback any feedback emitted by the blender
     */
    public BlenderResult(final DataSet<V> dataSet, final String blenderName, final Map<String, Object> blenderFeedback) {
        this.dataSet = dataSet;
        this.blenderName = blenderName;
        this.blenderFeedback = blenderFeedback == null ? Collections.emptyMap() : blenderFeedback;
    }

    /**
     * Constructs the blender result with empty blender feedback
     * @param dataSet dataset output by the blender
     * @param blenderName the name of the blender creating the result
     */
    public BlenderResult(final DataSet<V> dataSet, final String blenderName) {
        this.dataSet = dataSet;
        this.blenderName = blenderName;
        this.blenderFeedback = new HashMap<>();
    }

    /**
     * Adds the specified feedback and returns itself, so chaining is
     * possible.
     *
     * @param key the feedback key (name)
     * @param value the feeback value
     * @return the BlenderResult instance itself, to support chaining
     */
    public BlenderResult<V> feedback(final String key, final Object value) {
        blenderFeedback.put(key, value);
        return this;
    }
}
