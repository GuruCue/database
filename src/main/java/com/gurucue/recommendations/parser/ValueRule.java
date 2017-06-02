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
package com.gurucue.recommendations.parser;

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.ResponseStatus;
import com.gurucue.recommendations.tokenizer.PrimitiveToken;
import com.gurucue.recommendations.tokenizer.Token;

import java.util.Map;

public final class ValueRule extends Rule {
    private final PrimitiveTokenParser parser;

    public ValueRule(final String name, final boolean optional, final PrimitiveTokenParser parser) {
        super(name, optional);
        this.parser = parser;
    }

    @Override
    public Object parse(final Token token, final Map<String, Object> params) throws ResponseException {
        // name is verified by the caller
        // caller ensures token.isNull() == false
        if (!(token instanceof PrimitiveToken)) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: primitive value represented as a structured value");
        return parser.parse((PrimitiveToken)token);
    }
}
