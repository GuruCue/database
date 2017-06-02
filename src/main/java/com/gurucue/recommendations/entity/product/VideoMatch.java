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
import com.gurucue.recommendations.entity.value.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VideoMatch extends Matcher {
    private static final Logger log = LogManager.getLogger(VideoMatch.class);

    public final TranslatableValue title;
    public final int productionYear; // 0 -> null
    public final int episodeNumber; // 0 -> null
    public final int seasonNumber; // 0 -> null
    public final long airDate; // timestamp, 0 -> null
    public final long seriesId; // 0 -> null
    private final ImmutableList<Key> keys;


    public VideoMatch(
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

    public VideoMatch(
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
                attributeValues.getAsTranslatable(attributeCodes.title),
                (int)attributeValues.getAsInteger(attributeCodes.productionYear),
                (int)attributeValues.getAsInteger(attributeCodes.episodeNumber),
                (int)attributeValues.getAsInteger(attributeCodes.seasonNumber),
                attributeValues.getAsInteger(attributeCodes.airDate),
                relatedValues.getAsInteger(attributeCodes.seriesId)
        );
    }

    private VideoMatch(
            final long id,
            final long productTypeId,
            final long partnerId,
            final String partnerProductCode,
            final Timestamp added,
            final Timestamp modified,
            final Timestamp deleted,
            final AttributeValues attributeValues,
            final AttributeValues relatedValues,
            final TranslatableValue title,
            final int productionYear,
            final int episodeNumber,
            final int seasonNumber,
            final long airDate,
            final long seriesId
    ) {
        super(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues, relatedValues);
        this.title = title;
        this.productionYear = productionYear;
        this.episodeNumber = episodeNumber;
        this.seasonNumber = seasonNumber;
        this.airDate = airDate;
        this.seriesId = seriesId;
        if (title == null) throw new NullPointerException("Null title");
        if (title.translations == null) throw new NullPointerException("Null title translations");

        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int hash = 31 * 17;
//        if (productionYear > 0) hash += productionYear;
//        hash *= 31;
        if (seriesId != 0L) hash += (int)(seriesId ^ (seriesId >>> 32));
        hash *= 31;
        final ImmutableList.Builder<Key> keyBuilder = ImmutableList.builder();
        // don't specialize keys while we're still cleaning bugs from the system
/*        if ((seriesId > 0L) && (episodeNumber > 0) && (seasonNumber > 0)) {
            if (episodeNumber > 0) hash += episodeNumber;
            hash *= 31;
            if (seasonNumber > 0) hash += seasonNumber;
            hash *= 31;
            keyBuilder.add(new EpisodeKey(this, hash));
        }
        else if ((seriesId > 0L) && (airDate > 0L)) {
            if (airDate > 0L) hash += (int)(airDate ^ (airDate >>> 32));
            hash *= 31;
            keyBuilder.add(new AirdateKey(this, hash));
        }
        else {*/
            if (episodeNumber != 0) hash += episodeNumber;
            hash *= 31;
            if (seasonNumber != 0) hash += seasonNumber;
            hash *= 31;
            if (airDate != 0L) hash += (int)(airDate ^ (airDate >>> 32));
            hash *= 31;
            // generate so many matcher keys as there are translations
            final int hashWithoutProductionYear = hash * 31;
            final int hashWithProductionYear = (hash + productionYear) * 31;
            for (Map.Entry<Language, String> trEntry : title.translations.entrySet()) {
                keyBuilder.add(new GeneralKey(this, hashWithoutProductionYear, trEntry.getKey(), trEntry.getValue(), 0));
                if (productionYear != 0) keyBuilder.add(new GeneralKey(this, hashWithProductionYear, trEntry.getKey(), trEntry.getValue(), productionYear));
            }
            if (!title.translations.containsKey(title.language)) {
                // this should never occur, but we include it for completeness
                keyBuilder.add(new GeneralKey(this, hashWithoutProductionYear, title.language, title.value, 0));
                if (productionYear != 0) keyBuilder.add(new GeneralKey(this, hashWithProductionYear, title.language, title.value, productionYear));
            }
//        }
        keys = keyBuilder.build();
    }

    public static VideoMatch create(
            final AttributeCodes attributeCodes,
            final long productTypeId,
            final TranslatableValue title,
            final int productionYear,
            final int episodeNumber,
            final int seasonNumber,
            final long airDate, // milliseconds since 1970-01-01 00:00 UTC
            final long seriesId
    ) {
        final ImmutableMap.Builder<Attribute, Value> attributeValuesBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<Attribute, Value> relatedValuesBuilder = ImmutableMap.builder();

        if (title != null) attributeValuesBuilder.put(attributeCodes.title, title);
        if (productionYear != 0) attributeValuesBuilder.put(attributeCodes.productionYear, new LongValue(productionYear, false));
        if (episodeNumber != 0) attributeValuesBuilder.put(attributeCodes.episodeNumber, new LongValue(episodeNumber, false));
        if (seasonNumber != 0) attributeValuesBuilder.put(attributeCodes.seasonNumber, new LongValue(seasonNumber, false));
        if (airDate != 0L) attributeValuesBuilder.put(attributeCodes.airDate, new LongValue(airDate, true));
        if (seriesId != 0L) relatedValuesBuilder.put(attributeCodes.seriesId, new LongValue(seriesId, false));

        return new VideoMatch(0L, productTypeId, Partner.PARTNER_ZERO_ID, null, null, null, null,
                new AttributeValues(attributeValuesBuilder.build()), new AttributeValues(relatedValuesBuilder.build()),
                title, productionYear, episodeNumber, seasonNumber, airDate, seriesId);
    }

    public static VideoMatch create(final AttributeCodes attributeCodes, final Product video) {
        final ImmutableMap.Builder<Attribute, Value> attributeValuesBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<Attribute, Value> relatedValuesBuilder = ImmutableMap.builder();

        final AttributeValues srcAttributes = video.attributes;
        final Value title = srcAttributes.get(attributeCodes.title);
        if (title != null) attributeValuesBuilder.put(attributeCodes.title, title);
        final Value productionYear = srcAttributes.get(attributeCodes.productionYear);
        if (productionYear != null) attributeValuesBuilder.put(attributeCodes.productionYear, productionYear);
        final Value episodeNumber = srcAttributes.get(attributeCodes.episodeNumber);
        if (episodeNumber != null) attributeValuesBuilder.put(attributeCodes.episodeNumber, episodeNumber);
        final Value seasonNumber = srcAttributes.get(attributeCodes.seasonNumber);
        if (seasonNumber != null) attributeValuesBuilder.put(attributeCodes.seasonNumber, seasonNumber);
        final Value airDate = srcAttributes.get(attributeCodes.airDate);
        if (airDate != null) attributeValuesBuilder.put(attributeCodes.airDate, airDate);
        final Value seriesId = video.related.get(attributeCodes.seriesId);
        if (seriesId != null) relatedValuesBuilder.put(attributeCodes.seriesId, seriesId);

        return new VideoMatch(0L, video.productTypeId, Partner.PARTNER_ZERO_ID, null, null, null, null,
                new AttributeValues(attributeValuesBuilder.build()), new AttributeValues(relatedValuesBuilder.build()),
                title == null ? null : (TranslatableValue)title,
                productionYear == null ? 0 : (int)productionYear.asInteger(),
                episodeNumber == null ? 0 : (int)episodeNumber.asInteger(),
                seasonNumber == null ? 0 : (int)seasonNumber.asInteger(),
                airDate == null ? 0L : airDate.asInteger(),
                seriesId == null ? 0L : seriesId.asInteger());
    }

    public static VideoMatch create(final GeneralVideoProduct video) {
        return new VideoMatch(video.id, video.productTypeId, video.partnerId, video.partnerProductCode, video.added, video.modified, video.deleted, video.attributes, video.related, video.title, video.productionYear, video.episodeNumber, video.seasonNumber, video.airDate, video.seriesId);
    }

    @Override
    public List<Key> getKeys() {
        return keys;
    }

    @Override
    public VideoMatch merge(final Matcher matcher, final AttributeCodes attributeCodes) {
        if (matcher == null) return this;
        if (!(matcher instanceof VideoMatch)) throw new IllegalStateException("Cannot perform merge: not a VideoMatch matcher: " + matcher.getClass().getCanonicalName());
        final VideoMatch other = (VideoMatch)matcher;
/*        final StringBuilder errorBuilder = new StringBuilder(128);

        // validate equality of single-valued attributes
        if (this.episodeNumber != other.episodeNumber) errorBuilder.append(", episode numbers do not match");
        if (this.seasonNumber != other.seasonNumber) errorBuilder.append(", season numbers do not match");
        if (this.airDate != other.airDate) errorBuilder.append(", air-dates do not match");
        if (this.seriesId != other.seriesId) errorBuilder.append(", series IDs do not match");
        if ((this.productionYear > 0L) && (other.productionYear > 0L) && (this.productionYear != other.productionYear)) errorBuilder.append(", production years do not match");

        if (errorBuilder.length() > 0) {
            throw new IllegalArgumentException("Cannot perform merge of VideoMatch instances " + id + " (this) and " + other.id + " (other): " + errorBuilder.substring(2));
        }

        final StringBuilder reconcileLog = new StringBuilder(80 + keys.size() * 50);
        final TranslatableValue newTitle = title.merge(other.title, reconcileLog);
        if (reconcileLog.length() > 0) {
            log.warn("[" + Thread.currentThread().getId() + "] Merging of video matcher " + id + " to " + other.id + " had to reconcile difference(s) in both titles:\n" + reconcileLog.toString());
        }*/

        // the stop merge block
        if (this.episodeNumber != other.episodeNumber) return null;
        if (this.seasonNumber != other.seasonNumber) return null;
        if (this.airDate != other.airDate) return null;
        if (this.seriesId != other.seriesId) return null;
        if ((this.productionYear != 0) && (other.productionYear != 0) && (this.productionYear != other.productionYear)) return null;

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
/*
//        if (newTitle == other.title) {
        if ((thisMissing + thisTranslationsSize) == otherTranslations.size()) {
            // "other" wholly contains everything "this" contains
            final AttributeValues correctAttributes;
            if ((other.productionYear > 0) || (this.productionYear <= 0)) {
                if (other.id > 0L) return other; // it is okay to directly return it: it exists in the database
                if (id <= 0L) return other; // it is okay to directly return it: neither exists in the database
                correctAttributes = other.attributes;
            }
            else correctAttributes = other.attributes.modify(ImmutableMap.of(attributeCodes.productionYear, this.attributes.get(attributeCodes.productionYear)), null);
            return new VideoMatch(attributeCodes, other.id > 0L ? other.id : id, other.productTypeId, other.partnerId, other.partnerProductCode, other.added, other.deleted, correctAttributes, other.related);
        }
//        if (newTitle == title) {
        if (thisMissing == 0) {
            // "this" wholly contains everything "other" contains
            final AttributeValues correctAttributes;
            if ((this.productionYear > 0) || (other.productionYear <= 0)) {
                if (id > 0L) return this; // it is okay to directly return it: it exists in the database
                if (other.id <= 0L) return this; // it is okay to directly return it: neither exists in the database
                correctAttributes = this.attributes;
            }
            else correctAttributes = this.attributes.modify(ImmutableMap.of(attributeCodes.productionYear, other.attributes.get(attributeCodes.productionYear)), null);
            return new VideoMatch(attributeCodes, id > 0L ? id : other.id, productTypeId, partnerId, partnerProductCode, added, deleted, correctAttributes, related);
        }

        final ImmutableMap.Builder<Attribute, Value> attributeValuesBuilder = ImmutableMap.builder();
//        attributeValuesBuilder.put(attributeCodes.title, new TranslatableValue(commonTranslations.get(other.title.language), other.title.language, ImmutableMap.copyOf(commonTranslations)));
        final ImmutableMap.Builder<Language, String> newTranslationsBuilder = ImmutableMap.builder();
        newTranslationsBuilder.putAll(otherTranslations);
        newTranslationsBuilder.putAll(thisTranslations);
        // title
        final TranslatableValue newTitle = new TranslatableValue(title.value, title.language, newTranslationsBuilder.build());
        attributeValuesBuilder.put(attributeCodes.title, newTitle);
        // production-year
        final Value productionYearValue = attributes.get(attributeCodes.productionYear);
        if (productionYearValue != null) attributeValuesBuilder.put(attributeCodes.productionYear, productionYearValue);
        else {
            final Value otherProductionYearValue = other.attributes.get(attributeCodes.productionYear);
            if (otherProductionYearValue != null) attributeValuesBuilder.put(attributeCodes.productionYear, otherProductionYearValue);
        }
        // episode-number
        final Value episodeNumberValue = attributes.get(attributeCodes.episodeNumber);
        if (episodeNumberValue != null) attributeValuesBuilder.put(attributeCodes.episodeNumber, episodeNumberValue);
        // season-number
        final Value seasonNumberValue = attributes.get(attributeCodes.seasonNumber);
        if (seasonNumberValue != null) attributeValuesBuilder.put(attributeCodes.seasonNumber, seasonNumberValue);
        // air-date
        final Value airDateValue = attributes.get(attributeCodes.airDate);
        if (airDateValue != null) attributeValuesBuilder.put(attributeCodes.airDate, airDateValue);

        return new VideoMatch(attributeCodes, id > 0L ? id : other.id, productTypeId, partnerId, partnerProductCode, null, null,
                new AttributeValues(attributeValuesBuilder.build()), other.related);
        */

        // set correct attributes, with minimum of operations
        final AttributeValues correctAttributes;
        if (thisMissing == 0) {
            if (thisTranslationsSize == otherTranslations.size()) {
                // "this" and "other" are equal as far the translations are concerned, now check production years
                if (this.productionYear != 0) correctAttributes = this.attributes;
                else correctAttributes = other.attributes;
            }
            else {
                // "this" wholly contains everything "other" contains, merge production year if necessary
                if ((this.productionYear != 0) || (other.productionYear == 0)) correctAttributes = this.attributes;
                else correctAttributes = this.attributes.modify(ImmutableMap.of(attributeCodes.productionYear, other.attributes.get(attributeCodes.productionYear)), null);
            }
        }
        else if ((thisMissing + thisTranslationsSize) == otherTranslations.size()) {
            // "other" wholly contains everything "this" contains, merge production year if necessary
            if ((other.productionYear != 0) || (this.productionYear == 0)) correctAttributes = other.attributes;
            else correctAttributes = other.attributes.modify(ImmutableMap.of(attributeCodes.productionYear, this.attributes.get(attributeCodes.productionYear)), null);
        }
        else {
            // neither contains the other, assemble attributes from both
            final ImmutableMap.Builder<Attribute, Value> attributeValuesBuilder = ImmutableMap.builder();
            final ImmutableMap.Builder<Language, String> newTranslationsBuilder = ImmutableMap.builder();
            newTranslationsBuilder.putAll(otherTranslations);
            newTranslationsBuilder.putAll(thisTranslations);
            // title
            final TranslatableValue newTitle = new TranslatableValue(title.value, title.language, newTranslationsBuilder.build());
            attributeValuesBuilder.put(attributeCodes.title, newTitle);
            // production-year
            final Value productionYearValue = attributes.get(attributeCodes.productionYear);
            if (productionYearValue != null) attributeValuesBuilder.put(attributeCodes.productionYear, productionYearValue);
            else {
                final Value otherProductionYearValue = other.attributes.get(attributeCodes.productionYear);
                if (otherProductionYearValue != null) attributeValuesBuilder.put(attributeCodes.productionYear, otherProductionYearValue);
            }
            // episode-number
            final Value episodeNumberValue = attributes.get(attributeCodes.episodeNumber);
            if (episodeNumberValue != null) attributeValuesBuilder.put(attributeCodes.episodeNumber, episodeNumberValue);
            // season-number
            final Value seasonNumberValue = attributes.get(attributeCodes.seasonNumber);
            if (seasonNumberValue != null) attributeValuesBuilder.put(attributeCodes.seasonNumber, seasonNumberValue);
            // air-date
            final Value airDateValue = attributes.get(attributeCodes.airDate);
            if (airDateValue != null) attributeValuesBuilder.put(attributeCodes.airDate, airDateValue);

            correctAttributes = new AttributeValues(attributeValuesBuilder.build());
        }

        // see if you can return directly this or the other, and only if that's not possible construct a new instance
        if (this.id != 0L) {
            if (other.id != 0L) {
                if (other.attributes == correctAttributes) return other;
            }
            if (this.attributes == correctAttributes) return this;
            // arbitrary choice: use this.id
            return new VideoMatch(attributeCodes, id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, correctAttributes, related);
        }
        else if (other.id != 0L) {
            if (other.attributes == correctAttributes) return other;
            return new VideoMatch(attributeCodes, other.id, other.productTypeId, other.partnerId, other.partnerProductCode, other.added, other.modified, other.deleted, correctAttributes, other.related);
        }

        return new VideoMatch(attributeCodes, 0L, productTypeId, partnerId, partnerProductCode, null, null, null, correctAttributes, related);
    }

    @Override
    public boolean contains(final Matcher matcher) {
        if (!(matcher instanceof VideoMatch)) return false;
        final VideoMatch other = (VideoMatch)matcher;
        if (other == this) return true;
        if (other.getKeys().size() > keys.size()) return false;
//        if (other.productionYear != productionYear) return false;
        if ((other.productionYear != 0L) && (other.productionYear != productionYear)) return false;
        if (other.seasonNumber != seasonNumber) return false;
        if (other.episodeNumber != episodeNumber) return false;
        if (other.airDate != airDate) return false;
        if (other.seriesId != seriesId) return false;
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

    public abstract class Key extends MatcherKey {
        protected final VideoMatch owner;
        protected final Language titleLanguage;
        protected final String titleValue;
        protected final int productionYear;

        Key(final VideoMatch owner, final Language titleLanguage, final String titleValue, final int productionYear) {
            this.owner = owner;
            this.titleLanguage = titleLanguage;
            this.titleValue = titleValue;
            this.productionYear = productionYear;
        }

        @Override
        public VideoMatch getMatcher() {
            return owner;
        }
    }

    public final class GeneralKey extends Key {
        private final int hash;
        private MatchCondition condition = null; // lazy init

        GeneralKey(final VideoMatch matcher, final int partialHash, final Language titleLanguage, final String titleValue, final int productionYear) {
            super(matcher, titleLanguage, titleValue, productionYear);
            final long titleLanguageId = titleLanguage.getId();
            int newHash = partialHash + ((int)(titleLanguageId ^ (titleLanguageId >>> 32)));
            this.hash = (31 * newHash) + titleValue.hashCode();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof GeneralKey)) return false;
            final GeneralKey otherKey = (GeneralKey)obj;
            final VideoMatch otherMatch = otherKey.owner;
            if (titleLanguage.getId().longValue() != otherKey.titleLanguage.getId().longValue()) return false;
            if ((productionYear != 0) && (otherKey.productionYear != 0) && (productionYear != otherKey.productionYear)) return false;
            if (owner.episodeNumber != otherMatch.episodeNumber) return false;
            if (owner.seasonNumber != otherMatch.seasonNumber) return false;
            if (owner.airDate != otherMatch.airDate) return false;
            if (owner.seriesId != otherMatch.seriesId) return false;
            return titleValue.equals(otherKey.titleValue);
        }

        @Override
        public synchronized MatchCondition getCondition() {
            if (condition == null) {
                // fixed values
                final AttributeCodes attributeCodes = DataManager.getAttributeCodes();
                final ImmutableMap.Builder<Attribute, Value> attributesBuilder = ImmutableMap.builder();
                final ImmutableMap.Builder<Attribute, Value> relatedBuilder = ImmutableMap.builder();
//                final Value productionYear = owner.attributes.get(attributeCodes.productionYear);
                attributesBuilder.put(attributeCodes.productionYear, productionYear != 0 ? new LongValue((long)productionYear, false) : NullValue.INSTANCE);
                final Value episodeNumber = owner.attributes.get(attributeCodes.episodeNumber);
                attributesBuilder.put(attributeCodes.episodeNumber, episodeNumber == null ? NullValue.INSTANCE : episodeNumber);
                final Value seasonNumber = owner.attributes.get(attributeCodes.seasonNumber);
                attributesBuilder.put(attributeCodes.seasonNumber, seasonNumber == null ? NullValue.INSTANCE : seasonNumber);
                final Value airDate = owner.attributes.get(attributeCodes.airDate);
                attributesBuilder.put(attributeCodes.airDate, airDate == null ? NullValue.INSTANCE : airDate);
                final Value seriesId = owner.related.get(attributeCodes.seriesId);
                relatedBuilder.put(attributeCodes.seriesId, seriesId == null ? NullValue.INSTANCE : seriesId);
                // only the translation of this key
                if ((titleLanguage != null) && (titleValue != null)) {
                    attributesBuilder.put(attributeCodes.title, new TranslatableValue(titleValue, titleLanguage));
                }
                condition = new MatchCondition(owner.productTypeId, new AttributeValues(attributesBuilder.build()), new AttributeValues(relatedBuilder.build()));
            }
            return condition;
        }

        @Override
        public int compareTo(final MatcherKey other) {
            if (other instanceof AirdateKey) {
                // airdatics come before episodics
                return -1;
            }
            else if (other instanceof EpisodeKey) {
                // episodes are "more than" movies
                return -1;
            }
            else if (other instanceof GeneralKey) {
                // "native" comparison
                final GeneralKey otherKey = (GeneralKey)other;
                // try to compare based on hash -- it's fast
                if (hash < otherKey.hash) return -1;
                if (hash > otherKey.hash) return 1;
                // hashes are the same, now we have to compare all data fields
                final VideoMatch thisMatch = owner; // cache in the local variables for faster access
                final VideoMatch otherMatch = otherKey.owner;
                if (productionYear < otherKey.productionYear) return -1;
                if (productionYear > otherKey.productionYear) return 1;
                if (thisMatch.seriesId < otherMatch.seriesId) return -1;
                if (thisMatch.seriesId > otherMatch.seriesId) return 1;
                if (thisMatch.seasonNumber < otherMatch.seasonNumber) return -1;
                if (thisMatch.seasonNumber > otherMatch.seasonNumber) return 1;
                if (thisMatch.episodeNumber < otherMatch.episodeNumber) return -1;
                if (thisMatch.episodeNumber > otherMatch.episodeNumber) return 1;
                if (thisMatch.airDate < otherMatch.airDate) return -1;
                if (thisMatch.airDate > otherMatch.airDate) return 1;
                if (titleLanguage.getId().longValue() < otherKey.titleLanguage.getId().longValue()) return -1;
                if (titleLanguage.getId().longValue() > otherKey.titleLanguage.getId().longValue()) return 1;
                return titleValue.compareTo(otherKey.titleValue);
            }
            else if (other instanceof SeriesMatch.Key) {
                // all series are "less" than any video, because a series product gets created before a video product
                return 1;
            }
            throw new IllegalArgumentException("Don't know how to compare a " + other.getClass().getCanonicalName() + " to " + getClass().getSimpleName());
        }
    }

    /**
     * For matching episodic content, that must contain episode-number, season-number and series-id.
     * Those 3 attributes uniquely identify an episode of a series/serial.
     */
    public final class EpisodeKey extends Key {
        private final int hash;
        private MatchCondition condition = null; // lazy init

        EpisodeKey(final VideoMatch matcher, final int partialHash, final int productionYear) {
            super(matcher, null, null, productionYear);
            this.hash = partialHash;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof EpisodeKey)) return false;
            final EpisodeKey otherKey = (EpisodeKey)obj;
            final VideoMatch otherMatch = otherKey.owner;
            if ((productionYear != 0) && (otherKey.productionYear != 0) && (productionYear != otherKey.productionYear)) return false;
            if (owner.episodeNumber != otherMatch.episodeNumber) return false;
            if (owner.seasonNumber != otherMatch.seasonNumber) return false;
            if (owner.seriesId != otherMatch.seriesId) return false;
            return true;
        }

        @Override
        public synchronized MatchCondition getCondition() {
            if (condition == null) {
                // fixed values
                final AttributeCodes attributeCodes = DataManager.getAttributeCodes();
                final ImmutableMap.Builder<Attribute, Value> attributesBuilder = ImmutableMap.builder();
                final ImmutableMap.Builder<Attribute, Value> relatedBuilder = ImmutableMap.builder();
//                final Value productionYear = owner.attributes.get(attributeCodes.productionYear);
                attributesBuilder.put(attributeCodes.productionYear, productionYear != 0 ? new LongValue((long)productionYear, false) : NullValue.INSTANCE);
                final Value episodeNumber = owner.attributes.get(attributeCodes.episodeNumber);
                attributesBuilder.put(attributeCodes.episodeNumber, episodeNumber == null ? NullValue.INSTANCE : episodeNumber);
                final Value seasonNumber = owner.attributes.get(attributeCodes.seasonNumber);
                attributesBuilder.put(attributeCodes.seasonNumber, seasonNumber == null ? NullValue.INSTANCE : seasonNumber);
                final Value seriesId = owner.related.get(attributeCodes.seriesId);
                relatedBuilder.put(attributeCodes.seriesId, seriesId == null ? NullValue.INSTANCE : seriesId);
                condition = new MatchCondition(owner.productTypeId, new AttributeValues(attributesBuilder.build()), new AttributeValues(relatedBuilder.build()));
            }
            return condition;
        }

        @Override
        public int compareTo(final MatcherKey other) {
            if (other instanceof AirdateKey) {
                // airdatics come before episodics
                return -1;
            }
            else if (other instanceof EpisodeKey) {
                // "native" comparison
                final EpisodeKey otherKey = (EpisodeKey)other;
                // try to compare based on hash -- it's fast
                if (hash < otherKey.hash) return -1;
                if (hash > otherKey.hash) return 1;
                // hashes are the same, now we have to compare all data fields
                final VideoMatch thisMatch = owner; // cache in the local variables for faster access
                final VideoMatch otherMatch = otherKey.owner;
                if (productionYear < otherKey.productionYear) return -1;
                if (productionYear > otherKey.productionYear) return 1;
                if (thisMatch.seriesId < otherMatch.seriesId) return -1;
                if (thisMatch.seriesId > otherMatch.seriesId) return 1;
                if (thisMatch.seasonNumber < otherMatch.seasonNumber) return -1;
                if (thisMatch.seasonNumber > otherMatch.seasonNumber) return 1;
                if (thisMatch.episodeNumber < otherMatch.episodeNumber) return -1;
                if (thisMatch.episodeNumber > otherMatch.episodeNumber) return 1;
                return 0;
            }
            else if (other instanceof GeneralKey) {
                // all movies are "less" than episodes
                return 1;
            }
            else if (other instanceof SeriesMatch.Key) {
                // all series are "less" than any video, because a series product gets created before a video product
                return 1;
            }
            throw new IllegalArgumentException("Don't know how to compare a " + other.getClass().getCanonicalName() + " to " + getClass().getSimpleName());
        }
    }

    /**
     * For matching content having air-date, that must contain air-date and series-id.
     * Those 2 attributes uniquely identify an episode.
     */
    public final class AirdateKey extends Key {
        private final int hash;
        private MatchCondition condition = null; // lazy init

        AirdateKey(final VideoMatch matcher, final int partialHash, final int productionYear) {
            super(matcher, null, null, 0);
            this.hash = partialHash;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof AirdateKey)) return false;
            final AirdateKey otherKey = (AirdateKey)obj;
            final VideoMatch otherMatch = otherKey.owner;
            if ((productionYear != 0) && (otherKey.productionYear != 0) && (productionYear != otherKey.productionYear)) return false;
            if (owner.airDate != otherMatch.airDate) return false;
            if (owner.seriesId != otherMatch.seriesId) return false;
            return true;
        }

        @Override
        public synchronized MatchCondition getCondition() {
            if (condition == null) {
                // fixed values
                final AttributeCodes attributeCodes = DataManager.getAttributeCodes();
                final ImmutableMap.Builder<Attribute, Value> attributesBuilder = ImmutableMap.builder();
                final ImmutableMap.Builder<Attribute, Value> relatedBuilder = ImmutableMap.builder();
//                final Value productionYear = owner.attributes.get(attributeCodes.productionYear);
//                attributesBuilder.put(attributeCodes.productionYear, productionYear == null ? NullValue.INSTANCE : productionYear);
                attributesBuilder.put(attributeCodes.productionYear, productionYear != 0 ? new LongValue((long)productionYear, false) : NullValue.INSTANCE);
                final Value airDate = owner.attributes.get(attributeCodes.airDate);
                attributesBuilder.put(attributeCodes.airDate, airDate == null ? NullValue.INSTANCE : airDate);
                final Value seriesId = owner.related.get(attributeCodes.seriesId);
                relatedBuilder.put(attributeCodes.seriesId, seriesId == null ? NullValue.INSTANCE : seriesId);
                condition = new MatchCondition(owner.productTypeId, new AttributeValues(attributesBuilder.build()), new AttributeValues(relatedBuilder.build()));
            }
            return condition;
        }

        @Override
        public int compareTo(final MatcherKey other) {
            if (other instanceof AirdateKey) {
                // "native" comparison
                final AirdateKey otherKey = (AirdateKey)other;
                // try to compare based on hash -- it's fast
                if (hash < otherKey.hash) return -1;
                if (hash > otherKey.hash) return 1;
                // hashes are the same, now we have to compare all data fields
                final VideoMatch thisMatch = owner; // cache in the local variables for faster access
                final VideoMatch otherMatch = otherKey.owner;
                if (productionYear < otherKey.productionYear) return -1;
                if (productionYear > otherKey.productionYear) return 1;
                if (thisMatch.seriesId < otherMatch.seriesId) return -1;
                if (thisMatch.seriesId > otherMatch.seriesId) return 1;
                if (thisMatch.airDate < otherMatch.airDate) return -1;
                if (thisMatch.airDate > otherMatch.airDate) return 1;
                return 0;
            }
            else if (other instanceof EpisodeKey) {
                // all episodics are "less" than airdatics
                return 1;
            }
            else if (other instanceof GeneralKey) {
                // all movies are "less" than airdatics
                return 1;
            }
            else if (other instanceof SeriesMatch.Key) {
                // all series are "less" than any video, because a series product gets created before a video product
                return 1;
            }
            throw new IllegalArgumentException("Don't know how to compare a " + other.getClass().getCanonicalName() + " to " + getClass().getSimpleName());
        }
    }
}
