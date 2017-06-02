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
 * DataTranslator class for translating to JSON format.
 * @see DataTranslator
 */
public final class JSONTranslator extends DataTranslator {
    private Stack<Boolean> commaStack = new Stack<Boolean>();
    private boolean needsComma;

    public JSONTranslator() {
        
    }

    // === class methods ===

    public static String escapeCodePoint(final int codePoint) {
        switch (codePoint) {
        case 0x08: // backspace
            return "\\b";
        case 0x0c: // form feed
            return "\\f";
        case 0x0a: // newline
            return "\\n";
        case 0x0d: // carriage return
            return "\\r";
        case 0x09: // horizontal tab
            return "\\t";
        case 0x22: // quotation mark
            return "\\\"";
        case 0x5c: // backslash
            return "\\\\";
        }
        if ((codePoint >= 0x20) && (codePoint < 0x7f)) { // printable range
            return new String(new int[] {codePoint}, 0, 1);
        }
        return String.format("\\u%04x", codePoint);
    }

    public static void escape(final String input, final Appendable output) throws IOException {
        if (null == input) {
            throw new IllegalArgumentException("Cannot escape a null String to JSON");
        }
        final int len = input.length();
        int i = 0;
        int substringStart = 0;
        while (i < len) {
            int codePoint = input.codePointAt(i);
            if ((codePoint < 0x20) || (0x22 == codePoint) || (0x5c == codePoint) || (codePoint > 0x7e)) {
                // unprintable range
                if (substringStart < i) {
                    output.append(input, substringStart, i);
                }
                output.append(escapeCodePoint(codePoint));
                if (codePoint > 0xFFFF) i += 2; // high surrogate + low surrogate
                else i++;
                substringStart = i;
            }
            else i++; // printable range
        }
        if (substringStart < len) {
            output.append(input, substringStart, len);
        }
    }

    public static String escape(final String input) {
        final StringBuilder sb = new StringBuilder((int)((float)input.length() * 1.05)); // arbitrarily sensible initial capacity
        try {
            escape(input, sb);
        } catch (IOException e) {} // this doesn't happen
        return sb.toString();
    }

    public static String toJSON(final String input) {
        final StringBuilder sb = new StringBuilder((int)((float)input.length() * 1.05)); // arbitrarily sensible initial capacity
        try {
            toJSON(input, sb);
        } catch (IOException e) {} // this doesn't happen
        return sb.toString();
    }

    public static void toJSON(final String input, final Appendable output) throws IOException {
        if (null == input) {
            output.append("null");
        }
        else {
            output.append('"');
            escape(input, output);
            output.append('"');
        }
    }

    public static String toJSON(final Integer input) {
        return null == input ? "null" : input.toString();
    }

    public static void toJSON(final Integer input, final Appendable output) throws IOException {
        output.append(toJSON(input));
    }

    public static String toJSON(final Long input) {
        return null == input ? "null" : input.toString();
    }

    public static void toJSON(final Long input, final Appendable output) throws IOException {
        output.append(toJSON(input));
    }

    public static String toJSON(final Double input) {
        return null == input ? "null" : input.toString();
    }

    public static void toJSON(final Double input, final Appendable output) throws IOException {
        output.append(toJSON(input));
    }

    public static String toJSON(final Boolean input) {
        if (null == input) return "null";
        if (input.booleanValue()) return "true";
        return "false";
    }

    public static void toJSON(final Boolean input, final Appendable output) throws IOException {
        output.append(toJSON(input));
    }

    private static void outputKeyValueHeader(final String key, final Appendable output, int indentLevel) throws IOException {
        output.append("\n");
        indent(indentLevel, output);
        toJSON(key, output);
        output.append(": ");
    }

    private static void outputKeyValueFooter(final Appendable output) throws IOException {
        //output.append("\n");
    }

    public static void outputKeyValue(final String key, final String value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toJSON(value, output);
        outputKeyValueFooter(output);
    }

    public static void outputKeyValue(final String key, final Integer value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toJSON(value, output);
        outputKeyValueFooter(output);
    }

    public static void outputKeyValue(final String key, final Long value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toJSON(value, output);
        outputKeyValueFooter(output);
    }

    public static void outputKeyValue(final String key, final Double value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toJSON(value, output);
        outputKeyValueFooter(output);
    }

    public static void outputKeyValue(final String key, final Boolean value, final Appendable output, int indentLevel) throws IOException {
        outputKeyValueHeader(key, output, indentLevel);
        toJSON(value, output);
        outputKeyValueFooter(output);
    }

    // === instance methods ===

    @Override
    public String getContentType() {
        return "text/json; charset=UTF-8";
    }

    private void emitCommaIfNeeded() throws IOException {
        if (needsComma) {
            output.append(',');
        }
        else {
            needsComma = true;
        }
    }

    @Override
    public void addKeyValue(final String key, final String value) throws IOException {
        emitCommaIfNeeded();
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final Integer value) throws IOException {
        emitCommaIfNeeded();
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final Long value) throws IOException {
        emitCommaIfNeeded();
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final Double value) throws IOException {
        emitCommaIfNeeded();
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final Boolean value) throws IOException {
        emitCommaIfNeeded();
        outputKeyValue(key, value, output, currentIndentLevel);
    }

    @Override
    public void addKeyValue(final String key, final TranslatorAware value) throws IOException {
        emitCommaIfNeeded();
        outputKeyValueHeader(key, output, currentIndentLevel);
        value.translate(this);
    }

    @Override
    public void addKeyValue(final String key, final Iterable<? extends TranslatorAware> value) throws IOException {
        emitCommaIfNeeded();
        outputKeyValueHeader(key, output, currentIndentLevel);
        addValue(value);
    }

    @Override
    public void addKeyValue(final String key, final TranslatorAware[] value) throws IOException {
        emitCommaIfNeeded();
        outputKeyValueHeader(key, output, currentIndentLevel);
        addValue(value);
    }

    @Override
    public void addValue(final String value) throws IOException {
        toJSON(value, output);
    }

    @Override
    public void addValue(final Integer value) throws IOException {
        toJSON(value, output);
    }

    @Override
    public void addValue(final Long value) throws IOException {
        toJSON(value, output);
    }

    @Override
    public void addValue(final Double value) throws IOException {
        toJSON(value, output);
    }

    @Override
    public void addValue(final Boolean value) throws IOException {
        toJSON(value, output);
    }

    @Override
    public void addValue(final Iterable<? extends TranslatorAware> value) throws IOException {
        beginArray(null);
        addSequence(value);
        endArray();
    }

    @Override
    public void addValue(final TranslatorAware[] value) throws IOException {
        beginArray(null);
        if ((value != null) && (value.length > 0)) {
            output.append("\n");
            indent();
            value[0].translate(this);
            for (int i = 1; i < value.length; i++) {
                output.append(",\n");
                indent();
                value[i].translate(this);
            }
        }
        endArray();
    }

    @Override
    public void addValue(final String[] value) throws IOException {
        beginArray(null);
        if ((value != null) && (value.length > 0)) {
            output.append("\n");
            indent();
            toJSON(value[0], output);
            for (int i = 1; i < value.length; i++) {
                output.append(",\n");
                indent();
                toJSON(value[i], output);
            }
        }
        endArray();
    }

    @Override
    public void addSequence(final Iterable<? extends TranslatorAware> collection) throws IOException {
        Iterator<? extends TranslatorAware> i = collection.iterator();
        if (i.hasNext()) {
            TranslatorAware obj = i.next();
            output.append("\n");
            indent();
            obj.translate(this);
            while (i.hasNext()) {
                obj = i.next();
                output.append(",\n");
                indent();
                obj.translate(this);
            }
        }
    }

    @Override
    public void beginObject(final String name) throws IOException {
        output.append("{");
        increaseIndent();
        commaStack.push(needsComma);
        needsComma = false;
    }

    @Override
    public void endObject() throws IOException {
        output.append('\n');
        decreaseIndent();
        indent();
        output.append('}');
        needsComma = commaStack.pop();
    }

    @Override
    public void beginArray(final String name) throws IOException {
        output.append("[");
        increaseIndent();
        commaStack.push(needsComma);
        needsComma = false;
    }

    @Override
    public void endArray() throws IOException {
        output.append('\n');
        decreaseIndent();
        indent();
        output.append(']');
        needsComma = commaStack.pop();
    }
}
