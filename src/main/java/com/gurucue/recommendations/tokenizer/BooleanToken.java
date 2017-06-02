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

public final class BooleanToken implements PrimitiveToken {
    private static final String TRUE_STRING = "true";
    private static final String FALSE_STRING = "false";
    private static final Long TRUE_LONG = Long.valueOf(1L);
    private static final Long FALSE_LONG = Long.valueOf(0L);
    private static final Double TRUE_DOUBLE = Double.valueOf(1.0);
    private static final Double FALSE_DOUBLE = Double.valueOf(0.0);
    
    private final String name;
    private final Boolean value;
    
    BooleanToken(final String name, final Boolean value) {
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
        return value.booleanValue() ? TRUE_STRING : FALSE_STRING;
    }

    @Override
    public Long asLong() throws ResponseException {
        return value.booleanValue() ? TRUE_LONG : FALSE_LONG;
    }

    @Override
    public Double asDouble() throws ResponseException {
        return value.booleanValue() ? TRUE_DOUBLE : FALSE_DOUBLE;
    }

    @Override
    public Boolean asBoolean() throws ResponseException {
        return value;
    }
}
