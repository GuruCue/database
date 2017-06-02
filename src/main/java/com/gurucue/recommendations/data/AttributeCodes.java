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
package com.gurucue.recommendations.data;

import com.gurucue.recommendations.entity.Attribute;
import com.gurucue.recommendations.entitymanager.AttributeManager;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Contains attribute instances and codes for all the relevant attributes.
 * This is to minimise attribute lookup costs, which are fairly frequent
 * throughout the code.
 *
 * Attributes are considered constant during application runtime, therefore
 * this solution is possible.
 */
public final class AttributeCodes {
    public final Attribute genre;
    public final Attribute productionYear;
    public final Attribute runTime;
    public final Attribute actor;
    public final Attribute director;
    public final Attribute voice;
    public final Attribute title;
    public final Attribute coverPicture;
    public final Attribute imdbLink;
    public final Attribute imdbRating;
    public final Attribute isAdult;
    public final Attribute airDate;
    public final Attribute episodeNumber;
    public final Attribute seasonNumber;
    public final Attribute seriesId;
    public final Attribute partNumber;
    public final Attribute title2;
    public final Attribute tvChannel;
    public final Attribute country;
    public final Attribute videoCategory;
    public final Attribute spokenLanguage;
    public final Attribute beginTime;
    public final Attribute endTime;
    public final Attribute videoFormat;
    public final Attribute catalogueId;
    public final Attribute price;
    public final Attribute subtitleLanguage;
    public final Attribute videoId;
    @Deprecated // replaced by price
    public final Attribute cataloguePrice;
    @Deprecated // replaced by validity
    public final Attribute catalogueExpiry;
    public final Attribute catchupHours;
    public final Attribute tvChannelId;
    @Deprecated // replaced by validity
    public final Attribute expiry;
    public final Attribute packageType;
    public final Attribute tvodId;
    public final Attribute svodId;
    public final Attribute interactiveId;
    public final Attribute categoryId;
    public final Attribute parentalRating;
    public final Attribute isSubscribed;
    public final Attribute packageId;
    public final Attribute description;
    public final Attribute imageUrl;
    public final Attribute vodCategory;
    public final Attribute validity;
    public final Attribute isSeries;
    public final Attribute screenplayWriter;
    public final Attribute matchedAttribute;

    public final long idForGenre;
    public final long idForProductionYear;
    public final long idForRunTime;
    public final long idForActor;
    public final long idForDirector;
    public final long idForVoice;
    public final long idForTitle;
    public final long idForCoverPicture;
    public final long idForImdbLink;
    public final long idForImdbRating;
    public final long idForIsAdult;
    public final long idForAirDate;
    public final long idForEpisodeNumber;
    public final long idForSeasonNumber;
    public final long idForSeriesId;
    public final long idForPartNumber;
    public final long idForTitle2;
    public final long idForTvChannel;
    public final long idForCountry;
    public final long idForVideoCategory;
    public final long idForSpokenLanguage;
    public final long idForBeginTime;
    public final long idForEndTime;
    public final long idForVideoFormat;
    public final long idForCatalogueId;
    public final long idForPrice;
    public final long idForSubtitleLanguage;
    public final long idForVideoId;
    @Deprecated // replaced by idForPrice
    public final long idForCataloguePrice;
    @Deprecated // replaced by idForValidity
    public final long idForCatalogueExpiry;
    public final long idForCatchupHours;
    public final long idForTvChannelId;
    @Deprecated // replaced by idForValidity
    public final long idForExpiry;
    public final long idForPackageType;
    public final long idForTvodId;
    public final long idForSvodId;
    public final long idForInteractiveId;
    public final long idForCategoryId;
    public final long idForParentalRating;
    public final long idForIsSubscribed;
    public final long idForPackageId;
    public final long idForDescription;
    public final long idForImageUrl;
    public final long idForVodCategory;
    public final long idForValidity;
    public final long idForIsSeries;
    public final long idForScreenplayWriter;
    public final long idForMatchedAttribute;

    private final Map<String, Attribute> identifierMapping = new HashMap<>();
    private final TLongObjectMap<Attribute> idMapping = new TLongObjectHashMap<>();

    public AttributeCodes(final AttributeManager attributeManager) {
        final List<String> missingAttributes = new ArrayList<>();

        genre = getNonNull(attributeManager, Attribute.GENRE, missingAttributes);
        productionYear = getNonNull(attributeManager, Attribute.PRODUCTION_YEAR, missingAttributes);
        runTime = getNonNull(attributeManager, Attribute.RUN_TIME, missingAttributes);
        actor = getNonNull(attributeManager, Attribute.ACTOR, missingAttributes);
        director = getNonNull(attributeManager, Attribute.DIRECTOR, missingAttributes);
        voice = getNonNull(attributeManager, Attribute.VOICE, missingAttributes);
        title = getNonNull(attributeManager, Attribute.TITLE, missingAttributes);
        coverPicture = getNonNull(attributeManager, Attribute.COVER_PICTURE, missingAttributes);
        imdbLink = getNonNull(attributeManager, Attribute.IMDB_LINK, missingAttributes);
        imdbRating = getNonNull(attributeManager, Attribute.IMDB_RATING, missingAttributes);
        isAdult = getNonNull(attributeManager, Attribute.IS_ADULT, missingAttributes);
        airDate = getNonNull(attributeManager, Attribute.AIR_DATE, missingAttributes);
        episodeNumber = getNonNull(attributeManager, Attribute.EPISODE_NUMBER, missingAttributes);
        seasonNumber = getNonNull(attributeManager, Attribute.SEASON_NUMBER, missingAttributes);
        seriesId = getNonNull(attributeManager, Attribute.SERIES_ID, missingAttributes);
        partNumber = getNonNull(attributeManager, Attribute.PART_NUMBER, missingAttributes);
        title2 = getNonNull(attributeManager, Attribute.TITLE2, missingAttributes);
        tvChannel = getNonNull(attributeManager, Attribute.TV_CHANNEL, missingAttributes);
        country = getNonNull(attributeManager, Attribute.COUNTRY, missingAttributes);
        videoCategory = getNonNull(attributeManager, Attribute.VIDEO_CATEGORY, missingAttributes);
        spokenLanguage = getNonNull(attributeManager, Attribute.SPOKEN_LANGUAGE, missingAttributes);
        beginTime = getNonNull(attributeManager, Attribute.BEGIN_TIME, missingAttributes);
        endTime = getNonNull(attributeManager, Attribute.END_TIME, missingAttributes);
        videoFormat = getNonNull(attributeManager, Attribute.VIDEO_FORMAT, missingAttributes);
        catalogueId = getNonNull(attributeManager, Attribute.CATALOGUE_ID, missingAttributes);
        price = getNonNull(attributeManager, Attribute.PRICE, missingAttributes);
        subtitleLanguage = getNonNull(attributeManager, Attribute.SUBTITLE_LANGUAGE, missingAttributes);
        videoId = getNonNull(attributeManager, Attribute.VIDEO_ID, missingAttributes);
        cataloguePrice = getNonNull(attributeManager, Attribute.CATALOGUE_PRICE, missingAttributes);
        catalogueExpiry = getNonNull(attributeManager, Attribute.CATALOGUE_EXPIRY, missingAttributes);
        catchupHours = getNonNull(attributeManager, Attribute.CATCHUP_HOURS, missingAttributes);
        tvChannelId = getNonNull(attributeManager, Attribute.TV_CHANNEL_ID, missingAttributes);
        expiry = getNonNull(attributeManager, Attribute.EXPIRY, missingAttributes);
        packageType = getNonNull(attributeManager, Attribute.PACKAGE_TYPE, missingAttributes);
        tvodId = getNonNull(attributeManager, Attribute.TVOD_ID, missingAttributes);
        svodId = getNonNull(attributeManager, Attribute.SVOD_ID, missingAttributes);
        interactiveId = getNonNull(attributeManager, Attribute.INTERACTIVE_ID, missingAttributes);
        categoryId = getNonNull(attributeManager, Attribute.CATEGORY_ID, missingAttributes);
        parentalRating = getNonNull(attributeManager, Attribute.PARENTAL_RATING, missingAttributes);
        isSubscribed = getNonNull(attributeManager, Attribute.IS_SUBSCRIBED, missingAttributes);
        packageId = getNonNull(attributeManager, Attribute.PACKAGE_ID, missingAttributes);
        description = getNonNull(attributeManager, Attribute.DESCRIPTION, missingAttributes);
        imageUrl = getNonNull(attributeManager, Attribute.IMAGE_URL, missingAttributes);
        vodCategory = getNonNull(attributeManager, Attribute.VOD_CATEGORY, missingAttributes);
        validity = getNonNull(attributeManager, Attribute.VALIDITY, missingAttributes);
        isSeries = getNonNull(attributeManager, Attribute.IS_SERIES, missingAttributes);
        screenplayWriter = getNonNull(attributeManager, Attribute.SCREENPLAY_WRITER, missingAttributes);
        matchedAttribute = getNonNull(attributeManager, Attribute.MATCHED_ATTRIBUTE, missingAttributes);

        final Iterator<String> missingIterator = missingAttributes.iterator();
        if (missingIterator.hasNext()) {
            final StringBuilder errorBuilder = new StringBuilder(100 + missingAttributes.size() * 40);
            errorBuilder.append("Database is missing the following attribute definition(s): ");
            errorBuilder.append(missingIterator.next());
            while (missingIterator.hasNext()) {
                errorBuilder.append(", ").append(missingIterator.next());
            }
            throw new IllegalStateException(errorBuilder.toString());
        }

        idForGenre = genre.getId();
        idForProductionYear = productionYear.getId();
        idForRunTime = runTime.getId();
        idForActor = actor.getId();
        idForDirector = director.getId();
        idForVoice = voice.getId();
        idForTitle = title.getId();
        idForCoverPicture = coverPicture.getId();
        idForImdbLink = imdbLink.getId();
        idForImdbRating = imdbRating.getId();
        idForIsAdult = isAdult.getId();
        idForAirDate = airDate.getId();
        idForEpisodeNumber = episodeNumber.getId();
        idForSeasonNumber = seasonNumber.getId();
        idForSeriesId = seriesId.getId();
        idForPartNumber = partNumber.getId();
        idForTitle2 = title2.getId();
        idForTvChannel = tvChannel.getId();
        idForCountry = country.getId();
        idForVideoCategory = videoCategory.getId();
        idForSpokenLanguage = spokenLanguage.getId();
        idForBeginTime = beginTime.getId();
        idForEndTime = endTime.getId();
        idForVideoFormat = videoFormat.getId();
        idForCatalogueId = catalogueId.getId();
        idForPrice = price.getId();
        idForSubtitleLanguage = subtitleLanguage.getId();
        idForVideoId = videoId.getId();
        idForCataloguePrice = cataloguePrice.getId();
        idForCatalogueExpiry = catalogueExpiry.getId();
        idForCatchupHours = catchupHours.getId();
        idForTvChannelId = tvChannelId.getId();
        idForExpiry = expiry.getId();
        idForPackageType = packageType.getId();
        idForTvodId = tvodId.getId();
        idForSvodId = svodId.getId();
        idForInteractiveId = interactiveId.getId();
        idForCategoryId = categoryId.getId();
        idForParentalRating = parentalRating.getId();
        idForIsSubscribed = parentalRating.getId();
        idForPackageId = packageId.getId();
        idForDescription = description.getId();
        idForImageUrl = imageUrl.getId();
        idForVodCategory = vodCategory.getId();
        idForValidity = validity.getId();
        idForIsSeries = isSeries.getId();
        idForScreenplayWriter = screenplayWriter.getId();
        idForMatchedAttribute = matchedAttribute.getId();

        for (final Attribute a : attributeManager.list()) {
            identifierMapping.put(a.getIdentifier(), a);
            idMapping.put(a.getId(), a);
        }
    }

    public final Attribute byIdentifier(final String identifier) {
        return identifierMapping.get(identifier);
    }

    public final Attribute byId(final long id) {
        return idMapping.get(id);
    }

    private static Attribute getNonNull(final AttributeManager attributeManager, final String attributeName, final List<String> missingAttributes) {
        final Attribute a = attributeManager.getByIdentifier(attributeName);
        if (a == null) missingAttributes.add(attributeName);
        return a;
    }
}
