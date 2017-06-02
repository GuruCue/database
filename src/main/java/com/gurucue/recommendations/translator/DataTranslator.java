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

/**
 * Base class for RESTful webservices' output translation.
 * A RESTful webservice is supposed to produce an object that
 * implements the TranslatorAware interface, e.g. a RestResponse
 * descendant, and then use one of the DataTranslator descendants
 * to translate it into a proper output format.
 * <p>
 * If you're implementing a new DataTranslator descendant, then
 * be sure to also add it to {@link #forFormat(String)}.
 * @see TranslatorAware
 */
public abstract class DataTranslator {
    public static final String INDENT_UNIT = "    ";
    protected Appendable output;
    protected int currentIndentLevel;
    protected TranslatorAware translatedObject;

    public static void indent(int level, final Appendable output) throws IOException {
        while (level > 0) {
            output.append(INDENT_UNIT);
            level--;
        }
    }

    public static DataTranslator forFormat(final String format) {
        if (null == format) throw new IllegalArgumentException("Null format string given");
        if ("json".equals(format)) {
            return new JSONTranslator();
        }
        else if ("xml".equals(format)) {
            return new XMLTranslator();
        }
        else {
            throw new IllegalArgumentException("Unknown output format: " + format);
        }
    }

    protected void indent() throws IOException {
        indent(currentIndentLevel, output);
    }

    protected void addHeader() throws IOException {
        // add output document header
    }

    protected void addFooter() throws IOException {
        // add output document footer
    }

    protected void cleanup() {
        // cleanup after translation
    }

    protected void internalTranslate(final TranslatorAware object, final Appendable output) throws IOException {
        this.output = output;
        this.currentIndentLevel = 0;
        this.translatedObject = object;
        try {
            addHeader();
            object.translate(this);
            addFooter();
        }
        finally {
            this.output = null;
            this.translatedObject = null;
            cleanup();
        }
    }

    public String translate(final TranslatorAware object) {
        StringBuilder sb = new StringBuilder(200); // a bit bigger initial capacity than default
        try {
            internalTranslate(object, sb);
        } catch (IOException e) {} // this exception is not possible with StringBuilder
        return sb.toString();
    }

    public void increaseIndent() {
        currentIndentLevel++;
    }

    public void decreaseIndent() {
        if (currentIndentLevel > 0) {
            currentIndentLevel--;
        }
    }

    abstract String getContentType();
    public abstract void addKeyValue(final String key, final String value) throws IOException;
    public abstract void addKeyValue(final String key, final Integer value) throws IOException;
    public abstract void addKeyValue(final String key, final Long value) throws IOException;
    public abstract void addKeyValue(final String key, final Double value) throws IOException;
    public abstract void addKeyValue(final String key, final Boolean value) throws IOException;
    public abstract void addKeyValue(final String key, final TranslatorAware value) throws IOException;
    public abstract void addKeyValue(final String key, final Iterable<? extends TranslatorAware> value) throws IOException;
    public abstract void addKeyValue(final String key, final TranslatorAware[] value) throws IOException;
    public abstract void addValue(final String value) throws IOException;
    public abstract void addValue(final Integer value) throws IOException;
    public abstract void addValue(final Long value) throws IOException;
    public abstract void addValue(final Double value) throws IOException;
    public abstract void addValue(final Boolean value) throws IOException;
    public abstract void addValue(final Iterable<? extends TranslatorAware> value) throws IOException;
    public abstract void addValue(final TranslatorAware[] value) throws IOException;
    public abstract void addValue(final String[] value) throws IOException;
    public abstract void addSequence(final Iterable<? extends TranslatorAware> collection) throws IOException;
    public abstract void beginObject(final String name) throws IOException;
    public abstract void endObject() throws IOException;
    public abstract void beginArray(final String name) throws IOException;
    public abstract void endArray() throws IOException;
}
