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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.ResponseStatus;
import com.gurucue.recommendations.tokenizer.MapToken;
import com.gurucue.recommendations.tokenizer.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MapRule extends Rule {
    private static final Logger log = LogManager.getLogger(MapRule.class);
    public final Map<String, Rule> members;
    public final Set<String> compulsoryMembers;
    private final StructuredTokenParserMaker resultMaker;
    
    public MapRule(final String name, final boolean optional, final StructuredTokenParserMaker resultMaker, final Rule[] members) {
        super(name, optional);
        this.resultMaker = resultMaker;
        if ((null == members) || (members.length == 0)) throw new IllegalArgumentException("A MapRule requires at least one member, but none given");
        Map<String, Rule> m = new HashMap<String, Rule>();
        Set<String> c = new HashSet<String>();
        for (int i = 0; i < members.length; i++) {
            Rule r = members[i];
            m.put(r.name, r);
            if (!r.optional) c.add(r.name);
        }
        this.members = Collections.unmodifiableMap(m);
        this.compulsoryMembers = Collections.unmodifiableSet(c);
    }

    @Override
    public final Object parse(final Token token, final Map<String, Object> params) throws ResponseException {
        // name is verified by the caller
        // caller ensures token.isNull() == false
        final long timeBegin = System.nanoTime();
        StringBuilder logBuilder = null;

        if (!(token instanceof MapToken)) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: given token does not represent a map");
        final MapToken mapToken = (MapToken)token;
        final StructuredTokenParser result = resultMaker.create(params);
        final Map<String, Rule> availableMembers = new HashMap<String, Rule>(members);
        final Set<String> leftoverCompulsoryMembers = new HashSet<String>(compulsoryMembers);

        // parse the members
        while (mapToken.hasNext()) {
            final long timeStart = System.nanoTime();
            final Token subtoken = mapToken.next();
            final long timeSubtoken = System.nanoTime();
            final String subtokenName = subtoken.getName();
            if (null == subtokenName) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: null name inside " + name);
            final Rule subrule = availableMembers.remove(subtokenName);
            if (null == subrule) {
                if (members.containsKey(subtokenName)) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error inside " + name + ": " + subtokenName + " is specified multiple times");
                throw new ResponseException(ResponseStatus.ATTRIBUTE_NAME_NOT_EXISTS, "Parse error: illegal element inside " + name + ": " + subtokenName);
            }
            long timeParseBegin = 0L;
            long timeParseConsume = 0L;
            // is the value null?
            if (subtoken.isNull()) {
                // verify that it is not required
                if (leftoverCompulsoryMembers.remove(subtokenName)) {
                    throw new ResponseException(ResponseStatus.VALUE_IS_NULL, "Parse error: " + subtoken.getName() + " cannot be empty");
                }
            }
            else {
                // only parse non-null values
                leftoverCompulsoryMembers.remove(subtokenName);
                result.begin(subrule.name, params);
                timeParseBegin = System.nanoTime();
                result.consume(subrule.name, subrule.parse(subtoken, params));
                timeParseConsume = System.nanoTime();
            }
            final long timeFinish = System.nanoTime();
            final long timingIteration = timeFinish - timeStart;
            if (timingIteration > 1000000L) {
                if (logBuilder == null) logBuilder = new StringBuilder();
                logBuilder.append("\n  Excessive time parsing a map item \"");
                logBuilder.append(subtokenName);
                logBuilder.append("\": ");
                logBuilder.append(timingIteration);
                logBuilder.append(" ns; sub-token retrieval: ");
                logBuilder.append(timeSubtoken - timeStart);
                if (subtoken.isNull()) {
                    logBuilder.append(" ns, sub-token is null");
                }
                else {
                    logBuilder.append(" ns, before parse begin: ");
                    logBuilder.append(timeParseBegin - timeSubtoken);
                    logBuilder.append(" ns, parse + consume: ");
                    logBuilder.append(timeParseConsume - timeParseBegin);
                    logBuilder.append(" ns");
                }
            }
        }
        
        // check that there aren't any missing values
        if (leftoverCompulsoryMembers.size() > 1) {
            // this is for multiple missing values
            final java.util.Iterator<String> leftovers = leftoverCompulsoryMembers.iterator();
            final StringBuilder sb = new StringBuilder("Parse error: missing values: ");
            sb.append(leftovers.next());
            while (leftovers.hasNext()) {
                sb.append(", ");
                sb.append(leftovers.next());
            }
            throw new ResponseException(ResponseStatus.VALUE_NOT_FOUND, sb.toString());
        }
        else if (leftoverCompulsoryMembers.size() > 0) {
            // this is for a single missing value
            throw new ResponseException(ResponseStatus.VALUE_NOT_FOUND, "Parse error: missing value: " + leftoverCompulsoryMembers.iterator().next());
        }

        final long timeBeforeFinish = System.nanoTime();
        Object obj = result.finish(); // perform any cleanup or final checks, gets the actual object to return as the result
        final long timeAfterFinish = System.nanoTime();

        final long timeEnd = System.nanoTime();
        final long overallTiming = timeEnd - timeBegin;
        if ((overallTiming > 1000000) || (logBuilder != null)) {
            if (logBuilder == null) logBuilder = new StringBuilder();
            logBuilder.insert(0, "[" + Thread.currentThread().getId() + "] Excessive time in map parser \"" + name + "\": " + overallTiming + " ns");
            logBuilder.append("\n  Finish invocation: ");
            logBuilder.append(timeAfterFinish - timeBeforeFinish);
            logBuilder.append(" ns");
            log.warn(logBuilder.toString());
        }

        return obj;
    }
}
