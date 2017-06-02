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

import com.gurucue.recommendations.ResponseException;

/**
 * The top-level singleton instance that is invoked for each blending job.
 * It could perform the job itself, but is usually used as a switch
 * between various "ordinary" blenders. The switching decision is arbitrary,
 * subject to the blender domain, or other parameters.
 */
public interface TopBlender {
    <V extends DataValue> BlenderResult<V> blend(
            Class<V> dataClass,
            BlendEnvironment environment,
            BlendParameters parameters
    ) throws ResponseException;
}
