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

public final class StringToken implements PrimitiveToken {
    private final static String TRUE = "true";
    private final static String FALSE = "false";
    private final static String YES = "yes";
    private final static String NO = "no";
    
    private final String name;
    private final String value;
    
    StringToken(final String name, final String value) {
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
        return value;
    }
    
    @Override
    public Long asLong() throws ResponseException {
        try {
            return Long.valueOf(value, 10);
        }
        catch (NumberFormatException e) {
            throw new ResponseException(ResponseStatus.ILLEGAL_INTEGER, e, "Cannot convert to an integer: " + value);
        }
    }
    
    @Override
    public Double asDouble() throws ResponseException {
        try {
            return Double.valueOf(value);
        }
        catch (NumberFormatException e) {
            throw new ResponseException(ResponseStatus.ILLEGAL_DOUBLE, e, "Cannot convert to a decimal number: " + value);
        }
    }
    
    @Override
    public Boolean asBoolean() throws ResponseException {
        if (null == value) return null;
        if (TRUE.equalsIgnoreCase(value)) return Boolean.TRUE;
        if (FALSE.equalsIgnoreCase(value)) return Boolean.FALSE;
        if (YES.equalsIgnoreCase(value)) return Boolean.TRUE;
        if (NO.equalsIgnoreCase(value)) return Boolean.FALSE;
        try {
            return Double.parseDouble(value) != 0.0;
        }
        catch (NumberFormatException e) {
            throw new ResponseException(ResponseStatus.ILLEGAL_BOOLEAN, e, "Cannot convert to boolean: " + value);
        }
    }
}
