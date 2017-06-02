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

public final class TvChannelProduct extends Product {
    public final TranslatableValue title;
    public final boolean isAdult; // default: false
    public final String[] subtitleLanguages;
    public final String[] spokenLanguages;
    public final String videoFormat;
    public final int catchupHours;
    public final long catchupMillis;

    public TvChannelProduct(
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
        isAdult = attributes.getAsBoolean(attributeCodes.isAdult);
        subtitleLanguages = attributes.getAsStrings(attributeCodes.subtitleLanguage);
        spokenLanguages = attributes.getAsStrings(attributeCodes.spokenLanguage);
        videoFormat = attributes.getAsString(attributeCodes.videoFormat);
        catchupHours = (int)attributes.getAsInteger(attributeCodes.catchupHours);
        catchupMillis = catchupHours * 3600000L;
    }
}
