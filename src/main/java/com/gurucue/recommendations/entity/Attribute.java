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
package com.gurucue.recommendations.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gurucue.recommendations.type.ValueType;

public class Attribute implements Serializable {
    private static final long serialVersionUID = 9122247805922014343L;

    /** Identifier of the genre attribute. */
    public static final String GENRE = "genre";
    /** Identifier of the production year attribute. */
    public static final String PRODUCTION_YEAR = "production-year";
    /** Identifier of the run time attribute. */
    public static final String RUN_TIME = "run-time";
    /** Identifier of the actor attribute. */
    public static final String ACTOR = "actor";
    /** Identifier of the director attribute. */
    public static final String DIRECTOR = "director";
    /** Identifier of the voice attribute. */
    public static final String VOICE = "voice";
    /** Identifier of the title attribute. */
    public static final String TITLE = "title";
    /** Identifier of the cover picture attribute. */
    public static final String COVER_PICTURE = "cover-picture";
    /** Identifier of the IMDB link for movies. */
    public static final String IMDB_LINK = "imdb-link";
    /** Identifier of the IMDB rating for movies. */
    public static final String IMDB_RATING = "imdb-rating";
    /** Identifier of the adult flag. */
    public static final String IS_ADULT = "is-adult";
    /** Identifier of the air date. */
    public static final String AIR_DATE = "air-date";
    /** Identifier of the episode number in TV series, it is relative to a season. For the absolute number use part-number. */
    public static final String EPISODE_NUMBER = "episode-number";
    /** Identifier of the season number of a TV series */
    public static final String SEASON_NUMBER = "season-number";
    /** Identifier of the TV series ID. Used in TV series episodes. */
    public static final String SERIES_ID = "series-id";
    /** Identifier of the part number in TV series and movies. Do not confuse it with episode number: episode number is relative to a season, part number is an absolute number regardless of season. */
    public static final String PART_NUMBER = "part-number";
    /** Identifier of the subtitle attribute of a TV-series, and similar. */
    public static final String TITLE2 = "title2";
    /** Identifier of the TV-channel attribute. */
    public static final String TV_CHANNEL = "tv-channel";
    /** Identifier of the country attribute. */
    public static final String COUNTRY = "country";
    /** Identifier of the video category attribute. */
    public static final String VIDEO_CATEGORY = "video-category";
    /** Identifier of the episode identifier attribute. This is a string, which is usually in the form season.part. */
    @Deprecated
    public static final String EPISODE_IDENTIFIER = "episode-identifier";
    /** Identifier of the spoken language attribute. */
    public static final String SPOKEN_LANGUAGE = "spoken-language";
    /** Identifier of the begin time attribute. */
    public static final String BEGIN_TIME = "begin-time";
    /** Identifier of the end time attribute. */
    public static final String END_TIME = "end-time";
    /** Identifier of the catch-up flag. */
    @Deprecated
    public static final String IS_CATCHUP = "is-catchup";
    /** Identifier of the video format attribute. */
    public static final String VIDEO_FORMAT = "video-format";
    /** Identifier of the catalogue ID attribute. */
    public static final String CATALOGUE_ID = "catalogue-id";
    /** Identifier of the price attribute */
    public static final String PRICE = "price";
    /** Identifier of the subtitle language attribute. */
    public static final String SUBTITLE_LANGUAGE = "subtitle-language";
    /** Identifier of the video content ID, */
    public static final String VIDEO_ID = "video-id";
    /** Identifier of the attribute for catalogue ID with price. */
    public static final String CATALOGUE_PRICE = "catalogue-price";
    /** Identifier of the attribute for catalogue ID with expiry timestamp, when the product attribute should be removed. */
    public static final String CATALOGUE_EXPIRY = "catalogue-expiry";
    /** Identifier for the attribute for catchup time a channel has available, in hours. */
    public static final String CATCHUP_HOURS = "catchup-hours";
    /** Identifier for the tv-channel ID attribute of a package. */
    public static final String TV_CHANNEL_ID = "tv-channel-id";
    /** Identifier for the expiry attribute, containing the timestamp after which the content is deleted. */
    public static final String EXPIRY = "expiry";
    /** Identifier for the package type attribute. */
    public static final String PACKAGE_TYPE = "package-type";
    /** Identifier for the TVOD ID attribute. */
    public static final String TVOD_ID = "tvod-id";
    /** Identifier for the SVOD ID attribute. */
    public static final String SVOD_ID = "svod-id";
    /** Identifier for the attribute for ID of interactive content. */
    public static final String INTERACTIVE_ID = "interactive-id";
    /** Identifier for the category ID attribute. */
    public static final String CATEGORY_ID = "category-id";
    /** Identifier for the parental rating attribute. */
    public static final String PARENTAL_RATING = "parental-rating";
    /** Identifier for the pseudo-attribute telling whether a consumer is subscribed to the product. */
    public static final String IS_SUBSCRIBED = "is-subscribed";
    /** Identifier for the pseudo-attribute containing the ID of the package a consumer needs to subscribe to in order to be able access a certain content. */
    public static final String PACKAGE_ID = "package-id";
    /** Identifier for the description attribute. */
    public static final String DESCRIPTION = "description";
    /** Identifier for the image URL attribute. */
    public static final String IMAGE_URL = "image-url";
    /** Identifier for the VOD category attribute. */
    public static final String VOD_CATEGORY = "vod-category";
    /** Identifier for the validity attribute. */
    public static final String VALIDITY = "validity";
    /** Identifier for the is-series flag. */
    public static final String IS_SERIES = "is-series";
    /** Identifier for the screenplay writer attribute. */
    public static final String SCREENPLAY_WRITER = "screenplay-writer";
    /** Identifier for the list of matched attributes, used for content that is result of a search. */
    public static final String MATCHED_ATTRIBUTE = "matched-attribute";

    /** Unmodifiable list of all attributes. */
    public static final List<String> allAttributes;
    static {
        final List<String> attrs = new ArrayList<String>();
        attrs.add(GENRE);
        attrs.add(PRODUCTION_YEAR);
        attrs.add(RUN_TIME);
        attrs.add(ACTOR);
        attrs.add(DIRECTOR);
        attrs.add(VOICE);
        attrs.add(TITLE);
        attrs.add(COVER_PICTURE);
        attrs.add(IMDB_LINK);
        attrs.add(IMDB_RATING);
        attrs.add(IS_ADULT);
        attrs.add(AIR_DATE);
        attrs.add(EPISODE_NUMBER);
        attrs.add(SEASON_NUMBER);
        attrs.add(SERIES_ID);
        attrs.add(PART_NUMBER);
        attrs.add(TITLE2);
        attrs.add(TV_CHANNEL);
        attrs.add(COUNTRY);
        attrs.add(VIDEO_CATEGORY);
        attrs.add(EPISODE_IDENTIFIER);
        attrs.add(SPOKEN_LANGUAGE);
        attrs.add(BEGIN_TIME);
        attrs.add(END_TIME);
        attrs.add(IS_CATCHUP);
        attrs.add(VIDEO_FORMAT);
        attrs.add(CATALOGUE_ID);
        attrs.add(PRICE);
        attrs.add(SUBTITLE_LANGUAGE);
        attrs.add(VIDEO_ID);
        attrs.add(CATALOGUE_PRICE);
        attrs.add(CATALOGUE_EXPIRY);
        attrs.add(CATCHUP_HOURS);
        attrs.add(TV_CHANNEL_ID);
        attrs.add(EXPIRY);
        attrs.add(PACKAGE_TYPE);
        attrs.add(TVOD_ID);
        attrs.add(SVOD_ID);
        attrs.add(INTERACTIVE_ID);
        attrs.add(CATEGORY_ID);
        attrs.add(PARENTAL_RATING);
        attrs.add(IS_SUBSCRIBED);
        attrs.add(PACKAGE_ID);
        attrs.add(DESCRIPTION);
        attrs.add(IMAGE_URL);
        attrs.add(VOD_CATEGORY);
        attrs.add(VALIDITY);
        attrs.add(IS_SERIES);
        attrs.add(SCREENPLAY_WRITER);
        attrs.add(MATCHED_ATTRIBUTE);
        allAttributes = Collections.unmodifiableList(attrs);
    }

    Long id;
    String identifier;
    ValueType valueType;
    Boolean isTranslatable;
    Boolean isMultivalue;
    Boolean isPrivate;
    
    public Attribute() {

    }

    public Attribute(final Long id, final String identifier, final ValueType valueType, final Boolean isTranslatable, final Boolean isMultivalue, final Boolean isPrivate) {
        this.id = id;
        this.identifier = identifier;
        this.valueType = valueType;
        this.isTranslatable = isTranslatable;
        this.isMultivalue = isMultivalue;
        this.isPrivate = isPrivate;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(final ValueType valueType) {
        this.valueType = valueType;
    }

    public Boolean getIsTranslatable() {
        return isTranslatable;
    }

    public void setIsTranslatable(final Boolean isTranslatable) {
        this.isTranslatable = isTranslatable;
    }

    public Boolean getIsMultivalue() {
        return isMultivalue;
    }

    public void setIsMultivalue(final Boolean isMultivalue) {
        this.isMultivalue = isMultivalue;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(final Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof Attribute) {
            final Attribute other = (Attribute) obj;
            // only compare ids
            return (this.getId() == other.getId()) ||
                ((this.getId() != null) && this.getId().equals(other.getId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        return 31 * 17 + (getId() == null ? 0 : getId().hashCode());
    }

    @Override
    public String toString() {
        return "Attribute(id=" + (null == getId() ? "null" : getId().toString()) +
            ", identifier=" + (null == getIdentifier() ? "null" : "\"" + getIdentifier().replace("\"", "\\\"") + "\"") +
            ", valueType=" + (null == getValueType() ? "null" : getValueType().toString()) +
            ", isTranslatable=" + (null == getIsTranslatable() ? "null" : getIsTranslatable().toString()) +
            ", isMultivalue=" + (null == getIsMultivalue() ? "null" : getIsMultivalue().toString()) +
            ", isPrivate=" + (null == getIsPrivate() ? "null" : getIsPrivate().toString()) +
            ")";
    }
}
