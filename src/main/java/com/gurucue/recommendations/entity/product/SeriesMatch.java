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
package com.gurucue.recommendations.entity.product;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.gurucue.recommendations.data.AttributeCodes;
import com.gurucue.recommendations.data.DataManager;
import com.gurucue.recommendations.data.DataProvider;
import com.gurucue.recommendations.entity.Attribute;
import com.gurucue.recommendations.entity.Language;
import com.gurucue.recommendations.entity.Partner;
import com.gurucue.recommendations.entity.value.AttributeValues;
import com.gurucue.recommendations.entity.value.MatchCondition;
import com.gurucue.recommendations.entity.value.TranslatableValue;
import com.gurucue.recommendations.entity.value.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SeriesMatch extends Matcher {
    private static final Logger log = LogManager.getLogger(SeriesMatch.class);

    public final TranslatableValue title;
    private final ImmutableList<Key> keys;

    public SeriesMatch(
            final long id,
            final long productTypeId,
            final long partnerId,
            final String partnerProductCode,
            final Timestamp added,
            final Timestamp modified,
            final Timestamp deleted,
            final AttributeValues attributeValues,
            final AttributeValues relatedValues,
            final DataProvider provider
    ) {
        this(provider.getAttributeCodes(), id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues, relatedValues);
    }

    public SeriesMatch(
            final AttributeCodes attributeCodes,
            final long id,
            final long productTypeId,
            final long partnerId,
            final String partnerProductCode,
            final Timestamp added,
            final Timestamp modified,
            final Timestamp deleted,
            final AttributeValues attributeValues,
            final AttributeValues relatedValues
    ) {
        this(
                id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues, relatedValues,
                attributeValues.getAsTranslatable(attributeCodes.title)
        );
    }

    private SeriesMatch(
            final long id,
            final long productTypeId,
            final long partnerId,
            final String partnerProductCode,
            final Timestamp added,
            final Timestamp modified,
            final Timestamp deleted,
            final AttributeValues attributeValues,
            final AttributeValues relatedValues,
            final TranslatableValue title
    ) {
        super(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues, relatedValues);
        if (title == null) throw new NullPointerException("Series title is null");
        this.title = title;
        // generate so many matcher keys as there are translations
        final ImmutableList.Builder<Key> keyBuilder = ImmutableList.builder();
        for (Map.Entry<Language, String> trEntry : title.translations.entrySet()) {
            keyBuilder.add(new Key(this, 0, trEntry.getKey(), trEntry.getValue()));
        }
        if (!title.translations.containsKey(title.language)) {
            // this should never occur, but we include it for completeness
            keyBuilder.add(new Key(this, 0, title.language, title.value));
        }
        keys = keyBuilder.build();
    }

    public static SeriesMatch create(
            final AttributeCodes attributeCodes,
            final long productTypeId,
            final TranslatableValue title
    ) {
        return new SeriesMatch(attributeCodes, 0L, productTypeId, Partner.PARTNER_ZERO_ID, null, null, null, null,
                new AttributeValues(ImmutableMap.<Attribute, Value>of(attributeCodes.title, title)), AttributeValues.NO_VALUES);
    }

    public static SeriesMatch create(
            final AttributeCodes attributeCodes,
            final long productTypeId,
            final AttributeValues attributeValues
    ) {
        return new SeriesMatch(attributeCodes, 0L, productTypeId, Partner.PARTNER_ZERO_ID, null, null, null, null,
                attributeValues, AttributeValues.NO_VALUES);
    }

    public static SeriesMatch create(final AttributeCodes attributeCodes, final VideoProduct video) {
        final ImmutableMap.Builder<Attribute, Value> attributeValuesBuilder = ImmutableMap.builder();
        final Value title = video.attributes.get(attributeCodes.title);
        if (title != null) attributeValuesBuilder.put(attributeCodes.title, title);
        return new SeriesMatch(attributeCodes, 0L, video.productTypeId, Partner.PARTNER_ZERO_ID, null, null, null, null,
                new AttributeValues(attributeValuesBuilder.build()), AttributeValues.NO_VALUES);
    }

    public static SeriesMatch create(final AttributeCodes attributeCodes, final VideoMatch video) {
        final ImmutableMap.Builder<Attribute, Value> attributeValuesBuilder = ImmutableMap.builder();
        final Value title = video.attributes.get(attributeCodes.title);
        if (title != null) attributeValuesBuilder.put(attributeCodes.title, title);
        return new SeriesMatch(attributeCodes, 0L, video.productTypeId, Partner.PARTNER_ZERO_ID, null, null, null, null,
                new AttributeValues(attributeValuesBuilder.build()), AttributeValues.NO_VALUES);
    }

    @Override
    public List<Key> getKeys() {
        return keys;
    }

    @Override
    public SeriesMatch merge(final Matcher matcher, final AttributeCodes attributeCodes) {
        if (matcher == null) return this;
        if (!(matcher instanceof SeriesMatch)) throw new IllegalStateException("Cannot perform merge: not a SeriesMatch matcher: " + matcher.getClass().getCanonicalName());
        final SeriesMatch other = (SeriesMatch)matcher;
/*        final StringBuilder reconcileLog = new StringBuilder(80 + keys.size() * 50);
        final TranslatableValue newTitle = title.merge(other.title, reconcileLog);
        if (reconcileLog.length() > 0) {
            log.warn("[" + Thread.currentThread().getId() + "] Merging of series matcher " + id + " to " + other.id + " had to reconcile difference(s) in both titles:\n" + reconcileLog.toString());
        }*/

        // intersect the translation sets: "delete" other translations from this translation, so later if we must we can simply assemble a union of translations
        final Map<Language, String> thisTranslations = new HashMap<>(title.translations);
        final Map<Language, String> otherTranslations = new HashMap<>(other.title.translations);
        if (!title.translations.containsKey(title.language)) thisTranslations.put(title.language, title.value);
        if (!other.title.translations.containsKey(other.title.language)) otherTranslations.put(other.title.language, other.title.value);
        final int thisTranslationsSize = thisTranslations.size();
        int thisMissing = 0;
        for (final Map.Entry<Language, String> entry : otherTranslations.entrySet()) {
            final String thisValue = thisTranslations.remove(entry.getKey());
            if (thisValue == null) thisMissing++;
            else if (!thisValue.equals(entry.getValue())) return null; // incompatible in this translation
        }
        if (thisMissing >= otherTranslations.size()) return null; // nothing in common

//        if (newTitle == other.title) {
        if ((thisMissing + thisTranslationsSize) == otherTranslations.size()) {
            // "other" wholly contains everything "this" contains
            if (other.id != 0L) return other; // it is okay to directly return it: it exists in the database
            if (id == 0L) return other; // it is okay to directly return it: neither exists in the database
            return new SeriesMatch(attributeCodes, id, other.productTypeId, other.partnerId, other.partnerProductCode, other.added, other.modified, other.deleted, other.attributes, other.related);
        }
//        if (newTitle == title) {
        if (thisMissing == 0) {
            // "this" wholly contains everything "other" contains
            if (id != 0L) return this; // it is okay to directly return it: it exists in the database
            if (other.id == 0L) return this; // it is okay to directly return it: neither exists in the database
            return new SeriesMatch(attributeCodes, other.id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related);
        }

        final ImmutableMap.Builder<Language, String> newTranslationsBuilder = ImmutableMap.builder();
        newTranslationsBuilder.putAll(otherTranslations);
        newTranslationsBuilder.putAll(thisTranslations);
        final TranslatableValue newTitle = new TranslatableValue(title.value, title.language, newTranslationsBuilder.build());
        return new SeriesMatch(attributeCodes, id == 0L ? other.id : id, productTypeId, Partner.PARTNER_ZERO_ID, null, null, null, null,
                new AttributeValues(ImmutableMap.<Attribute, Value>of(attributeCodes.title, newTitle)), AttributeValues.NO_VALUES);
    }

    @Override
    public boolean contains(final Matcher matcher) {
        if (!(matcher instanceof SeriesMatch)) return false;
        final SeriesMatch other = (SeriesMatch)matcher;
        if (other == this) return true;
        if (other.getKeys().size() > keys.size()) return false;
        final Map<Language, String> otherTranslations;
        if (other.title.translations.containsKey(other.title.language)) otherTranslations = other.title.translations;
        else {
            otherTranslations = new HashMap<>(other.title.translations);
            otherTranslations.put(other.title.language, other.title.value);
        }
        final Map<Language, String> thisTranslations;
        if (title.translations.containsKey(title.language)) thisTranslations = title.translations;
        else {
            thisTranslations = new HashMap<>(title.translations);
            thisTranslations.put(title.language, title.value);
        }
        for (final Map.Entry<Language, String> entry : otherTranslations.entrySet()) {
            final String thisValue = thisTranslations.get(entry.getKey());
            if (thisValue == null) return false;
            if (!thisValue.equals(entry.getValue())) return false;
        }
        return true;
    }

    public final class Key extends MatcherKey {
        private final SeriesMatch owner;
        private final Language titleLanguage;
        private final String titleValue;
        private final int hash;
        private MatchCondition condition = null; // lazy init

        Key(final SeriesMatch matcher, final int partialHash, final Language titleLanguage, final String titleValue) {
            this.owner = matcher;
            this.titleLanguage = titleLanguage;
            this.titleValue = titleValue;
            final long titleLanguageId = titleLanguage.getId();
            int newHash = partialHash + ((int)(titleLanguageId ^ (titleLanguageId >>> 32)));
            this.hash = (31 * newHash) + titleValue.hashCode();
        }

        @Override
        public SeriesMatch getMatcher() {
            return owner;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof Key)) return false;
            final Key otherKey = (Key)obj;
            return (titleLanguage.getId().longValue() == otherKey.titleLanguage.getId().longValue()) && titleValue.equals(otherKey.titleValue);
        }

        @Override
        public synchronized MatchCondition getCondition() {
            if (condition == null) {
                final AttributeCodes attributeCodes = DataManager.getAttributeCodes();
                // only the translation of this key
                condition = new MatchCondition(owner.productTypeId, new AttributeValues(ImmutableMap.<Attribute, Value>of(attributeCodes.title, new TranslatableValue(titleValue, titleLanguage))), AttributeValues.NO_VALUES);
            }
            return condition;
        }

        @Override
        public int compareTo(final MatcherKey other) {
            if (other instanceof Key) {
                final Key otherKey = (Key)other;
                // try to compare based on hash -- it's fast
                if (hash < otherKey.hash) return -1;
                if (hash > otherKey.hash) return 1;
                // hashes are the same, now we have to compare all data fields
                if (titleLanguage.getId().longValue() < otherKey.titleLanguage.getId().longValue()) return -1;
                if (titleLanguage.getId().longValue() > otherKey.titleLanguage.getId().longValue()) return 1;
                return titleValue.compareTo(otherKey.titleValue);
            }
            else if (other instanceof VideoMatch.Key) {
                // all series are "less" than any video, because a series product gets created before a video product
                return -1;
            }
            throw new IllegalArgumentException("Don't know how to compare a " + other.getClass().getCanonicalName() + " to " + getClass().getSimpleName());
        }
    }
}
