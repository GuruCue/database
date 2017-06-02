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
package com.gurucue.recommendations.translator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;

/**
 * DataTranslator class for translating to XML format.
 * @see DataTranslator
 */
public final class XMLTranslator extends DataTranslator {
    private Stack<String> objectFinalizers = new Stack<String>();

    public XMLTranslator() {
        
    }

    // === class methods ===

    private static void verifyKeySyntax(final String key) {
        if (null == key) throw new IllegalArgumentException("NULL XML tag name");
        if (0 == key.length()) throw new IllegalArgumentException("Empty XML tag name");
        final int len = key.length();
        int codePoint = key.codePointAt(0);
        if ((codePoint != 0x5f) && ((codePoint < 0x41) || ((codePoint > 0x5a) && (codePoint < 0x61)) || (codePoint > 0x7a))) {
            throw new IllegalArgumentException("Illegal first character in XML name: " + key);
        }
        int i = 1;
        while (i < len) {
            codePoint = key.codePointAt(i);
            if (((codePoint > 0x60) && (codePoint < 0x7b)) || // small letters
                    ((codePoint > 0x40) && (codePoint < 0x5b)) || // caps letters
                    ((codePoint > 0x2f) && (codePoint < 0x3a)) || // digits
                    (0x5f == codePoint) || // underscore
                    (0x3a == codePoint) || // colon
                    (0x2e == codePoint) || // full stop
                    (0x2d == codePoint)) { // hyphen
                i++; // okay
            }
            else { // not okay
                throw new IllegalArgumentException("Illegal character at position " + (i + 1) + " in XML name: " + key);
            }
        }
    }

    public static String escapeCodePoint(final int codePoint) {
        switch (codePoint) {
        case 0x22: // quotation mark
            return "&quot;";
        case 0x26: // ampersand
            return "&amp;";
        case 0x3c: // less-than sign
            return "&lt;";
        case 0x3e: // greater-than sign
            return "&gt;";
        }
        if ((codePoint >= 0x20) && (codePoint < 0x7f)) { // printable range
            return new String(new int[] {codePoint}, 0, 1);
        }
        return String.format("&#%d;", codePoint);
    }

    /**
     * Escapes special characters (quotation mark, ampersand, less-than sign,
     * greater-than sign) using default XML entities. All other characters are
     * passed through as-is, so a Unicode (e.g. UTF-8) encoding is supposed
     * on the <code>output</code>.
     * 
     * @param input the string to escape using default XML entities
     * @param output where the escaped string is to be written
     * @throws IOException if there was an error while writing to the <code>output</code>
     */
    public static void escape(final String input, final Appendable output) throws IOException {
        if (null == input) {
            throw new IllegalArgumentException("Cannot escape a null String to XML");
        }
        final int len = input.length();
        int i = 0;
        int substringStart = 0;
        while (i < len) {
            int codePoint = input.codePointAt(i);
            if ((0x22 == codePoint) || // quotation mark
                    (0x26 == codePoint) || // ampersand
                    (0x3c == codePoint) || // less-than
                    (0x3e == codePoint)/* || // greater-than
                    (codePoint > 0x7e)*/) {
                // unprintable range
                if (substringStart < i) {
                    output.append(input, substringStart, i);
                }
                output.append(escapeCodePoint(codePoint));
                if (codePoint > 0xFFFF) i += 2; // high surrogate + low surrogate
                else i++;
                substringStart = i;
            }
            else if (codePoint > 0xFFFF) i += 2; // high surrogate + low surrogate
            else i++; // printable range
        }
        if (substringStart < len) {
            output.append(input, substringStart, len);
        }
    }

    public static String escape(final String input) {
        final StringBuilder sb = new StringBuilder();
        try {
            escape(input, sb);
        } catch (IOException e) {} // this exception not possible here
        return sb.toString();
    }

    public static String toXML(final String input) {
        final StringBuilder sb = new StringBuilder();
        try {
            toXML(input, sb);
        } catch (IOException e) {} // this exception not possible here
        return sb.toString();
    }

    public static void toXML(final String input, final Appendable output) throws IOException {
        if (null != input) {
            escape(input, output);
        }
    }

    public static String toXML(final Integer input) {
        return null == input ? "" : input.toString();
    }

    public static void toXML(final Integer input, final Appendable output) throws IOException {
        output.append(toXML(input));
    }

    public static String toXML(final Long input) {
        return null == input ? "" : input.toString();
    }

    public static void toXML(final Long input, final Appendable output) throws IOException {
        output.append(toXML(input));
    }

    public static String toXML(final Double input) {
        return null == input ? "" : input.toString();
    }

    public static void toXML(final Double input, final Appendable output) throws IOException {
        output.append(toXML(input));
    }

    public static String toXML(final Boolean input) {
        if (null == input) return "";
        if (input.booleanValue()) return "true";
        return "false";
    }

    public static void toXML(final Boolean input, final Appendable output) throws IOException {
        output.append(toXML(input));
    }

    private static void outputKeyValueHeader(final String key, final Appendable output, int indentLevel) throws IOException {
        verifyKeySyntax(key);
        indent(indentLevel, output);
        output.append('<');
        output.append(key);
        output.append('>');
    }

    private static void outputKeyValueFooter(final String key, final Appendable output) throws IOException {
        output.append("</");
        output.append(key);
        output.append(">\n");
    }

    public static void outputKeyValue(final String key, final String value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toXML(value, output);
        outputKeyValueFooter(key, output);
    }

    public static void outputKeyValue(final String key, final Integer value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toXML(value, output);
        outputKeyValueFooter(key, output);
    }

    public static void outputKeyValue(final String key, final Long value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toXML(value, output);
        outputKeyValueFooter(key, output);
    }

    public static void outputKeyValue(final String key, final Double value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toXML(value, output);
        outputKeyValueFooter(key, output);
    }

    public static void outputKeyValue(final String key, final Boolean value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toXML(value, output);
        outputKeyValueFooter(key, output);
    }

    // === instance methods ===

    @Override
    public String getContentType() {
        return "text/xml; charset=UTF-8";
    }

    @Override
    protected void cleanup() {
        objectFinalizers.clear();
    }

    @Override
    protected void addHeader() throws IOException {
        output.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n");
    }

    @Override
    public void addKeyValue(final String key, final String value) throws IOException {
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final Integer value) throws IOException {
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final Long value) throws IOException {
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final Double value) throws IOException {
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final Boolean value) throws IOException {
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final TranslatorAware value) throws IOException {
        beginObject(key); // TODO: find a way to ignore the immediately next object generation, if it's an object of course
        value.translate(this);
        endObject();
    }

    @Override
    public void addKeyValue(final String key, final Iterable<? extends TranslatorAware> value) throws IOException {
        beginArray(key);
        Iterator<? extends TranslatorAware> i = value.iterator();
        while (i.hasNext()) {
            TranslatorAware obj = i.next();
            obj.translate(this);
        }
        endArray();
    }

    @Override
    public void addKeyValue(final String key, final TranslatorAware[] value) throws IOException {
        beginArray(key);
        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                value[i].translate(this);
            }
        }
        endArray();
    }

    @Override
    public void addValue(final String value) throws IOException {
        outputKeyValue("item", value, output, currentIndentLevel);
    }

    @Override
    public void addValue(final Integer value) throws IOException {
        outputKeyValue("item", value, output, currentIndentLevel);
    }

    @Override
    public void addValue(final Long value) throws IOException {
        outputKeyValue("item", value, output, currentIndentLevel);
    }

    @Override
    public void addValue(final Double value) throws IOException {
        outputKeyValue("item", value, output, currentIndentLevel);
    }

    @Override
    public void addValue(final Boolean value) throws IOException {
        outputKeyValue("item", value, output, currentIndentLevel);
    }

    @Override
    public void addValue(final Iterable<? extends TranslatorAware> value) throws IOException {
        addKeyValue("list", value);
    }

    @Override
    public void addValue(final TranslatorAware[] value) throws IOException {
        addKeyValue("list", value);
    }

    @Override
    public void addValue(final String[] value) throws IOException {
        beginArray("list");
        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                outputKeyValue("item", value[i], output, currentIndentLevel);
            }
        }
        endArray();
    }

    @Override
    public void addSequence(final Iterable<? extends TranslatorAware> collection) throws IOException {
        if (collection == null) return;
        for (final TranslatorAware item : collection) item.translate(this);
    }

    @Override
    public void beginObject(final String name) throws IOException {
        verifyKeySyntax(name);
        objectFinalizers.add(name);
        indent();
        output.append('<');
        output.append(name);
        output.append(">\n");
        increaseIndent();
    }

    @Override
    public void endObject() throws IOException {
        decreaseIndent();
        indent();
        output.append("</");
        output.append(objectFinalizers.pop());
        output.append(">\n");
    }

    @Override
    public void beginArray(final String name) throws IOException {
        beginObject(name);
    }

    @Override
    public void endArray() throws IOException {
        endObject();
    }
}
