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
import com.gurucue.recommendations.data.DataManager;
import com.gurucue.recommendations.entity.Language;
import com.gurucue.recommendations.type.ValueType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class TranslatableValue extends Value {
    public final String value;
    public final Language language;
    public final ImmutableMap<Language, String> translations;

    public TranslatableValue(final String value, final Language language, final ImmutableMap<Language, String> translations) {
        super(ValueType.STRING, false);
        if (value == null) throw new NullPointerException("Original value cannot be null");
        if (language == null) throw new NullPointerException("Original language cannot be null");
        this.value = value;
        this.language = language;
        if (!value.equals(translations.get(language))) {
            final ImmutableMap.Builder<Language, String> builder = ImmutableMap.builder();
            final long originalLanguageId = language.getId().longValue();
            for (final Map.Entry<Language, String> entry : translations.entrySet()) {
                if (entry.getKey().getId().longValue() != originalLanguageId) {
                    builder.put(entry.getKey(), entry.getValue());
                }
            }
            builder.put(language, value);
            this.translations = builder.build();
        }
        else this.translations = translations;
    }

    public TranslatableValue(final String value, final Language language) {
        super(ValueType.STRING, false);
        this.value = value == null ? "" : value;
        this.language = language;
        this.translations = ImmutableMap.of(this.language, this.value);
    }

    @Override
    public void toJson(final StringBuilder output) {
        output.append("{\"value\":\"");
        escapeJson(value, output);
        output.append("\",\"language\":\"");
        escapeJson(language.getIso639_2t(), output);
        output.append("\",\"translations\":{");
        final Iterator<Map.Entry<Language, String>> iterator = translations.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Language, String> entry = iterator.next();
            output.append("\"");
            escapeJson(entry.getKey().getIso639_2t(), output);
            output.append("\":\"");
            escapeJson(entry.getValue(), output);
            output.append("\"");
            while (iterator.hasNext()) {
                entry = iterator.next();
                output.append(",\"");
                escapeJson(entry.getKey().getIso639_2t(), output);
                output.append("\":\"");
                escapeJson(entry.getValue(), output);
                output.append("\"");
            }
        }
        output.append("}}");
    }

    @Override
    public boolean matches(final Value value) { // TODO: if any translation matches, then whe whole matches
        if (value instanceof TranslatableValue) {
            // only match translations, the original value must already be present among them
            final Map<Language, String> otherTranslations = ((TranslatableValue)value).translations;
/*            if (translations.size() != otherTranslations.size()) return false;
            for (final Map.Entry<Language, String> entry : otherTranslations.entrySet()) {
                final String translation = translations.get(entry.getKey());
                if (translation == null) return false;
                if (!translation.equals(entry.getValue())) return false;
            }
            return true;*/
            for (final Map.Entry<Language, String> entry : otherTranslations.entrySet()) {
                final String translation = translations.get(entry.getKey());
                if (translation == null) continue;
                if (translation.equals(entry.getValue())) return true;
            }
            return false;
        }
        return false; // a translatable value doesn't match anything else because one translation may match, but others won't
    }

    @Override
    public TranslatableValue replace(final Value newValue) {
        final TranslatableValue newTranslatableValue = newValue.asTranslatable();
        if ((newTranslatableValue == null) || newTranslatableValue.equals(null)) return newTranslatableValue;
        final ImmutableMap.Builder<Language, String> builder = ImmutableMap.builder();
        final long unknownId = DataManager.getLanguageCodes().idForUknown;
        final long originalId = newTranslatableValue.language.getId().longValue();

        if ((language.getId().longValue() == originalId) && value.equals(newTranslatableValue.value)) {
            // The new value is the same, handle the translation differences.
            final ImmutableMap<Language, String> newTranslations = newTranslatableValue.translations;
            // Step 1: process all existing translations and see if they need to be modified
            for (final Map.Entry<Language, String> entry : translations.entrySet()) {
                final Language trLanguage = entry.getKey();
                if (trLanguage == null) continue;
                final long trId = trLanguage.getId().longValue();
                if ((trId == unknownId) || (originalId == trId)) continue;
                final String trValue = entry.getValue();
                if ((trValue == null) || (trValue.length() == 0)) continue;

                if (newTranslations.containsKey(trLanguage)) {
                    final String newTrValue = newTranslations.get(trLanguage);
                    if ((newTrValue == null) || (newTrValue.length() == 0)) continue; // translation deletion
                    builder.put(trLanguage, newTrValue);
                }
                else {
                    builder.put(trLanguage, trValue);
                }
            }
            // Step 2: process all new translations that are not present in the existing translations
            for (final Map.Entry<Language, String> entry : newTranslations.entrySet()) {
                final String trValue = entry.getValue();
                if ((trValue == null) || (trValue.length() == 0)) continue;
                final Language trLanguage = entry.getKey();
                if (trLanguage == null) continue;
                final long trId = trLanguage.getId().longValue();
                if ((trId == unknownId) || (originalId == trId)) continue;
                if (!translations.containsKey(trLanguage)) {
                    builder.put(trLanguage, trValue);
                }
            }
            builder.put(language, value);
            return new TranslatableValue(value, language, builder.build());
        }
        else {
            // Not the same value, discard previous translations.
            // Remove null translations from the given value.
            for (final Map.Entry<Language, String> entry : newTranslatableValue.translations.entrySet()) {
                final String trValue = entry.getValue();
                if ((trValue == null) || (trValue.length() == 0)) continue;
                final Language trLanguage = entry.getKey();
                if (trLanguage == null) continue;
                final long trId = trLanguage.getId().longValue();
                if ((trId == unknownId) || (originalId == trId)) continue;
                builder.put(trLanguage, trValue);
            }
            builder.put(newTranslatableValue.language, newTranslatableValue.value);
            return new TranslatableValue(newTranslatableValue.value, newTranslatableValue.language, builder.build());
        }
    }

    @Override
    public boolean asBoolean() {
        return "true".equals(value) || "1".equals(value) || "yes".equals(value) || "on".equals(value);
    }

    @Override
    public boolean[] asBooleans() {
        return new boolean[]{asBoolean()};
    }

    @Override
    public long asInteger() {
        try {
            return Long.parseLong(value, 10);
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public long[] asIntegers() {
        return new long[]{asInteger()};
    }

    @Override
    public double asFloat() {
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public double[] asFloats() {
        return new double[]{asFloat()};
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public String[] asStrings() {
        return new String[]{value};
    }

    @Override
    public TimestampIntervalValue asTimestampInterval() {
        final long l = asInteger();
        return TimestampIntervalValue.fromSeconds(l, l);
    }

    @Override
    public TimestampIntervalValue[] asTimestampIntervals() {
        return new TimestampIntervalValue[]{asTimestampInterval()};
    }

    @Override
    public TranslatableValue asTranslatable() {
        return this;
    }

    @Override
    public TranslatableValue[] asTranslatables() {
        return new TranslatableValue[]{this};
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int r = 17;
        // translations are not ordered, so first order them the way that the result will always be the same no matter how the translations are laid out
        final int[] hashes = new int[translations.size()];
        int i = 0;
        for (Map.Entry<Language, String> entry : translations.entrySet()) {
            // ImmutableMap does not permit nulls as values or keys
            final Long languageId = entry.getKey().getId();
            hashes[i++] = 31 * languageId.hashCode() + entry.getValue().hashCode();
        }
        Arrays.sort(hashes);
        for (i = hashes.length - 1; i >= 0; i--) r = 31 * r + hashes[i];
        r = 31 * r + language.getId().hashCode();
        r = 31 * r + value.hashCode();
        return r;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other instanceof TranslatableValue) {
            final TranslatableValue otherValue = (TranslatableValue)other;
            if (otherValue.translations.size() != translations.size()) return false;
            if (otherValue.language.getId().longValue() != language.getId().longValue()) return false;
            if (!otherValue.value.equals(value)) return false;
            for (final Map.Entry<Language, String> entry : otherValue.translations.entrySet()) {
                final String translation = translations.get(entry.getKey());
                if ((translation == null) || !translation.equals(entry.getValue())) return false;
            }
            return true;
        }
        return false;
    }

    public void toString(final StringBuilder output) {
        output.append("value=");
        if (value == null) output.append("(null)");
        else {
            output.append("\"").append(value.replace("\"", "\\\"")).append("\"");
        }
        output.append(", language=");
        if (language == null) output.append("(null)");
        else output.append(language.getIso639_2t());
        output.append(", translations={");
        final Iterator<Map.Entry<Language, String>> entryIterator = translations.entrySet().iterator();
        if (entryIterator.hasNext()) {
            final Map.Entry<Language, String> firstEntry = entryIterator.next();
            final Language firstLanguage = firstEntry.getKey();
            final String firstValue = firstEntry.getValue();
            if (firstLanguage == null) output.append("(null)");
            else output.append(firstLanguage.getIso639_2t());
            if (firstValue == null) output.append("=>(null)");
            else output.append("=>\"").append(firstValue.replace("\"", "\\\"")).append("\"");
            while (entryIterator.hasNext()) {
                final Map.Entry<Language, String> nextEntry = entryIterator.next();
                final Language nextLanguage = nextEntry.getKey();
                final String nextValue = nextEntry.getValue();
                if (nextLanguage == null) output.append(", (null)");
                else output.append(", ").append(nextLanguage.getIso639_2t());
                if (nextValue == null) output.append("=>(null)");
                else output.append("=>\"").append(nextValue.replace("\"", "\\\"")).append("\"");
            }
        }
        output.append("}");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(256);
        sb.append("TranslatableValue(valueType=").append(valueType).append(", ");
        toString(sb);
        return sb.append(")").toString();
    }

    /**
     * Constructs a new TranslatableValue by modifying or introducing a single translation.
     *
     * @param language the language for which to introduce a new translation or modify the existing translation
     * @param value the new translation value
     * @return the new <code>TranslatableValue</code> instance having the modification applied
     */
    public TranslatableValue set(final Language language, final String value) {
        if (value == null) throw new NullPointerException("Cannot set a null value");
        if (language == null) throw new NullPointerException("Cannot set a null language");
        final long languageId = language.getId().longValue();
        final ImmutableMap.Builder<Language, String> builder = ImmutableMap.builder();
        for (final Map.Entry<Language, String> entry : translations.entrySet()) {
            if (entry.getKey().getId().longValue() != languageId) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        builder.put(language, value);
        final ImmutableMap<Language, String> translations = builder.build();
        if (languageId == this.language.getId().longValue()) return new TranslatableValue(value, language, translations);
        return new TranslatableValue(this.value, this.language, translations);
    }

    /**
     * Merge translations of this and the other translatable value, reconciling any differences.
     * @param other the <code>TranslatableValue</code> instance to merge with
     * @param reconcileLog the log output, where any problems with merging are logged
     * @return the result of merging with the specified <code>TranslatableValue</code> instance
     */
    public TranslatableValue merge(final TranslatableValue other, final StringBuilder reconcileLog) {
        final Map<Language, String> otherUnique = new HashMap<>(other.translations);
        otherUnique.put(other.language, other.value); // the original value should always be among translation, this is just to make sure of it
        final Map<Language, String> thisUnique = new HashMap<>(translations.size());
        final Map<Language, String> commonTranslations = new HashMap<>(translations.size() + otherUnique.size()); // upper limit of number of entries
        final Set<Map.Entry<Language, String>> thisTranslations;
        if (translations.containsKey(language)) thisTranslations = translations.entrySet();
        else {
            // the original value should always be among translation, this is just to make sure of it
            final Map<Language, String> someMap = new HashMap<>(translations); // a proxy map, to obtain full set of (language, value) tuples
            someMap.put(language, value);
            thisTranslations = someMap.entrySet();
        }
        int reconcileToThisCount = 0;
        int reconcileToOtherCount = 0;
//        final StringBuilder reconcileLog = new StringBuilder(80 + translations.size() * 50);
        final int beginReconcileLogSize = reconcileLog.length();
        reconcileLog.append("Detected different translation(s) with same language, using longer or other (2nd) values:\n");
        final int startingReconcileLogSize = reconcileLog.length();
        String unkValue = null; // unknown language must be treated differently
        Language unkLanguage = null;
        for (final Map.Entry<Language, String> entry : thisTranslations) {
            final Language language = entry.getKey();
            if (Language.UNKNOWN.equals(language.getIso639_2t())) {
                // handle the translation in unknown language in a specific way after all the other translations have been processed
                unkValue = entry.getValue();
                unkLanguage = language;
                continue;
            }
            final String otherValue = otherUnique.remove(language);
            final String thisValue = entry.getValue();
            if (otherValue == null) thisUnique.put(language, thisValue);
            else if (otherValue.equals(thisValue)) commonTranslations.put(language, thisValue);
            else {
                // reconcile
                reconcileLog.append("  ").append(language.getIso639_2t());
                // first check that no 0xfffd occurs; if it does then choose the one that doesn't have it
                if (thisValue.contains("\uFFFD")) {
                    if (otherValue.contains("\uFFFD")) {
                        // choose the longest one
                        if (thisValue.length() > otherValue.length()) {
                            reconcileToThisCount++;
                            commonTranslations.put(language, thisValue);
                            reconcileLog.append(", using my (1st) value: ");
                        } else {
                            reconcileToOtherCount++;
                            commonTranslations.put(language, otherValue);
                            reconcileLog.append(", using other (2nd) value: ");
                        }
                    }
                    else {
                        reconcileToOtherCount++;
                        commonTranslations.put(language, otherValue);
                        reconcileLog.append(", using other (2nd) value: ");
                    }
                }
                else if (otherValue.contains("\uFFFD")) {
                    reconcileToThisCount++;
                    commonTranslations.put(language, thisValue);
                    reconcileLog.append(", using my (1st) value: ");
                }
                else {
                    // choose the longest one
                    if (thisValue.length() > otherValue.length()) {
                        reconcileToThisCount++;
                        commonTranslations.put(language, thisValue);
                        reconcileLog.append(", using my (1st) value: ");
                    } else {
                        reconcileToOtherCount++;
                        commonTranslations.put(language, otherValue);
                        reconcileLog.append(", using other (2nd) value: ");
                    }
                }
                reconcileLog.append(": \"")
                        .append(thisValue)
                        .append("\" != \"")
                        .append(otherValue)
                        .append("\"\n");
            }
        }
        if (reconcileLog.length() == startingReconcileLogSize) reconcileLog.delete(beginReconcileLogSize, startingReconcileLogSize);
        if (unkValue != null) {
            // first check that it's not already among common values, which can occur if a value has been reconciled to the other
            for (final Map.Entry<Language, String> entry : commonTranslations.entrySet()) {
                if (unkValue.equals(entry.getValue())) {
                    reconcileLog.append("Translation in unknown language has been removed because other translation in language ")
                            .append(entry.getKey().getIso639_2t())
                            .append(" has been found with the same value: \"")
                            .append(unkValue)
                            .append("\"\n");
                    unkValue = null;
                    break;
                }
            }
            if (unkValue != null) {
                // now check leftovers among the other values
                for (final Map.Entry<Language, String> entry : otherUnique.entrySet()) {
                    if (unkValue.equals(entry.getValue())) {
                        if (Language.UNKNOWN.equals(entry.getKey().getIso639_2t())) {
                            // the value remains unknown
                        }
                        else {
                            reconcileToOtherCount++;
                            reconcileLog.append("Translation in unknown language matches the other's translation in language ")
                                    .append(entry.getKey().getIso639_2t())
                                    .append(": \"")
                                    .append(unkValue)
                                    .append("\"\n");
                        }
                        otherUnique.remove(entry.getKey());
                        commonTranslations.put(entry.getKey(), entry.getValue());
                        unkValue = null;
                        break;
                    }
                }
                if (unkValue != null) {
                    // give up: add it to the unique values
                    thisUnique.put(unkLanguage, unkValue);
                }
            }
        }

        // whoever invoked the merge must have a reason despite there not being any translations in common, so don't throw an exception because of it
//        if ((commonTranslations.size() - reconcileToThisCount - reconcileToOtherCount) == 0) errorBuilder.append(", title translations have nothing in common");
        if ((commonTranslations.size() - reconcileToThisCount - reconcileToOtherCount) == 0) {
            reconcileLog.append("Reconciliation found no title translations in common\n");
        }

        if (thisUnique.isEmpty()) {
            // return the other, but only if reconciliation didn't prefer anything from us
            if (reconcileToThisCount == 0) {
                if (reconcileLog.length() > 0) {
                    reconcileLog.deleteCharAt(reconcileLog.length() - 1);
//                    log.warn(reconcileLog.substring(0, reconcileLog.length() - 1)); // trim the last newline
                }
                return other;
            }
        }
        else if (otherUnique.isEmpty()) {
            // return this one, but only if reconciliation didn't decide for the other
            if (reconcileToOtherCount == 0) {
                if (reconcileLog.length() > 0) {
                    reconcileLog.deleteCharAt(reconcileLog.length() - 1);
//                    log.warn(reconcileLog.substring(0, reconcileLog.length() - 1)); // trim the last newline
                }
                return this;
            }
        }

        // we can't return this or the other as no one is a subset of the other, we have to finish the merge and create a new matcher
        // prefer the other for the original values and any unknown value
        commonTranslations.putAll(thisUnique);
        final Language unknownLanguage = DataManager.getLanguageCodes().unknown;
        String otherUnknownValue = otherUnique.remove(unknownLanguage);
        if (otherUnknownValue != null) {
            // try to reconcile with an existing value
            for (final Map.Entry<Language, String> entry : commonTranslations.entrySet()) {
                if (otherUnknownValue.equals(entry.getValue())) {
                    reconcileLog.append("Translation in unknown language has been removed because my translation in language ")
                            .append(entry.getKey().getIso639_2t())
                            .append(" has been found with the same value: \"")
                            .append(unkValue)
                            .append("\"\n");
                    otherUnknownValue = null;
                    break;
                }
            }
            if (otherUnknownValue != null) {
                // give up
                commonTranslations.put(unknownLanguage, otherUnknownValue);
            }
        }
        commonTranslations.putAll(otherUnique);

        if (reconcileLog.length() > 0) {
            reconcileLog.deleteCharAt(reconcileLog.length() - 1);
//            log.warn(reconcileLog.substring(0, reconcileLog.length() - 1)); // trim the last newline
        }

        return new TranslatableValue(commonTranslations.get(other.language), other.language, ImmutableMap.copyOf(commonTranslations));
    }
}
