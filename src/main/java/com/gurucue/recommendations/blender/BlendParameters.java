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

import com.google.common.collect.ImmutableMap;
import com.gurucue.recommendations.entity.DataType;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains blending parameters, usually transformed from an API request:
 * blender name, requested result size, attributes to consider, referenced
 * products, arbitrary data, ...
 */
public final class BlendParameters {
    public final String blenderName;
    public final String consumerUsername;
    public final ImmutableMap<DataType, String> data;
    public final Map<String, Object> input; // input parameters to the blender, not every one is generally always or ever needed, so no special fields area allotted and we use a Map instead

    /**
     * Constructs the parameters for a blender invocation.
     *
     * @param blenderName the name of the blender at the source, may be different from the actual name of the blender used
     * @param consumerUsername the username of the consumer on whose behalf this is being processed, usually it's handier to use the consumer entity from {@link BlendEnvironment}
     * @param data arbitrary data supplied with the blending request, usually not interesting for the blending process
     * @param input input parameters expected by the given blender; each blender can have different optional and required parameters
     */
    public BlendParameters(
            final String blenderName,
            final String consumerUsername,
            final Map<DataType, String> data,
            final Map<String, Object> input
    ) {
        this.blenderName = blenderName;
        this.consumerUsername = consumerUsername;
        this.data = data instanceof ImmutableMap ? (ImmutableMap<DataType, String>)data : ImmutableMap.copyOf(data);
        this.input = input;
    }

    /**
     * Constructs the parameters for a blender invocation, with empty inputs.
     * Use the {@link #addInput(String, Object)} method to add input parameters
     * for the blender.
     *
     * @param blenderName the name of the blender at the source, may be different from the actual name of the blender used
     * @param consumerUsername the username of the consumer on whose behalf this is being processed, usually it's handier to use the consumer entity from {@link BlendEnvironment}
     * @param data arbitrary data supplied with the blending request, usually not interesting for the blending process
     */
    public BlendParameters(
            final String blenderName,
            final String consumerUsername,
            final Map<DataType, String> data
    ) {
        this(blenderName, consumerUsername, data, new HashMap<>());
    }

    /**
     * A convenience method to set up inputs and enables chaining.
     *
     * @param inputName the name of the input parameter
     * @param value the value of the input parameter
     * @return the instance itself, so chaining is possible
     */
    public BlendParameters addInput(final String inputName, final Object value) {
        input.put(inputName, value);
        return this;
    }
}
