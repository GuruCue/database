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
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.ResponseStatus;

/**
 * TODO: doesn't handle internal DTD subsets, conditional inclusion and similar, e.g.:
 * <ul>
 *   <li><code>&lt;!DOCTYPE person [ &lt;!ELEMENT</code> ... <code>&gt; ]&gt;</code></li>
 *   <li><code>&lt;![IGNORE[ ... ]]&gt;</code></li>
 *   <li><code>&lt;![INCLUDE[ ... ]]&gt;</code></li>
 *   <li> ... </li>
 * </ul>
 *
 */
public final class XMLTokenizer extends Tokenizer {
    private final static Logger logger = LogManager.getLogger(XMLTokenizer.class);
    private final static String CDATA_START = "<![CDATA[";
    private final static int CDATA_PREFIX_LENGTH = CDATA_START.length();
    private final static String CDATA_END = "]]>";
    private final static int CDATA_SUFFIX_LENGTH = CDATA_END.length();
    private final static char LESS_THAN = '<';
    private final static char GREATER_THAN = '>';
    private final static char AMPERSAND = '&';
    private final static char HASH = '#';
    private final static char UNDERSCORE = '_';
    private final static char HYPHEN = '-';
    private final static char SEMICOLON = ';';
    private final static char X_BIG = 'X';
    private final static char X_SMALL = 'x';
    private final static char QUESTION_MARK = '?';
    private final static char EXCLAMATION_MARK = '!';
    private static final Map<String, String> entityCache = new HashMap<String, String>();
    static {
        entityCache.put("amp", "&");
        entityCache.put("apos", "'");
        entityCache.put("quot", "\"");
        entityCache.put("lt", "<");
        entityCache.put("gt", ">");
    }
    
    private final Deque<String> openTags = new ArrayDeque<String>();
    
    public XMLTokenizer(final String input) {
        super(input);
    }
    
    public Token nextToken() throws ResponseException {
        if ((openTags.size() == 0) && (currentCharIndex > 0)) {
            // the root element has been processed in its entirety;
            if (!skipWhiteSpace()) return null; // EOF
            throw new ResponseException(ResponseStatus.MALFORMED_XML, "Superfluous characters after terminating root tag");
        }
        // get next tag
        final String elementName = getElement();
        if (null == elementName) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Truncated XML at line " + currentLineNumber + ", position " + currentPositionNumber);
        if (elementName.charAt(0) == SLASH) {
            // a terminating tag
            final String lastOpenTag = openTags.pollLast();
            if (null == lastOpenTag) throw new ResponseException(ResponseStatus.MALFORMED_XML, "XML begins with a closing tag");
            if ((lastOpenTag.length() != (elementName.length() - 1)) || !elementName.endsWith(lastOpenTag)) {
                throw new ResponseException(ResponseStatus.MALFORMED_XML, "The closing <" + elementName + "> tag doesn't match the corresponding opening tag <" + lastOpenTag + "> at line " + currentLineNumber + ", position " + currentPositionNumber);
            }
            return null; // END_OF_STRUCTURE
        }
        if (elementName.charAt(elementName.length() - 1) == SLASH) {
            // a shortcut opening+closing tag
            return new NullToken(elementName.substring(0, elementName.length()-1)); // name of the token must not contain the terminating slash
        }
        // it's an opening tag
        if (!skipWhiteSpace()) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Truncated XML at line " + currentLineNumber + ", position " + currentPositionNumber);
        // determined whether it's a simple or a complex value
        if (input.charAt(currentCharIndex) == LESS_THAN) {
            // an opening tag followed by another tag, determine if it's a closing tag
            if (currentCharIndex >= (inputLength - 1)) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Truncated XML at line " + currentLineNumber + ", position " + currentPositionNumber);
            if (input.charAt(currentCharIndex+1) != SLASH) {
                // a complex item, containing other tags with a structure
                openTags.addLast(elementName);
                return new XMLTreeToken(elementName, this);
            }
        }
        // a simple value, extract the value and swallow the closing tag
        final String value = getString();
        if (null == value) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Truncated XML at line " + currentLineNumber + ", position " + currentPositionNumber);
        final String closingElementName = getElement();
        if (null == closingElementName) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Truncated XML at line " + currentLineNumber + ", position " + currentPositionNumber);
        if (closingElementName.charAt(0) != SLASH) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Text and tags are not allowed to intermix at the same level, error at line " + currentLineNumber + ", position " + currentPositionNumber);
        if ((elementName.length() != (closingElementName.length() - 1)) || !closingElementName.endsWith(elementName)) {
            throw new ResponseException(ResponseStatus.MALFORMED_XML, "The closing <" + closingElementName + "> tag doesn't match the corresponding opening tag <" + elementName + "> at line " + currentLineNumber + ", position " + currentPositionNumber);
        }
        if (value.length() == 0) return new NullToken(elementName); // it's no different from an empty element
        return new StringToken(elementName, value);
    }
    
    protected String getElement() throws ResponseException {
        String tag;
        char firstChar;
        // skip processing instructions, comments and definitions
        do {
            tag = getElementInternal();
            firstChar = tag.charAt(0);
        } while ((QUESTION_MARK == firstChar) || (EXCLAMATION_MARK == firstChar));
        return tag;
    }
    
    private String getElementInternal() throws ResponseException {
        if (!skipWhiteSpace()) return null; // nothing to parse
        // now a tag must start
        char currentChar = input.charAt(currentCharIndex);
        // does not start with a '<'?
        if (currentChar != LESS_THAN) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Invalid character #" + ((int)currentChar) + " at line " + currentLineNumber + ", position " + currentPositionNumber);
        currentCharIndex++;
        currentPositionNumber++;
        if (!skipWhiteSpace()) return null; // nothing to parse
        currentChar = input.charAt(currentCharIndex);
        if (currentChar == LESS_THAN) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Invalid character #" + ((int)currentChar) + " at line " + currentLineNumber + ", position " + currentPositionNumber);
        final String tagContent = getString();
        // did not terminate with the '>'?
        currentChar = input.charAt(currentCharIndex);
        if (currentChar != GREATER_THAN) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Invalid character #" + ((int)currentChar) + " at line " + currentLineNumber + ", position " + currentPositionNumber);
        currentCharIndex++;
        currentPositionNumber++;
        // find first possible whitespace, it separates the tag name from attributes which we ignore
        int wsPos = -1;
        final int tagContentLength = tagContent.length();
        for (int i = 0; i < tagContentLength; i++) {
            if (Character.isWhitespace(tagContent.charAt(i))) {
                wsPos = i;
                break;
            }
        }
        // find out if it's a self-terminating tag, that is an empty element
        final boolean isEmptyElement = tagContent.charAt(tagContentLength - 1) == SLASH;
        // no tag name?
        if (0 == wsPos) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Invalid character #" + ((int)currentChar) + " at line " + currentLineNumber + ", position " + currentPositionNumber);
        if ((tagContent.charAt(0) == SLASH) && isEmptyElement) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Invalid element at line " + currentLineNumber + ", position " + currentPositionNumber);
        // remove any attributes
        final String tagName;
        if (wsPos > 0) {
            if (isEmptyElement) tagName = tagContent.substring(0, wsPos) + "/";
            else tagName = tagContent.substring(0, wsPos);
        }
        else tagName = tagContent;
        if (tagName.length() == 0) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Empty XML element at line " + currentLineNumber + ", position " + currentPositionNumber);
        return tagName;
    }
    
    private String getString() throws ResponseException {
        if (!skipWhiteSpace()) return null; // nothing to parse
        if (input.startsWith(CDATA_START, currentCharIndex)) {
            // handle a CDATA section
            currentCharIndex += CDATA_PREFIX_LENGTH;
            currentPositionNumber += CDATA_PREFIX_LENGTH;
            final int endPos = input.indexOf(CDATA_END, currentCharIndex);
            if (endPos < 0) throw new ResponseException(ResponseStatus.MALFORMED_XML, "Unterminated CDATA section at line " + currentLineNumber + ", position " + (currentCharIndex - CDATA_PREFIX_LENGTH));
            final String cdataContent = input.substring(currentCharIndex, endPos);
            currentCharIndex = endPos + CDATA_SUFFIX_LENGTH;
            // update line and position counters
            int lastNewlinePos = currentCharIndex - currentPositionNumber;
            for (int i = currentCharIndex; i < endPos; i++) {
                if (input.charAt(i) == LF) {
                    lastNewlinePos = i;
                    currentLineNumber++;
                }
            }
            currentPositionNumber += endPos - lastNewlinePos;
            return cdataContent;
        }
        char currentChar = input.charAt(currentCharIndex);
        if ((LESS_THAN == currentChar) || (GREATER_THAN == currentChar)) return EMPTY_STRING;
        
        final StringBuilder result = new StringBuilder();
        int lastWhiteSpaceStart = -1;
        boolean previousWasNotWhiteSpace = true;
        for (;;) {
            if (AMPERSAND == currentChar) {
                result.append(parseEntity());
            }
            else {
                result.append(currentChar);
            }
            if (!incrementCharIndex()) break;
            currentChar = input.charAt(currentCharIndex);
            if ((GREATER_THAN == currentChar) || (LESS_THAN == currentChar)) break;
            // remember last whitespace position, so we can trim it
            if (Character.isWhitespace(currentChar)) {
                if (previousWasNotWhiteSpace) {
                    lastWhiteSpaceStart = result.length();
                    previousWasNotWhiteSpace = false;
                }
            }
            else {
                previousWasNotWhiteSpace = true;
            }
        }
        if (previousWasNotWhiteSpace) return result.toString();
        return result.substring(0, lastWhiteSpaceStart);
    }
    
    /**
     * Assumption: the currentChar points at an ampersand.
     * The currentCharIndex remains positioned on the last character of the entity.
     * 
     * @return the parsed entity
     */
    private String parseEntity() {
        final int parseStart = currentCharIndex; // remember where we started
        if (!incrementCharIndex()) return "&";
        char currentChar = input.charAt(currentCharIndex);
        if (HASH == currentChar) {
            // parse out the code
            if (!incrementCharIndex()) return "&#"; // premature end
            currentChar = input.charAt(currentCharIndex);
            final boolean hexMode = (X_BIG == currentChar) || (X_SMALL == currentChar);
            if (hexMode) {
                // hexadecimal code, skip over the heading x
                if (!incrementCharIndex()) return input.substring(parseStart, currentCharIndex); // premature end
                currentChar = input.charAt(currentCharIndex);
            }
            final int numberStart = currentCharIndex;
            while (currentCharIndex < inputLength) {
                // verify that the current character is a digit
                if (hexMode) {
                    if (!isHexDigit(currentChar)) break;
                }
                else {
                    if (!isDecimalDigit(currentChar)) break;
                }
                // advance to the next character
                if (!incrementCharIndex()) break; // no more characters
                currentChar = input.charAt(currentCharIndex);
            }
            if (numberStart == currentCharIndex) return input.substring(parseStart, currentCharIndex); // no digits encountered, premature end
            if ((currentCharIndex >= inputLength) || (SEMICOLON == currentChar) || (Character.isWhitespace(currentChar))) {
                // correct entity termination, decode the code
                final String entityName = input.substring(parseStart+1, currentCharIndex); // include the hash character, exclude the termination character
                String result;
                synchronized(entityCache) {
                    result = entityCache.get(entityName);
                    if (null == result) {
                        // the entity name hasn't been encountered before, decode and save it
                        final String entityDecimalCode = input.substring(numberStart, currentCharIndex);
                        result = new String(new int[] {Integer.parseInt(entityDecimalCode, hexMode ? 16 : 10)}, 0, 1);
                        entityCache.put(entityName, result);
                    }
                }
                if (Character.isWhitespace(currentChar)) decrementCharIndex(); // go back over whitespace, it's not part of the entity
                return result;
            }
            // incorrect entity termination, don't decode and return the entity name instead
            return input.substring(parseStart, currentCharIndex+1);
        }
        else {
            // parse out the name
            // it must start with a letter or an underscore
            if (Character.isHighSurrogate(currentChar)) {
                // it's not a LF for sure, and because it's split into surrogates it cannot be an underscore, therefore just check whether it's a letter
                if (((currentCharIndex+1) >= inputLength) || !Character.isLetter(input.codePointAt(currentCharIndex))) {
                    // entity syntax error
                    return input.substring(parseStart, currentCharIndex+1);
                }
                currentCharIndex += 2;
                currentPositionNumber += 2;
            }
            else if (Character.isLetter(currentChar) || (UNDERSCORE == currentChar)) {
                currentCharIndex++;
                currentPositionNumber++;
            }
            else {
                // entity syntax error
                return input.substring(parseStart, currentCharIndex+1);
            }
            // it must continue with a letter, number, underscore, hyphen, or a period, and be terminated by a semicolon or whitespace
            while (currentCharIndex < inputLength) {
                currentChar = input.charAt(currentCharIndex);
                if (Character.isHighSurrogate(currentChar)) {
                    // it's not a LF for sure, and because it's split into surrogates it cannot be an underscore, hyphen, or period, therefore just check whether it's a letter or number
                    if (((currentCharIndex+1) >= inputLength) || !Character.isLetterOrDigit(input.codePointAt(currentCharIndex))) {
                        // entity syntax error
                        return input.substring(parseStart, currentCharIndex+1);
                    }
                    currentCharIndex += 2;
                    currentPositionNumber += 2;
                }
                else if (Character.isLetterOrDigit(currentChar) || (UNDERSCORE == currentChar) || (HYPHEN == currentChar) || (PERIOD == currentChar)) {
                    currentCharIndex++;
                    currentPositionNumber++;
                }
                else if ((SEMICOLON == currentChar) || Character.isWhitespace(currentChar)) {
                    // entity name terminated by a semicolon or whitespace
                    break;
                }
                else {
                    // entity syntax error
                    return input.substring(parseStart, currentCharIndex+1);
                }
            }
            String entityName = input.substring(parseStart+1, currentCharIndex); // skip the ampersand, and leave out the terminating character
            if (Character.isWhitespace(currentChar)) decrementCharIndex(); // go back over whitespace, it's not part of the entity
            // parse ended successfully
            String result;
            synchronized(entityCache) {
                result = entityCache.get(entityName);
            }
            if (null == result) {
                final String entity = input.substring(parseStart, currentCharIndex+1);
                logger.warn("Entity not found: " + entity);
                return entity; // entity name not resolved, return entity name together with the ampersand and last character
            }
            return result;
        }
    }
}
