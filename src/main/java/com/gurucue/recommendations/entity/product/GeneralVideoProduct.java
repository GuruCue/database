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

import com.gurucue.recommendations.data.AttributeCodes;
import com.gurucue.recommendations.data.DataProvider;
import com.gurucue.recommendations.entity.value.AttributeValues;
import com.gurucue.recommendations.entity.value.TranslatableValue;

import java.sql.Timestamp;

/**
 * Common base for video products, currently those are video and tv-programme products.
 */
public class GeneralVideoProduct extends Product {
    public final TranslatableValue title;
    public final TranslatableValue title2;
    public final int productionYear; // 0 -> null
    public final TranslatableValue[] actors;
    public final String[] countries;
    public final TranslatableValue[] directors;
    public final String[] genres;
    public final String imdbLink;
    public final double imdbRating; // 0 -> null
    public final int runTime; // 0 -> null, in minutes
    public final String[] spokenLanguages;
    public final String[] subtitleLanguages;
    public final String videoCategory;
    public final String videoFormat;
    public final boolean isAdult; // default: false
    public final int parentalRating;
    public final int episodeNumber; // 0 -> null
    public final int seasonNumber; // 0 -> null
    public final long airDate; // timestamp, -1 -> null
    public final long seriesId; // 0 -> null TODO: rename to seriesMatchId
    public final String description;
    public final String imageUrl;
    public final long videoMatchId;
    public final TranslatableValue[] screenplayWriters;

    GeneralVideoProduct(
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
        super(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues, relatedValues);
        final AttributeCodes attributeCodes = provider.getAttributeCodes();
        title = attributes.getAsTranslatable(attributeCodes.title);
        title2 = attributes.getAsTranslatable(attributeCodes.title2);
        productionYear = (int)attributes.getAsInteger(attributeCodes.productionYear);
        actors = attributes.getAsTranslatables(attributeCodes.actor);
        countries = attributes.getAsStrings(attributeCodes.country);
        directors = attributes.getAsTranslatables(attributeCodes.director);
        genres = attributes.getAsStrings(attributeCodes.genre);
        imdbLink = attributes.getAsString(attributeCodes.imdbLink);
        imdbRating = attributes.getAsFloat(attributeCodes.imdbRating);
        runTime = (int)attributes.getAsInteger(attributeCodes.runTime);
        spokenLanguages = attributes.getAsStrings(attributeCodes.spokenLanguage);
        subtitleLanguages = attributes.getAsStrings(attributeCodes.subtitleLanguage);
        videoCategory = attributes.getAsString(attributeCodes.videoCategory);
        videoFormat = attributes.getAsString(attributeCodes.videoFormat);
        isAdult = attributes.getAsBoolean(attributeCodes.isAdult);
        parentalRating = (int)attributes.getAsInteger(attributeCodes.parentalRating);
        episodeNumber = (int)attributes.getAsInteger(attributeCodes.episodeNumber);
        seasonNumber = (int)attributes.getAsInteger(attributeCodes.seasonNumber);
        airDate = attributes.getAsInteger(attributeCodes.airDate);
        seriesId = related.getAsInteger(attributeCodes.seriesId);
        description = attributes.getAsString(attributeCodes.description);
        imageUrl = attributes.getAsString(attributeCodes.imageUrl);
        videoMatchId = related.getAsInteger(attributeCodes.videoId);
        screenplayWriters = attributes.getAsTranslatables(attributeCodes.screenplayWriter);
    }

    /**
     * Constructs an empty, or NULL, instance. Usable as a placeholder or sentinel in place
     * of an invalid general video instance.
     */
    GeneralVideoProduct() {
        super(0L, 0L, 0L, null, null, null, null, AttributeValues.NO_VALUES, AttributeValues.NO_VALUES);
        title = title2 = null;
        productionYear = runTime = parentalRating = episodeNumber = seasonNumber = 0;
        actors = directors = screenplayWriters = new TranslatableValue[0];
        countries = genres = spokenLanguages = subtitleLanguages = new String[0];
        imdbLink = videoCategory = videoFormat = description = imageUrl = null;
        imdbRating = 0.0;
        isAdult = true;
        airDate = seriesId = videoMatchId = 0L;
    }

    /**
     * The clone constructor. The caller guarantees that all the separately given values are exactly the same as those
     * in <code>attributeValues</code> and <code>relatedValues</code>.
     *
     * @param id product ID
     * @param productTypeId product type ID
     * @param partnerId partner ID
     * @param partnerProductCode partner's product code
     * @param added when it was created
     * @param modified when it was last modified
     * @param deleted when it was removed
     * @param attributeValues all attribute values
     * @param relatedValues all related values
     * @param title value of the "title" attribute
     * @param title2 value of the "title2" attribute
     * @param productionYear value of the "production-year" attribute
     * @param actors values of the "actor" attribute
     * @param countries values of the "country" attribute
     * @param directors values of the "director" attribute
     * @param genres values of the "genre" attribute
     * @param imdbLink value of the "imdb-link" attribute
     * @param imdbRating value of the "imdb-rating" attribute
     * @param runTime value of the "run-time" attribute
     * @param spokenLanguages values of the "spoken-language" attribute
     * @param subtitleLanguages values of the "subtitle-language" attribute
     * @param videoCategory value of the "video-category" attribute
     * @param videoFormat value of the "video-format" attribute
     * @param isAdult value of the "is-adult" attribute
     * @param parentalRating value of the "parental-rating" attribute
     * @param episodeNumber value of the "episode-number" attribute
     * @param seasonNumber value of the "season-number" attribute
     * @param airDate value of the "air-date" attribute
     * @param seriesId value of the "series-id" related value
     * @param description value of the "description" attribute
     * @param imageUrl value of the "image-url" attribute
     * @param videoMatchId value of the "video-id" related value
     * @param screenplayWriters values of the "screenplay-writer" attribute
     */
    GeneralVideoProduct(
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
            final TranslatableValue title2,
            final int productionYear,
            final TranslatableValue[] actors,
            final String[] countries,
            final TranslatableValue[] directors,
            final String[] genres,
            final String imdbLink,
            final double imdbRating,
            final int runTime,
            final String[] spokenLanguages,
            final String[] subtitleLanguages,
            final String videoCategory,
            final String videoFormat,
            final boolean isAdult,
            final int parentalRating,
            final int episodeNumber,
            final int seasonNumber,
            final long airDate,
            final long seriesId,
            final String description,
            final String imageUrl,
            final long videoMatchId,
            final TranslatableValue[] screenplayWriters
    ) {
        super(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues, relatedValues);
        this.title = title;
        this.title2 = title2;
        this.productionYear = productionYear;
        this.actors = actors;
        this.countries = countries;
        this.directors = directors;
        this.genres = genres;
        this.imdbLink = imdbLink;
        this.imdbRating = imdbRating;
        this.runTime = runTime;
        this.spokenLanguages = spokenLanguages;
        this.subtitleLanguages = subtitleLanguages;
        this.videoCategory = videoCategory;
        this.videoFormat = videoFormat;
        this.isAdult = isAdult;
        this.parentalRating = parentalRating;
        this.episodeNumber = episodeNumber;
        this.seasonNumber = seasonNumber;
        this.airDate = airDate;
        this.seriesId = seriesId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoMatchId = videoMatchId;
        this.screenplayWriters = screenplayWriters;
    }
}
