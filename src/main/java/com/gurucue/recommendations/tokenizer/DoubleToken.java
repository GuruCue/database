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
import com.gurucue.recommendations.ResponseStatus;

public final class DoubleToken implements PrimitiveToken {
    private final String name;
    private final Double value;
    
    DoubleToken(final String name, final Double value) {
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
        final long l = value.longValue();
        if ((value.doubleValue() - ((double)l)) != 0) throw new ResponseException(ResponseStatus.ILLEGAL_INTEGER, "Cannot convert to integer: " + value);
        return Long.valueOf(l);
    }

    @Override
    public Double asDouble() throws ResponseException {
        return value;
    }

    @Override
    public Boolean asBoolean() throws ResponseException {
        return value.doubleValue() == 0.0 ? Boolean.FALSE : Boolean.TRUE;
    }
}
