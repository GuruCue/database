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

import java.util.Map;

/**
 * A general structure parser interface.
 */
public interface StructuredTokenParser {
    /**
     * Invoked before a structure member starts being processed.
     * Useful for setting parameters for any parsers involved in
     * parsing the member.
     *
     * @param memberName the name of the member of the current structure that is about to be parsed
     * @param params a map with input/output parameters
     * @throws ResponseException when there cannot be a member with the given name
     */
    void begin(final String memberName, final Map<String, Object> params) throws ResponseException;

    /**
     * Invoked after a structure member has been successfully parsed.
     * Generally this is the point where the member is stored somewhere.
     *
     * @param memberName the name of the structure member that is being passed
     * @param member the structure member that has been successfully parsed
     * @throws ResponseException when there is something wrong about the given member
     */
    void consume(final String memberName, final Object member) throws ResponseException;

    /**
     * Invoked after all the available structure's members have been parsed
     * and consumed, and a parse result must be returned.
     * Generally here any final verifications are done, and the instance itself
     * is returned.
     *
     * @return a parse result
     * @throws ResponseException if something is wrong
     */
    Object finish() throws ResponseException;
}
