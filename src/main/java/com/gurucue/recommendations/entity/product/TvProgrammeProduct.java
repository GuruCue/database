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
import java.util.Arrays;

/**
 * TV-programme product abstraction.
 */
public final class TvProgrammeProduct extends GeneralVideoProduct {

    public final String[] tvChannelCodes;
    public final long beginTimeMillis;
    public final long endTimeMillis;

    public TvProgrammeProduct(
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
        super(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues, relatedValues, provider);
        final AttributeCodes attributeCodes = provider.getAttributeCodes();
        tvChannelCodes = attributes.getAsStrings(attributeCodes.tvChannel);
        if ((tvChannelCodes != null) && (tvChannelCodes.length > 1)) Arrays.sort(tvChannelCodes);
        beginTimeMillis = attributes.getAsInteger(attributeCodes.beginTime);
        endTimeMillis = attributes.getAsInteger(attributeCodes.endTime);
    }

    /**
     * Constructs an empty, or NULL, instance. Usable as a placeholder or sentinel in place
     * of an invalid tv-programme instance.
     */
    public TvProgrammeProduct() {
        super();
        tvChannelCodes = new String[0];
        beginTimeMillis = endTimeMillis = 0L;
    }

    private TvProgrammeProduct(
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
            final TranslatableValue[] screenplayWriters,
            final String[] tvChannelCodes,
            final long beginTimeMillis,
            final long endTimeMillis
    ) {
        super(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues,
                relatedValues, title, title2, productionYear, actors, countries, directors, genres, imdbLink,
                imdbRating, runTime, spokenLanguages, subtitleLanguages, videoCategory, videoFormat, isAdult,
                parentalRating, episodeNumber, seasonNumber, airDate, seriesId, description, imageUrl, videoMatchId,
                screenplayWriters);
        this.tvChannelCodes = tvChannelCodes;
        this.beginTimeMillis = beginTimeMillis;
        this.endTimeMillis = endTimeMillis;
    }

    public TvProgrammeProduct cloneAsNew(final long newId) {
        return new TvProgrammeProduct(newId, productTypeId, partnerId, partnerProductCode, added, modified, deleted,
                attributes, related, title, title2, productionYear, actors, countries, directors, genres, imdbLink,
                imdbRating, runTime, spokenLanguages, subtitleLanguages, videoCategory, videoFormat, isAdult,
                parentalRating, episodeNumber, seasonNumber, airDate, seriesId, description, imageUrl, videoMatchId,
                screenplayWriters, tvChannelCodes, beginTimeMillis, endTimeMillis);
    }
}
