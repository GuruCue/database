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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.ResponseStatus;
import com.gurucue.recommendations.tokenizer.Token;
import com.gurucue.recommendations.tokenizer.Tokenizer;

public abstract class Rule {
    public static final Map<String, Rule> EMPTY_MEMBERS = Collections.<String, Rule>emptyMap();
    
    public final String name;
    public final boolean optional;
    
    public Rule(final String name, final boolean optional) {
        if (null == name) throw new NullPointerException("name cannot be null");
        this.name = name;
        this.optional = optional;
    }
    
    public abstract Object parse(final Token token, final Map<String, Object> params) throws ResponseException;
    
    public static ValueRule value(final String name, final boolean optional, final PrimitiveTokenParser parser) {
        return new ValueRule(name, optional, parser);
    }

    public static <T> ListRule<T> list(final String name, final boolean optional, final Class<T> listType, final Rule member) {
        return new ListRule<T>(name, optional, listType, member);
    }

    public static MapRule map(final String name, final boolean optional, final StructuredTokenParserMaker resultMaker, final Rule[] members) {
        return new MapRule(name, optional, resultMaker, members);
    }
    
    public static Object parse(final String format, final String input, final Rule rule, final Map<String, Object> params) throws ResponseException {
        final Tokenizer tokenizer = Tokenizer.tokenize(format, input);
        final Object result = rule.parse(tokenizer.nextToken(), params == null ? new HashMap<String, Object>() : params);
        if (null == result) throw new ResponseException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "Parse result is null");
        if (!tokenizer.eof()) throw new ResponseException(ResponseStatus.DOCUMENT_PROCESSING_ERROR, "Parse error: stray text after the main entity at line " + tokenizer.getLineNumber() + ", column " + tokenizer.getColumnNumber());
        return result;
    }
}
