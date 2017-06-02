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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.ResponseStatus;

public abstract class Tokenizer {
    protected final static String EMPTY_STRING = "";
    protected final static char LF = '\n';
    protected final static char SLASH = '/';
    protected final static char PERIOD = '.';

    // --------------- Tokenizer instantiator -----------------
    private static interface TokenizerInstantiator {
        Tokenizer createTokenizer(final String input) throws ResponseException;
    }
    private static final TokenizerInstantiator xmlTokenizerInstantiator = new TokenizerInstantiator() {
        public final Tokenizer createTokenizer(final String input) throws ResponseException { return new XMLTokenizer(input); }
    };
    private static final TokenizerInstantiator jsonTokenizerInstantiator = new TokenizerInstantiator() {
        public final Tokenizer createTokenizer(final String input) throws ResponseException { return new JSONTokenizer(input); }
    };
    private final static Map<String, TokenizerInstantiator> formats;
    static {
        final Map<String, TokenizerInstantiator> f = new HashMap<String, TokenizerInstantiator>();
        f.put("xml", xmlTokenizerInstantiator);
        f.put("json", jsonTokenizerInstantiator);
        formats = Collections.unmodifiableMap(f);
    }
    
    public static Tokenizer tokenize(final String format, final String input) throws ResponseException {
        final TokenizerInstantiator instantiator = formats.get(format);
        if (null == instantiator) throw new ResponseException(ResponseStatus.UNKNOWN_MIME_TYPE, "Unrecognized data format: " + format);
        return instantiator.createTokenizer(input);
    }
    // --------------- End of tokenizer instantiator -----------------
    
    protected final String input;
    protected final int inputLength;
    protected int currentCharIndex;
    protected int currentLineNumber;
    protected int currentPositionNumber;
    
    protected Tokenizer(final String input) {
        this.input = null == input ? EMPTY_STRING : input;
        this.inputLength = input.length();
        this.currentCharIndex = 0;
        this.currentLineNumber = 1;
        this.currentPositionNumber = 1;
    }
    
    abstract public Token nextToken() throws ResponseException;
    
    public final boolean eof() {
        return !skipWhiteSpace();
    }
    
    public final int getLineNumber() {
        return currentLineNumber;
    }
    
    public final int getColumnNumber() {
        return currentPositionNumber;
    }
    
    protected final boolean skipWhiteSpace() {
        if (currentCharIndex >= inputLength) return false; // past the end of string
        while (Character.isWhitespace(input.charAt(currentCharIndex))) {
            if (!incrementCharIndex()) return false; // past the end of string
        }
        return true; // at least one character remaining
    }
    
    protected final boolean incrementCharIndex() {
        if (currentCharIndex >= inputLength) return false; // cannot increment, nothing left
        if (LF == input.charAt(currentCharIndex)) {
            currentLineNumber++;
            currentPositionNumber = 1;
        }
        else {
            currentPositionNumber++; // TODO: position number doesn't account for high+low surrogates, which represent a single character, although they occupy two chars
        }
        currentCharIndex++;
        return currentCharIndex < inputLength;
    }
    
    protected final void decrementCharIndex() {
        currentCharIndex--;
        if (LF == input.charAt(currentCharIndex)) {
            currentLineNumber--;
            if (currentLineNumber <= 1) currentPositionNumber = currentCharIndex;
            else {
                final int previousLFIndex = input.lastIndexOf(LF, currentCharIndex-1);
                currentPositionNumber = currentCharIndex - previousLFIndex;
            }
        }
        else {
            currentPositionNumber--;
        }
    }
    
    protected final boolean isDecimalDigit(final char c) {
        return ((c >= '0') && (c <= '9'));
    }
    
    protected final boolean isHexDigit(final char c) {
        if ((c >= '0') && (c <= '9')) return true;
        if ((c >= 'A') && (c <= 'F')) return true;
        if ((c >= 'a') && (c <= 'f')) return true;
        return false;
    }
}
