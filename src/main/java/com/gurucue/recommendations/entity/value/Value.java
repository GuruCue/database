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
package com.gurucue.recommendations.entity.value;

import com.google.common.collect.ImmutableMap;
import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.ResponseStatus;
import com.gurucue.recommendations.data.AttributeCodes;
import com.gurucue.recommendations.data.DataProvider;
import com.gurucue.recommendations.data.LanguageCodes;
import com.gurucue.recommendations.entity.Attribute;
import com.gurucue.recommendations.entity.Language;
import com.gurucue.recommendations.tokenizer.*;
import com.gurucue.recommendations.type.ValueType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Abstracts an attribute value of a product. Also used in the "related" product field.
 */
public abstract class Value {
    private static final MultiValue EMPTY_VALUES = new MultiValue(new Value[0]);

    public final ValueType valueType;
    public final boolean isArray;

    public Value(final ValueType valueType, final boolean isArray) {
        this.valueType = valueType;
        this.isArray = isArray;
    }

    /**
     * Convert the value to JSON, to be used for storing it to the database.
     *
     * @param output where to output JSON
     */
    public abstract void toJson(final StringBuilder output);

    public abstract boolean matches(final Value value);
    public abstract Value replace(Value newValue);

    public abstract boolean asBoolean();
    public abstract boolean[] asBooleans();
    public abstract long asInteger();
    public abstract long[] asIntegers();
    public abstract double asFloat();
    public abstract double[] asFloats();
    public abstract String asString();
    public abstract String[] asStrings();
    public abstract TimestampIntervalValue asTimestampInterval();
    public abstract TimestampIntervalValue[] asTimestampIntervals();
    public abstract TranslatableValue asTranslatable();
    public abstract TranslatableValue[] asTranslatables();


    /* Static methods to decode JSON values from the database.
     * API JSON requests go through parsers in the web service.
     */

    private static final String parseLogPrefix = "\nValue.parse(): ";

    public static final ImmutableMap<Attribute, Value> parse(final String jsonValues, final DataProvider provider, final Appendable log) throws ResponseException {
        final AttributeCodes attributeCodes = provider.getAttributeCodes();
        final LanguageCodes languageCodes = provider.getLanguageCodes();
        final Tokenizer tokenizer = new JSONTokenizer(jsonValues);
        Token rootToken = tokenizer.nextToken();
        if (!(rootToken instanceof MapToken))
            throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: \"attributes\" does not represent a map");
        final MapToken rootMapToken = (MapToken) rootToken;
        final ImmutableMap.Builder<Attribute, Value> builder = ImmutableMap.builder();

        // parse the members
        while (rootMapToken.hasNext()) {
            final Token subtoken = rootMapToken.next();
            final String subtokenName = subtoken.getName();
//            if (null == subtokenName) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: null attribute name");
            if (null == subtokenName) {
                // ignore null attribute names, although this state is probably impossible in JSON
                appendLog(log, parseLogPrefix);
                appendLog(log, "null attribute name at line ");
                appendLog(log, tokenizer.getLineNumber());
                appendLog(log, " column ");
                appendLog(log, tokenizer.getColumnNumber());
                continue;
            }
            if (subtoken.isNull()) {
                // ignore attributes with null values, they won't be written out if this gets updated
                appendLog(log, parseLogPrefix);
                appendLog(log, "null value for attribute ");
                appendLog(log, subtokenName);
                appendLog(log, " at line ");
                appendLog(log, tokenizer.getLineNumber());
                appendLog(log, " column ");
                appendLog(log, tokenizer.getColumnNumber());
                continue;
            }

            final Attribute attribute = attributeCodes.byIdentifier(subtokenName);
            final boolean isList;
            final Boolean isTranslatable;
            final ValueType valueType;
            if (attribute == null) {
                // we don't have such an attribute, so run in compatibility mode: decode it as whatever it is
                appendLog(log, parseLogPrefix);
                appendLog(log, "unknown attribute \"");
                appendLog(log, subtokenName);
                appendLog(log, "\" at line ");
                appendLog(log, tokenizer.getLineNumber());
                appendLog(log, " column ");
                appendLog(log, tokenizer.getColumnNumber());
                appendLog(log, ", skipping");
                continue;
/*                isList = subtoken instanceof ListToken;
                isTranslatable = null;
                valueType = null;*/
            }
            else {
                isList = attribute.getIsMultivalue();
                isTranslatable = attribute.getIsTranslatable();
                valueType = attribute.getValueType();
            }

            // convert the value where needed between an array and a primitive value
            try {
                final Value value;
                if (isList) {
                    if (subtoken instanceof ListToken) {
                        final MultiValue multiValue = parseListOfValues((ListToken) subtoken, valueType, isTranslatable, languageCodes, log);
                        if (multiValue.values.length == 0) {
                            // empty list, skip the attribute
                            appendLog(log, parseLogPrefix);
                            appendLog(log, "found an empty list of values for attribute \"");
                            appendLog(log, subtokenName);
                            appendLog(log, "\", ignoring the attribute");
                            continue;
                        }
                        value = multiValue;
                    } else {
                        appendLog(log, parseLogPrefix);
                        appendLog(log, "attribute \"");
                        appendLog(log, subtokenName);
                        appendLog(log, "\" is defined as multi-value, but only a single value provided; converting it to a single-item list");
                        value = new MultiValue(new Value[]{parseSingleValue(subtoken, valueType, isTranslatable, languageCodes, log)});
                    }
                } else {
                    if (subtoken instanceof ListToken) {
                        appendLog(log, parseLogPrefix);
                        appendLog(log, "attribute \"");
                        appendLog(log, subtokenName);
                        appendLog(log, "\" is defined as single-value, but a list of values is provided; using only the first item in the list");
                        final MultiValue multiValue = parseListOfValues((ListToken) subtoken, valueType, isTranslatable, languageCodes, log);
                        if (multiValue.values.length == 0) {
                            // empty list, skip the attribute
                            appendLog(log, parseLogPrefix);
                            appendLog(log, "found an empty list of values for attribute \"");
                            appendLog(log, subtokenName);
                            appendLog(log, "\", ignoring the attribute");
                            continue;
                        }
                        value = multiValue.values[0];
                    } else {
                        value = parseSingleValue(subtoken, valueType, isTranslatable, languageCodes, log);
                    }
                }

                builder.put(attribute, value);
            }
            catch (Exception e) {
                // skip this attribute due to a parse error
                appendLog(log, parseLogPrefix);
                appendLog(log, "Exception ");
                appendLog(log, e.getClass().getCanonicalName());
                appendLog(log, " while decoding attribute \"");
                appendLog(log, subtokenName);
                appendLog(log, "\", skipping it: ");
                appendLog(log, e.toString());
                appendLog(log, "\n");
                logException(e, log);
            }
        }

        return builder.build();
    }

    private static final String parseListOfValuesLogPrefix = "\nValue.parseListOfValues(): ";

    public static final MultiValue parseListOfValues(final ListToken listToken, final ValueType valueType, final Boolean isTranslatable, final LanguageCodes languageCodes, final Appendable log) throws ResponseException {
        final List<Value> values = new ArrayList<>();
        Value firstValue = null;
        int i = 0;
        while (listToken.hasNext()) {
            i++;
            try {
                firstValue = parseSingleValue(listToken.next(), valueType, isTranslatable, languageCodes, log);
                break;
            }
            catch (Exception e) {
                // ignore and try the next value
                appendLog(log, parseListOfValuesLogPrefix);
                appendLog(log, "Exception ");
                appendLog(log, e.getClass().getCanonicalName());
                appendLog(log, " while parsing ");
                appendLog(log, i);
                appendLog(log, ". value in the list as the first value, attempting to parse the next item as first: ");
                appendLog(log, e.toString());
                appendLog(log, "\n");
                logException(e, log);
            }
        }
        if (firstValue == null) return EMPTY_VALUES;
        values.add(firstValue);

        final ValueType valueTypeOfRest;
        final Boolean isTranslatableRest;
        if (valueType == null) valueTypeOfRest = firstValue.valueType;
        else valueTypeOfRest = valueType;
        if (isTranslatable == null) isTranslatableRest = firstValue instanceof TranslatableValue;
        else isTranslatableRest = isTranslatable;

        while (listToken.hasNext()) {
            i++;
            try {
                values.add(parseSingleValue(listToken.next(), valueTypeOfRest, isTranslatableRest, languageCodes, log));
            }
            catch (ResponseException e) {
                // ignore
                appendLog(log, parseListOfValuesLogPrefix);
                appendLog(log, "Exception ");
                appendLog(log, e.getClass().getCanonicalName());
                appendLog(log, " while parsing ");
                appendLog(log, i);
                appendLog(log, ". value in the list, skipping to the next value: ");
                appendLog(log, e.toString());
                appendLog(log, "\n");
                logException(e, log);
            }
        }
        return new MultiValue(values.toArray(new Value[values.size()]));
    }

    private static final String parseSingleValueLogPrefix = "\nValue.parseSingleValue(): ";

    public static final Value parseSingleValue(final Token token, final ValueType valueType, final Boolean isTranslatable, final LanguageCodes languageCodes, final Appendable log) throws ResponseException {
        if (token instanceof ListToken) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: an attribute value can be a primitive value or an object, but not a list");
        if (token instanceof MapToken) {
            final MapToken mapToken = (MapToken)token;
            Long begin = null, end = null;
            PrimitiveToken value = null;
            Language language = null;
            Map<Language, String> translations = null;

            // sieve out the key values from the value object
            while (mapToken.hasNext()) {
                final Token subToken = mapToken.next();
                if ("translations".equals(subToken.getName())) {
                    if (!(subToken instanceof MapToken)) {
                        // translations is a mapping
                        appendLog(log, parseSingleValueLogPrefix);
                        appendLog(log, "\"translations\" is not an object (mapping)");
                        continue;
                    }
                    translations = new HashMap<>();
                    final MapToken translationsToken = (MapToken)subToken;
                    while (translationsToken.hasNext()) {
                        final Token subSubToken = translationsToken.next();
                        if (!(subSubToken instanceof PrimitiveToken)) {
                            appendLog(log, parseSingleValueLogPrefix);
                            appendLog(log, "a translation is not a primitive value");
                            continue;
                        }
                        if (subSubToken.isNull() || (subSubToken.getName() == null)) {
                            appendLog(log, parseSingleValueLogPrefix);
                            appendLog(log, "a translation has a null key");
                            continue;
                        }
                        final Language l = languageCodes.byIso639_2t(subSubToken.getName());
                        if (l == null) {
                            appendLog(log, parseSingleValueLogPrefix);
                            appendLog(log, "unknown ISO639/2t language code, translation discarded: ");
                            appendLog(log, subSubToken.getName());
                        }
                        else {
                            translations.put(l, ((PrimitiveToken) subSubToken).asString());
                        }
                    }
                }
                else {
                    if (!(subToken instanceof PrimitiveToken)) {
                        // a structured value cannot itself be made of sub-structures, only primitive values
                        appendLog(log, parseSingleValueLogPrefix);
                        if (subToken.getName() == null) appendLog(log, "a null key in the value mapping");
                        else {
                            appendLog(log, subToken.getName());
                            appendLog(log, " key in the value mapping does not represent a primitive value");
                        }
                        continue;
                    }
                    switch (subToken.getName()) {
                        case "value":
                            value = (PrimitiveToken)subToken;
                            break;
                        case "language":
                            language = languageCodes.byIso639_2t(((PrimitiveToken)subToken).asString());
                            if (language == null) {
                                appendLog(log, parseSingleValueLogPrefix);
                                appendLog(log, "unknown ISO639/2t language code, using \"unk\": ");
                                appendLog(log, ((PrimitiveToken) subToken).asString());
                                language = languageCodes.unknown;
                            }
                            break;
                        case "begin":
                            begin = ((PrimitiveToken) subToken).asLong();
                            break;
                        case "end":
                            end = ((PrimitiveToken) subToken).asLong();
                            break;
                        default:
                            appendLog(log, parseSingleValueLogPrefix);
                            if (subToken.getName() == null) appendLog(log, "a null key in the value mapping");
                            else {
                                appendLog(log, "unknown key in the value mapping: ");
                                appendLog(log, subToken.getName());
                            }
                    }
                }
            }

            // guess the type
            if ((valueType == null) || (isTranslatable == null)) {
                // timestamp interval
                if ((begin != null) && (end != null)) return TimestampIntervalValue.fromSeconds(begin, end);
                if (value.isNull()) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: a value cannot be null");
                if (value == null) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: an object value did not contain the \"value\" key");
                // translatable value
                if ((translations != null) || (language != null)) {
                    final String stringValue = value.asString();
                    final ImmutableMap<Language, String> immutableTranslations;
                    if (translations == null) {
                        immutableTranslations = ImmutableMap.of(language, stringValue);
                    }
                    else {
                        if (language == null) {
                            // try to find the language of the original value
                            for (final Map.Entry<Language, String> entry : translations.entrySet()) {
                                if (stringValue.equals(entry.getValue())) {
                                    language = entry.getKey();
                                    break;
                                }
                            }
                            if (language == null) {
                                language = languageCodes.unknown;
                            }
                        }
                        translations.put(language, stringValue);
                        immutableTranslations = ImmutableMap.copyOf(translations);
                    }
                    return new TranslatableValue(stringValue, language, immutableTranslations);
                }
                // primitive value
                if (value instanceof LongToken) return new LongValue(value.asLong(), false);
                if (value instanceof BooleanToken) return new BooleanValue(value.asBoolean());
                if (value instanceof DoubleToken) return new FloatValue(value.asDouble());
                return new StringValue(value.asString());
            }

            // a translatable value
            if (isTranslatable) {
                if (value == null) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: a translatable value without a \"value\" key");
                final String stringValue = value.asString();
                final ImmutableMap<Language, String> immutableTranslations;
                if (translations == null) {
                    appendLog(log, parseSingleValueLogPrefix);
                    if (language == null) {
                        language = languageCodes.unknown;
                        appendLog(log, "translatable value without translations and language");
                    }
                    else appendLog(log, "translatable value without translations");
                    immutableTranslations = ImmutableMap.of(language, stringValue);
                }
                else {
                    if (language == null) {
                        appendLog(log, parseSingleValueLogPrefix);
                        appendLog(log, "translatable value without a language");
                        for (final Map.Entry<Language, String> entry : translations.entrySet()) {
                            // try to find the language of the original value
                            if (stringValue.equals(entry.getValue())) {
                                language = entry.getKey();
                                break;
                            }
                        }
                        if (language == null) {
                            language = languageCodes.unknown;
                        }
                    }
                    translations.put(language, stringValue);
                    immutableTranslations = ImmutableMap.copyOf(translations);
                }
                return new TranslatableValue(stringValue, language, immutableTranslations);
            }

            // a primitive value
            if (valueType == ValueType.TIMESTAMP_INTERVAL) {
                if ((begin != null) && (end != null)) return TimestampIntervalValue.fromSeconds(begin, end);
                throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: the timestamp interval did not have both \"begin\" and \"end\" set");
            }
            return parseAsTypedPrimitiveValue(value, valueType);
        }
        else if (token instanceof PrimitiveToken) {
            final PrimitiveToken primitiveToken = (PrimitiveToken) token;
            if ((isTranslatable == null) || (!isTranslatable.booleanValue())) {
                return parseAsTypedPrimitiveValue(primitiveToken, valueType == null ? ValueType.STRING : valueType);
            }
            else {
                appendLog(log, parseSingleValueLogPrefix);
                appendLog(log, "a translatable value is specified as a primitive value, converting it with the unknown language");
                final String value = primitiveToken.asString();
                return new TranslatableValue(value, languageCodes.unknown, ImmutableMap.of(languageCodes.unknown, value));
            }
        }
        throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: unknown token type");
    }

    private static Value parseAsTypedPrimitiveValue(final PrimitiveToken token, final ValueType valueType) throws ResponseException {
        if ((token == null) || token.isNull()) throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: an object value without the \"value\" set");
        switch (valueType) {
            case STRING:
                return new StringValue(token.asString());
            case BOOLEAN:
                return new BooleanValue(token.asBoolean());
            case FLOAT:
                return new FloatValue(token.asDouble());
            case INTEGER:
                return new LongValue(token.asLong(), false);
            case TIMESTAMP:
                return new LongValue(token.asLong(), true);
            default:
                throw new ResponseException(ResponseStatus.UNKNOWN_ERROR, "Parse error: a primitive value can only be an integer, a string, a boolean, a float, or a timestamp");
        }
    }


    public static final void escapeJsonCodePoint(final int codePoint, final StringBuilder output) {
        switch (codePoint) {
            case 0x08: // backspace
                output.append("\\b");
                return;
            case 0x0c: // form feed
                output.append("\\f");
                return;
            case 0x0a: // newline
                output.append("\\n");
                return;
            case 0x0d: // carriage return
                output.append("\\r");
                return;
            case 0x09: // horizontal tab
                output.append("\\t");
                return;
            case 0x22: // quotation mark
                output.append("\\\"");
                return;
            case 0x5c: // backslash
                output.append("\\\\");
                return;
        }
        if ((codePoint >= 0x20) && (codePoint < 0x7f)) { // printable range
            output.appendCodePoint(codePoint);
            return;
        }
        output.append(String.format("\\u%04x", codePoint));
    }

    public static final void escapeJson(final String input, final StringBuilder output) {
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
                escapeJsonCodePoint(codePoint, output);
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


    private static void logException(final Exception e, final Appendable log) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        appendLog(log, sw.toString());
    }
    
    private static void appendLog(final Appendable log, final String stuff) {
        try {
            log.append(stuff);
        }
        catch (Exception e) {
            // ignore IO and any other exception
        }
    }

    private static void appendLog(final Appendable log, final int stuff) {
        try {
            log.append(Integer.toString(stuff, 10));
        }
        catch (Exception e) {
            // ignore IO and any other exception
        }
    }
}
