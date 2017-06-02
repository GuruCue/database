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
package com.gurucue.recommendations.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * JSON parser. You'll probably need only {@link JsonParser#parse(CharSequence)}.
 */
public final class JsonParser {
    public final JsonData data;
    
    public static Json parse(final CharSequence input) {
        return parse(new JsonDataCharSequence(input, 0));
    }

    public static Json parse(final Reader input) {
        return parse(new JsonDataReader(input));
    }

    public static Json parse(final InputStream input) {
        return parse(new JsonDataReader(new BufferedReader(new InputStreamReader(input))));
    }

    private static Json parse(final JsonData data) {
        final JsonParser parser = new JsonParser(data);
        final Json result = parser.parse();
        if (data.currentChar() != 0) throw new JsonParseError("Parse error at position " + data.currentPos());
        return result;
    }

    public static JsonParser create(final CharSequence input) {
        return new JsonParser(new JsonDataCharSequence(input, 0));
    }

    public static JsonParser create(final CharSequence input, final int index) {
        return new JsonParser(new JsonDataCharSequence(input, index));
    }

    public static JsonParser create(final Reader input) {
        return new JsonParser(new JsonDataReader(input));
    }

    public static JsonParser create(final InputStream input) {
        return new JsonParser(new JsonDataReader(new BufferedReader(new InputStreamReader(input))));
    }

    public JsonParser(final JsonData data) {
        this.data = data;
    }

    /**
     * Parses and returns a JSON object.
     *
     * @return a Json value
     */
    public Json parse() {
        for (;;) {
            final char c = data.currentChar();
            switch (c) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    // skip whitespace
                    break;

                case '"':
                case '\'':
                    return parseString();

                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return parseNumber();

                case 't':
                case 'f':
                    return parseBoolean();

                case 'n':
                    // a possible "null"
                    return parseNull();

                case '{':
                    return parseObject();

                case '[':
                    return parseArray();

                case 0:
                    return null; // nothing to parse (end of parsing)

                default:
                    // parse error
                    throw new JsonParseError("Parse error at position " + data.currentPos());
            }
            data.skipChar();
        }
    }

    public char skipWhiteSpace() {
        for (;;) {
            final char c = data.currentChar();
            if (c == 0) return 0;
            if ((c != ' ') && (c != '\t') && (c != '\r') && (c != '\n')) return c;
            data.skipChar();
        }
    }

    public JsonString parseString() {
        final char quote = data.nextChar();
        if ((quote != '"') && (quote != '\'')) throw new JsonParseError("Expected a JSON string, but instead of an opening quote there is character '" + quote + "' at position " + (data.currentPos()-1));
        final StringBuilder sb = new StringBuilder(256);
        for(;;) {
            final char c = data.nextChar();
            if (c == quote) {
                return new JsonString(sb.toString());
            }
            else if (c == '\\') {
                final char cc = data.nextChar();
                switch (cc) {
                    case '"': // quotation mark
                        sb.append('"');
                        break;
                    case '\'': // compatibility with single-quoted strings
                        sb.append('\'');
                        break;
                    case '\\': // reverse solidus
                        sb.append('\\');
                        break;
                    case '/': // solidus
                        sb.append('/');
                        break;
                    case 'b': // backspace
                        sb.append('\b');
                        break;
                    case 'f': // formfeed
                        sb.append('\f');
                        break;
                    case 'n': // newline
                        sb.append('\n');
                        break;
                    case 'r': // carriage return
                        sb.append('\r');
                        break;
                    case 't': // horizontal tab
                        sb.append('\t');
                        break;
                    case 'u': // 4 hexadecimal digits -> unicode codepoint
                        int codePoint = 0;
                        for (int i = 0; i < 4; i++) {
                            final char hex = data.nextChar();
                            if (hex == 0) throw new JsonParseError("Unterminated JSON string at position " + data.currentPos());
                            final int n;
                            if ((hex >= '0') && (hex <= '9')) n = hex - '0';
                            else if ((hex >= 'a') && (hex <= 'f')) n = 10 + hex - 'a';
                            else if ((hex >= 'A') && (hex <= 'F')) n =  10 + hex - 'A';
                            else throw new JsonParseError("Not a hexadecimal character at position " + data.currentPos() + ": '" + hex + "'");
                            codePoint = (codePoint << 4) | n;
                        }
                        sb.append(Character.toChars(codePoint));
                        break;
                    case 0:
                        throw new JsonParseError("Unterminated JSON string at position " + (data.currentPos()-1));
                    default:
                        throw new JsonParseError("Illegal escaped character at position " + (data.currentPos()-1) + ": '" + cc + "'");
                }
            }
            else if (c == 0) throw new JsonParseError("EOF while parsing a string");
            else sb.append(c);
        }
    }

    public JsonNumber parseNumber() {
        char c = data.nextChar();
        final boolean isNegative;
        if (c == '-') {
            isNegative = true;
            c = data.nextChar();
        } else isNegative = false;
        if (c == 0) throw new JsonParseError("Incomplete number at position " + (data.currentPos() - 1));

        // parse the integral part
        long p = 0;
        if (c == '0') { // if the first character is a 0, then no digits may follow
            c = data.currentChar();
        } else {
            // otherwise the first character must be a non-zero digit
            if ((c >= '1') && (c <= '9')) p = c - '0';
            else throw new JsonParseError("Invalid character in a number at position " + (data.currentPos() - 1) + ": " + c);
            // and more digits may follow
            for (c = data.currentChar(); c != 0; c = data.currentChar()) {
                if ((c >= '0') && (c <= '9')) {
                    p = (p * 10) + (c - '0');
                    data.skipChar();
                }
                else break;
            }
        }

        if (isNegative) p = -p;
        if (c == 0) return new JsonLong(p);

        // parse the decimal part, if there is one
        long q = 0; // numerator
        long r = isNegative ? -1 : 1; // denominator
        if (c == '.') {
            // exactly one digit must follow
            data.skipChar();
            c = data.nextChar();
            if (c == 0) throw new JsonParseError("Incomplete number at position " + (data.currentPos() - 2));
            if ((c >= '0') && (c <= '9')) {
                q = c - '0';
                r *= 10;
            }
            else throw new JsonParseError("Invalid character in a number at position " + (data.currentPos() - 1) + ": " + c);
            // and more digits may follow
            for (c = data.currentChar(); c != 0; c = data.currentChar()) {
                if ((c >= '0') && (c <= '9')) {
                    q = (q * 10) + (c - '0');
                    r *= 10;
                    data.skipChar();
                }
                else break;
            }
            if (c == 0) return new JsonDouble(((double) p) + (((double) q) / ((double) r)));
        }

        // parse the exponent part, if there is one
        long s = 0;
        boolean isNegativeExponent = false;
        if ((c == 'e') || (c == 'E')) {
            data.skipChar();
            c = data.nextChar();
            if (c == '+') {
                isNegativeExponent = false;
                c = data.nextChar();
            }
            else if (c == '-') {
                isNegativeExponent = true;
                c = data.nextChar();
            }
            if (c == 0) throw new JsonParseError("Incomplete number at position " + (data.currentPos() - 2));
            // there must be at least one digit in the exponent
            if ((c >= '0') && (c <= '9')) s = c - '0';
            else throw new JsonParseError("Invalid character in a number at position " + (data.currentPos() - 1) + ": " + c);
            // and more digits may follow
            for (c = data.currentChar(); c != 0; c = data.currentChar()) {
                if ((c >= '0') && (c <= '9')) {
                    s = (s * 10) + (c - '0');
                    data.skipChar();
                }
                else break;
            }
            if (isNegativeExponent) s = -s;
        }

        if (q == 0) {
            // a whole number
            if (s == 0) return new JsonLong(p);
            // determine if it's long-able
            if (isNegativeExponent) return new JsonDouble(((double)p) * Math.pow(10, s));
            final double digitsLimit = Math.log10((double)Long.MAX_VALUE);
            final double digitsCount = Math.log10(p) + (double)s;
            if (digitsCount < digitsLimit) {
                // it's long-able
                for (long e = 0; e < s; e++) p *= 10;
                return new JsonLong(p);
            }
            // must use a double
            return new JsonDouble(((double)p) * Math.pow(10, s));
        }
        else if (s == 0) {
            // no exponent
            return new JsonDouble(((double) p) + (((double) q) / ((double) r)));
        }
        return new JsonDouble((((double) p) + (((double) q) / ((double) r))) * Math.pow(10, s));
    }

    private static final char[] REMAINING_TRUE = new char[]{'r', 'u', 'e'};
    private static final char[] REMAINING_FALSE = new char[]{'a', 'l', 's', 'e'};

    public JsonBoolean parseBoolean() {
        final char firstChar = data.nextChar();
        final char[] expectedRemaining;
        if (firstChar == 't') expectedRemaining = REMAINING_TRUE;
        else if (firstChar == 'f') expectedRemaining = REMAINING_FALSE;
        else throw new JsonParseError("Parse error at position " + data.currentPos());
        final int n = expectedRemaining.length;
        for (int j = 0; j < n; j++) {
            final char c = data.nextChar();
            if (c == 0) throw new JsonParseError("Parse error at position " + (data.currentPos() - 1));
            if (c != expectedRemaining[j]) throw new JsonParseError("Parse error at position " + data.currentPos());
        }
        return firstChar == 't' ? JsonBoolean.TRUE : JsonBoolean.FALSE;
    }

    private static final char[] NULL_TOKEN = new char[]{'n', 'u', 'l', 'l'};

    public JsonNull parseNull() {
        final int n = NULL_TOKEN.length;
        for (int j = 0; j < n; j++) {
            final char c = data.nextChar();
            if (c == 0) throw new JsonParseError("Parse error at position " + (data.currentPos() - 1));
            if (c != NULL_TOKEN[j]) throw new JsonParseError("Parse error at position " + data.currentPos());
        }
        return JsonNull.INSTANCE;
    }

    public JsonObject parseObject() {
        final char firstChar = data.nextChar();
        if (firstChar != '{') throw new JsonParseError("Parse error at position " + data.currentPos());
        final char c1 = skipWhiteSpace();
        if (c1 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON object");
        if (c1 == '}') {
            data.skipChar();
            return new JsonObject(); // empty object
        }
        final LinkedHashMap<String, Json> mapping = new LinkedHashMap<>();
        for (;;) {
            // first the key
            final JsonString key = parseString();
            // the key-value separator
            final char c2 = skipWhiteSpace();
            if (c2 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON object");
            final char colon = data.nextChar();
            if (colon != ':') throw new JsonParseError("Parse error: expected a colon at position " + data.currentPos() + ", got: " + colon);
            // the value
            final char c3 = skipWhiteSpace();
            if (c3 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON object");
            final Json value = parse();
            mapping.put(key.asString(), value);
            // either the '}', or the comma
            final char c4 = skipWhiteSpace();
            if (c4 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON object");
            final char separator = data.nextChar();
            if (separator == '}') return new JsonObject(mapping);
            if (separator != ',') throw new JsonParseError("Parse error: expected a comma at position " + (data.currentPos()-1) + ", got: " + separator);
            final char c5 = skipWhiteSpace();
            if (c5 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON object");
        }
    }

    public JsonArray parseArray() {
        final char firstChar = data.nextChar();
        if (firstChar != '[') throw new JsonParseError("Parse error at position " + data.currentPos() + ", expected [, got: " + firstChar);
        final char c1 = skipWhiteSpace();
        if (c1 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON array");
        if (c1 == ']') {
            data.skipChar();
            return new JsonArray(); // empty array
        }
        final LinkedList<Json> list = new LinkedList<>();
        for (;;) {
            list.add(parse());
            final char c2 = skipWhiteSpace();
            if (c2 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON array");
            final char separator = data.nextChar();
            if (separator == ']') return new JsonArray(list);
            if (separator != ',') throw new JsonParseError("Parse error: expected a comma at position " + (data.currentPos()-1) + ", got: " + separator);
            final char c3 = skipWhiteSpace();
            if (c3 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON array");
        }
    }

    public void streamingParseArray(final JsonCallback callback) {
        final char firstChar = data.nextChar();
        if (firstChar != '[') throw new JsonParseError("Parse error at position " + data.currentPos());
        final char c1 = skipWhiteSpace();
        if (c1 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON array");
        if (c1 == ']') {
            data.skipChar();
            return; // empty array
        }
        final LinkedList<Json> list = new LinkedList<>();
        for (;;) {
            callback.readJson(parse());
            final char c2 = skipWhiteSpace();
            if (c2 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON array");
            final char separator = data.nextChar();
            if (separator == ']') return;
            if (separator != ',') throw new JsonParseError("Parse error: expected a comma at position " + (data.currentPos()-1) + ", got: " + separator);
            final char c3 = skipWhiteSpace();
            if (c3 == 0) throw new JsonParseError("Incomplete JSON code when parsing a JSON array");
        }
    }

    public interface JsonData {
        /**
         * Returns the current character and advances the position to the next character.
         * @return the current character
         */
        char nextChar();

        /**
         * Returns the current character, without changing the current position.
         * @return the current character
         */
        char currentChar();

        /**
         * Advances the position to the next character.
         */
        void skipChar();

        /**
         * Returns the current position.
         * @return the current position
         */
        int currentPos();
    }

    static final class JsonDataCharSequence implements JsonData {
        final CharSequence input;
        final int startingIndex;
        final int len;
        int currentIndex;
        char currentChar;

        JsonDataCharSequence(final CharSequence input, final int index) {
            this.input = input;
            this.startingIndex = index < 0 ? 0 : index;
            this.len = input.length();
            this.currentIndex = startingIndex;
            this.currentChar = currentIndex >= len ? 0 : input.charAt(currentIndex);
        }

        @Override
        public char nextChar() {
            final char result = currentChar;
            skipChar();
            return result;
        }

        @Override
        public char currentChar() {
            return currentChar;
        }

        @Override
        public void skipChar() {
            final int i = ++currentIndex;
            currentChar = i < len ? input.charAt(i) : 0;
        }

        @Override
        public int currentPos() {
            return currentIndex;
        }
    }

    static final class JsonDataReader implements JsonData {
        final Reader reader;
        char currentChar;
        int pos = 0;

        JsonDataReader(final Reader reader) {
            this.reader = reader;
            final int c;
            try {
                c = reader.read();
            } catch (IOException e) {
                throw new JsonParseError("I/O Error while reading a JSON stream: " + e.toString(), e);
            }
            currentChar = c < 0 ? 0 : (char) c;
        }

        @Override
        public char nextChar() {
            final char result = currentChar;
            skipChar();
            return result;
        }

        @Override
        public char currentChar() {
            return currentChar;
        }

        @Override
        public void skipChar() {
            final int c;
            try {
                c = reader.read();
            } catch (IOException e) {
                throw new JsonParseError("I/O Error while reading a JSON stream: " + e.toString(), e);
            }
            currentChar = c < 0 ? 0 : (char) c;
            pos++;
        }

        @Override
        public int currentPos() {
            return pos;
        }
    }
}
