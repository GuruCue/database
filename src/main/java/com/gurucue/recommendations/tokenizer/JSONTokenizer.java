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

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.ResponseStatus;

public final class JSONTokenizer extends Tokenizer {
    private final static Logger logger = LogManager.getLogger(JSONTokenizer.class);

    protected final static char SINGLE_QUOTE = '\'';
    protected final static char DOUBLE_QUOTE = '"';
    protected final static char SPACE = ' ';
    protected final static char DEL = '\u007f';
    protected final static char APC = '\u009f';
    protected final static char BACKSLASH = '\\';
    protected final static char B_SMALL = 'b';
    protected final static char F_SMALL = 'f';
    protected final static char N_SMALL = 'n';
    protected final static char R_SMALL = 'r';
    protected final static char T_SMALL = 't';
    protected final static char U_SMALL = 'u';
    protected final static char BEL = '\b';
    protected final static char FF = '\f';
    protected final static char CR = '\r';
    protected final static char TAB = '\t';
    protected final static char MINUS = '-';
    protected final static char E_SMALL = 'e';
    protected final static char E_BIG = 'E';
    protected final static char PLUS = '+';
    protected final static String TRUE = "true";
    protected final static int TRUE_LENGTH = TRUE.length();
    protected final static String FALSE = "false";
    protected final static int FALSE_LENGTH = FALSE.length();
    protected final static String NULL = "null";
    protected final static int NULL_LENGTH = NULL.length();
    protected final static char COMMA = ',';
    protected final static char BRACE_OPEN = '{';
    protected final static char BRACE_CLOSE = '}';
    protected final static char BRACKET_OPEN = '[';
    protected final static char BRACKET_CLOSE = ']';
    protected final static char COLON = ':';

    protected boolean insideStructure;
    
    private final Deque<StructuredType> openStructures = new ArrayDeque<StructuredType>();
    
    public JSONTokenizer(final String input) {
        super(input);
        insideStructure = false;
    }

    @Override
    public final Token nextToken() throws ResponseException {
        final boolean isRootElement = currentCharIndex == 0;
        final boolean wasInsideStructure = insideStructure;
        insideStructure = true; // default
        
        if (!skipWhiteSpace()) {
            if (openStructures.size() > 0) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Premature end of JSON");
            return null; // EOF
        }
        char currentChar = input.charAt(currentCharIndex);
        final StructuredType currentStructure = isRootElement ? null : openStructures.peekLast();
        
        // check for termination of the current structure
        if (BRACE_CLOSE == currentChar) {
            if (StructuredType.OBJECT != currentStructure) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Syntax error at line " + currentLineNumber + ", column " + currentPositionNumber + ": closing brace outside an object");
            currentCharIndex++;
            currentPositionNumber++;
            openStructures.removeLast();
            return null; // END_OF_STRUCTURE
        }
        if (BRACKET_CLOSE == currentChar) {
            if (StructuredType.ARRAY != currentStructure) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Syntax error at line " + currentLineNumber + ", column " + currentPositionNumber + ": closing bracket outside an array");
            currentCharIndex++;
            currentPositionNumber++;
            openStructures.removeLast();
            return null; // END_OF_STRUCTURE
        }
        
        // if inside structure, we need the comma delimiter
        if (wasInsideStructure) {
            if (COMMA != currentChar) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Syntax error at line " + currentLineNumber + ", column " + currentPositionNumber + ": expecting a comma");
            currentCharIndex++;
            currentPositionNumber++;
            if (!skipWhiteSpace()) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Premature end of JSON");
            currentChar = input.charAt(currentCharIndex);
        }
        
        // if inside an object, the element name must be given
        final String elementName;
        if (StructuredType.OBJECT == currentStructure) {
            // extract key name and a colon, move up to the value
            final Object nextContent = getValue();
            if (!(nextContent instanceof String)) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Syntax error at line " + currentLineNumber + ", column " + currentPositionNumber + ": expected a key to an object property");
            elementName = (String)nextContent;
            if (!skipWhiteSpace()) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Syntax error at line " + currentLineNumber + ", column " + currentPositionNumber + ": premature end of JSON");
            currentChar = input.charAt(currentCharIndex);
            if (COLON != currentChar) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Syntax error at line " + currentLineNumber + ", column " + currentPositionNumber + ": key not followed by a colon");
            currentCharIndex++;
            currentPositionNumber++;
            if (!skipWhiteSpace()) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Premature end of JSON");
            currentChar = input.charAt(currentCharIndex);
        }
        else {
            elementName = null;
        }
        
        // get the value
        if (BRACE_OPEN == currentChar) {
            // it's an object
            currentCharIndex++;
            currentPositionNumber++;
            openStructures.addLast(StructuredType.OBJECT);
            insideStructure = false;
            return new JSONObjectToken(elementName, this);
        }
        else if (BRACKET_OPEN == currentChar) {
            // it's an array
            currentCharIndex++;
            currentPositionNumber++;
            openStructures.addLast(StructuredType.ARRAY);
            insideStructure = false;
            return new JSONListToken(elementName, this);
        }
        
        // it's a primitive value
        final Object value = getValue();
        if (null == value) return new NullToken(elementName);
        if (value instanceof String) return new StringToken(elementName, (String)value);
        if (value instanceof Long) return new LongToken(elementName, (Long)value);
        if (value instanceof Double) return new DoubleToken(elementName, (Double)value);
        if (value instanceof Boolean) return new BooleanToken(elementName, (Boolean)value);
        
        throw new ResponseException(ResponseStatus.INTERNAL_PROCESSING_ERROR, "Don't know how to tokenize the value of type: " + value.getClass().getCanonicalName());
    }

    private Object getValue() throws ResponseException {
        if (!skipWhiteSpace()) return null;
        char currentChar = input.charAt(currentCharIndex);
        
        if ((SINGLE_QUOTE == currentChar) || (DOUBLE_QUOTE == currentChar)) {
            // parse a string
            final char quote = currentChar; // remember what quotation mark we're using
            final int stringStartLine = currentLineNumber;
            final int stringStartPosition = currentPositionNumber;
            currentCharIndex++;
            currentPositionNumber++;
            final StringBuilder result = new StringBuilder();
            for (;;) {
                if (currentCharIndex >= inputLength) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Unterminated string at line " + stringStartLine + ", position " + stringStartPosition);
                currentChar = input.charAt(currentCharIndex);
                if (isControlCharacter(currentChar)) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Control characters must not be present inside strings, invalid character #" + ((int)currentChar) + " at line " + currentLineNumber + ", position " + currentPositionNumber);
                currentCharIndex++;
                currentPositionNumber++;
                if (quote == currentChar) {
                    // reached the end of string
                    return result.toString();
                }
                if (BACKSLASH == currentChar) {
                    // escaped character
                    if (currentCharIndex >= inputLength) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Unterminated string at line " + stringStartLine + ", position " + stringStartPosition);
                    currentChar = input.charAt(currentCharIndex);
                    currentCharIndex++;
                    currentPositionNumber++;
                    switch (currentChar) {
                    case SINGLE_QUOTE:
                        result.append(SINGLE_QUOTE);
                        break;
                    case DOUBLE_QUOTE:
                        result.append(DOUBLE_QUOTE);
                        break;
                    case BACKSLASH:
                        result.append(BACKSLASH);
                        break;
                    case SLASH:
                        result.append(SLASH);
                        break;
                    case B_SMALL:
                        result.append(BEL);
                        break;
                    case F_SMALL:
                        result.append(FF);
                        break;
                    case N_SMALL:
                        result.append(LF);
                        break;
                    case R_SMALL:
                        result.append(CR);
                        break;
                    case T_SMALL:
                        result.append(TAB);
                        break;
                    case U_SMALL:
                        if ((currentCharIndex + 3) >= inputLength) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Unterminated string at line " + stringStartLine + ", position " + stringStartPosition);
                        for (int i = 0; i < 4; i++) {
                            if (!isHexDigit(input.charAt(currentCharIndex + i))) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Invalid character code at line " + currentLineNumber + ", position " + (currentPositionNumber-2));
                        }
                        result.append(new String(new int[] {Integer.parseInt(input.substring(currentCharIndex, currentCharIndex+4), 16)}, 0, 1));
                        currentCharIndex += 4;
                        currentPositionNumber += 4;
                        break;
                    default:
                        throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Invalid escape sequence at line " + currentLineNumber + ", position " + currentPositionNumber);
                    }
                }
                else {
                    // ordinary character
                    result.append(currentChar);
                }
            }
        }
        
        if (isDecimalDigit(currentChar) || (MINUS == currentChar)) {
            // parse a number
            final boolean negative = MINUS == currentChar;
            boolean isInteger = true;
            final int numberStartIndex = negative ? currentCharIndex+1 : currentCharIndex; // skip over an optional minus
            final int parseStart = currentCharIndex;
            currentCharIndex++;
            currentPositionNumber++;
            // parse the integer part, decimal dot, and the fractional part
            while (currentCharIndex < inputLength) {
                currentChar = input.charAt(currentCharIndex);
                if (!isDecimalDigit(currentChar)) {
                    if (numberStartIndex == currentCharIndex) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Invalid number at line " + currentLineNumber + ", position " + currentPositionNumber);
                    if (PERIOD == currentChar) {
                        if (!isInteger) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Invalid number at line " + currentLineNumber + ", position " + currentPositionNumber);
                        isInteger = false;
                    }
                    else break;
                }
                currentCharIndex++;
                currentPositionNumber++;
            }
            // parse the optional exponent part
            if ((currentCharIndex < inputLength) && ((E_SMALL == currentChar) || (E_BIG == currentChar))) {
                isInteger = false;
                currentCharIndex++;
                currentPositionNumber++;
                if (currentCharIndex >= inputLength) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Invalid number at line " + currentLineNumber + ", position " + currentPositionNumber);
                // detect optional + or - prefix
                currentChar = input.charAt(currentCharIndex);
                if ((MINUS == currentChar) || (PLUS == currentChar)) {
                    currentCharIndex++;
                    currentPositionNumber++;
                }
                // skip over all the digits
                final int exponentStart = currentCharIndex;
                while (currentCharIndex < inputLength) {
                    currentChar = input.charAt(currentCharIndex);
                    if (!isDecimalDigit(currentChar)) break;
                    currentCharIndex++;
                    currentPositionNumber++;
                }
                if (exponentStart == currentCharIndex) throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Invalid number at line " + currentLineNumber + ", position " + currentPositionNumber);
            }
            // finally, do the conversion
            if (isInteger) return Long.valueOf(input.substring(parseStart, currentCharIndex), 10);
            return Double.valueOf(input.substring(parseStart, currentCharIndex));
        }
        
        if (input.startsWith(TRUE, currentCharIndex)) {
            // boolean true
            currentCharIndex += TRUE_LENGTH;
            currentPositionNumber += TRUE_LENGTH;
            return Boolean.TRUE;
        }
        
        if (input.startsWith(FALSE, currentCharIndex)) {
            // boolean false
            currentCharIndex += FALSE_LENGTH;
            currentPositionNumber += FALSE_LENGTH;
            return Boolean.FALSE;
        }
        
        if (input.startsWith(NULL, currentCharIndex)) {
            // null value
            currentCharIndex += NULL_LENGTH;
            currentPositionNumber += NULL_LENGTH;
            return null;
        }
        
        throw new ResponseException(ResponseStatus.MALFORMED_JSON, "Invalid JSON syntax at line " + currentLineNumber + ", position " + currentPositionNumber);
    }

    protected final boolean isControlCharacter(final char c) {
        return (c < SPACE) || ((c >= DEL) && (c <= APC));
    }
    
    private static enum StructuredType {
        ARRAY,
        OBJECT;
    }
}
