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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.ResponseStatus;
import com.gurucue.recommendations.tokenizer.ListToken;
import com.gurucue.recommendations.tokenizer.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ListRule<T> extends Rule {
    private static final Logger log = LogManager.getLogger(ListRule.class);
    public final Rule member;
    private final Class<T> listType;

    public ListRule(final String name, final boolean optional, final Class<T> listType, final Rule member) {
        super(name, optional);
        this.listType = listType;
        this.member = member;
    }

    @Override
    public final Object parse(final Token token, final Map<String, Object> params) throws ResponseException {
        // name is verified by the caller
        // caller ensures token.isNull() == false
        final long timeBegin = System.nanoTime();
        StringBuilder logBuilder = null;

        if (!(token instanceof ListToken)) throw new ResponseException(ResponseStatus.MALFORMED_REQUEST, "Parse error: given token does not represent a list");
        final ListToken listToken = (ListToken)token;
        final List<T> list = new ArrayList<T>();
        
        
        // parse the list of members
        int count = 0;
        while (listToken.hasNext()) {
            final long timeStart = System.nanoTime();
            final Token subtoken = listToken.next();
            final long timeSubtoken = System.nanoTime();
            final String subtokenName = subtoken.getName();
            if ((null != subtokenName) && !member.name.equals(subtokenName)) {
                // watch it, JSON will result in null names for members of lists
                throw new ResponseException(ResponseStatus.ATTRIBUTE_NAME_NOT_EXISTS, "Parse error: illegal element inside " + name + ": " + subtokenName);
            }
            // is the value null?
            if (subtoken.isNull()) {
                throw new ResponseException(ResponseStatus.VALUE_IS_NULL, "Parse error: null list item inside " + name);
            }
            final Object item = member.parse(subtoken, params);
            final long timeParse = System.nanoTime();
            if (null == item) continue; // ignore any nulls
            try {
                list.add(listType.cast(item));
            }
            catch (ClassCastException e) {
                throw new ResponseException(ResponseStatus.INTERNAL_PROCESSING_ERROR, e, "Parse error: list item for " + name + " is required to be of type " + listType.getCanonicalName() + " but instead received an item of type " + item.getClass().getCanonicalName());
            }
            count++;
            final long timeEnd = System.nanoTime();
            final long timing = timeEnd - timeStart;
            if (timing > 1000000L) {
                if (logBuilder == null) logBuilder = new StringBuilder();
                logBuilder.append("\n  Excessive time parsing a list item \"");
                logBuilder.append(member.name);
                logBuilder.append("\": ");
                logBuilder.append(timing);
                logBuilder.append(" ns; sub-token retrieval: ");
                logBuilder.append(timeSubtoken - timeStart);
                logBuilder.append(" ns, parsing: ");
                logBuilder.append(timeParse - timeSubtoken);
                logBuilder.append(" ns, adding to list: ");
                logBuilder.append(timeEnd - timeParse);
                logBuilder.append(" ns");
            }
        }
        
        if ((0 == count) && !member.optional) {
            throw new ResponseException(ResponseStatus.VALUE_NOT_FOUND, "Parse error: empty list: " + name);
        }

        final long timeEnd = System.nanoTime();
        final long overallTiming = timeEnd - timeBegin;
        if ((overallTiming > 1000000) || (logBuilder != null)) {
            if (logBuilder == null) logBuilder = new StringBuilder();
            logBuilder.insert(0, "[" + Thread.currentThread().getId() + "] Excessive time in list parser \"" + name + "\": " + overallTiming + " ns");
            log.warn(logBuilder.toString());
        }

        return list;
    }
}
