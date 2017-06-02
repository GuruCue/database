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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.gurucue.recommendations.ResponseException;

/**
 * The base structured token class implementation. All deriving
 * classes should only implement one or both of: ListToken, MapToken.
 * No additional methods should be necessary.
 *
 */
public class StructuredTokenImpl implements StructuredToken {
    private final String name;
    private final Tokenizer tokenizer;
    private final List<Token> items = new ArrayList<Token>();
    private boolean reachedEof = false; // when there's nothing more to parse inside the level denoted by the XMLTree instance
    private StructuredTokenImpl lastSubToken = null; // we must ensure that any previous structure was traversed
    private int currentIndex = -1; // index into items[] for nextToken()
    
    protected StructuredTokenImpl(final String name, final Tokenizer parser) {
        // don't allow direct instantiation, only subclasses allowed to invoke the constructor
        this.name = name;
        this.tokenizer = parser;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    private Token nextToken() throws ResponseException {
        if (reachedEof) return null;
        // if the previous token was a structured-token (map, list), then first jump over its content, to arrive at the next token at the same level
        if (null != lastSubToken) lastSubToken.traverse();
        final Token nextToken = tokenizer.nextToken();
        if (null == nextToken) {
            reachedEof = true;
            lastSubToken = null;
            return null;
        }
        items.add(nextToken);
        lastSubToken = nextToken instanceof StructuredTokenImpl ? (StructuredTokenImpl)nextToken : null;
        return nextToken;
    }

    @Override
    public boolean hasNext() throws ResponseException {
        int i = currentIndex + 1;
        while (i >= items.size()) {
            if (null == nextToken()) return false;
        }
        return true;
    }

    @Override
    public Token next() throws ResponseException, NoSuchElementException {
        currentIndex++;
        if (currentIndex < items.size()) return items.get(currentIndex);
        final Token token = nextToken();
        if (null == token) throw new NoSuchElementException("No more items available");
        return token;
    }

    private void traverse() throws ResponseException {
        while (!reachedEof) nextToken();
    }
}
