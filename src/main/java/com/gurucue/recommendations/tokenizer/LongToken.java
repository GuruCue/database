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
package com.gurucue.recommendations.tokenizer;

import com.gurucue.recommendations.ResponseException;

public final class LongToken implements PrimitiveToken {
    private final String name;
    private final Long value;
    
    LongToken(final String name, final Long value) {
        assert(null != value);
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public String asString() throws ResponseException {
        return value.toString();
    }

    @Override
    public Long asLong() throws ResponseException {
        return value;
    }

    @Override
    public Double asDouble() throws ResponseException {
        return Double.valueOf(value.doubleValue());
    }

    @Override
    public Boolean asBoolean() throws ResponseException {
        return value.longValue() == 0L ? Boolean.FALSE : Boolean.TRUE;
    }
}
