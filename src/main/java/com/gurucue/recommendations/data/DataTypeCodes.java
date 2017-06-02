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

import com.gurucue.recommendations.entity.DataType;
import com.gurucue.recommendations.entitymanager.DataTypeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class DataTypeCodes {
    public final DataType rating;
    public final DataType catalogueId;
    public final DataType deviceId;
    public final DataType wasPurchased;
    public final DataType product1;
    public final DataType product2;
    public final DataType deviceType;
    public final DataType watchOffset;
    public final DataType watchDuration;
    public final DataType watchDelay;
    public final DataType contentDuration;
    public final DataType price;
    public final DataType origin;
    public final DataType interactionType;
    public final DataType tvProgrammeId;
    public final DataType tvChannelId;
    public final DataType feedback;
    public final DataType zapCount;
    public final DataType zapOffset;
    public final DataType watchPercentage;
    public final DataType action;
    public final DataType speed;
    public final DataType status;
    public final DataType _errProductType;
    public final DataType _errProductId;
    public final DataType originId;
    public final DataType originCode;
    public final DataType originWait;
    public final DataType contentWatched;
    public final DataType fastForwardDuration;
    public final DataType contentFastForwarded;
    public final DataType rewindDuration;
    public final DataType contentRewound;
    public final DataType playCount;
    public final DataType fastForwardCount;
    public final DataType rewindCount;
    public final DataType stopCount;
    public final DataType pauseCount;
    public final DataType consumptionDuration;
    public final DataType consumptionPercentage;
    public final DataType userProfileId;
    public final DataType viewerCount;
    public final DataType consumerIdList;
    public final DataType zapIdList;

    public final long idForRating;
    public final long idForCatalogueId;
    public final long idForDeviceId;
    public final long idForWasPurchased;
    public final long idForProduct1;
    public final long idForProduct2;
    public final long idForDeviceType;
    public final long idForWatchOffset;
    public final long idForWatchDuration;
    public final long idForWatchDelay;
    public final long idForContentDuration;
    public final long idForPrice;
    public final long idForOrigin;
    public final long idForInteractionType;
    public final long idForTvProgrammeId;
    public final long idForTvChannelId;
    public final long idForFeedback;
    public final long idForZapCount;
    public final long idForZapOffset;
    public final long idForWatchPercentage;
    public final long idForAction;
    public final long idForSpeed;
    public final long idForStatus;
    public final long idFor_errProductType;
    public final long idFor_errProductId;
    public final long idForOriginId;
    public final long idForOriginCode;
    public final long idForOriginWait;
    public final long idForContentWatched;
    public final long idForFastForwardDuration;
    public final long idForContentFastForwarded;
    public final long idForRewindDuration;
    public final long idForContentRewound;
    public final long idForPlayCount;
    public final long idForFastForwardCount;
    public final long idForRewindCount;
    public final long idForStopCount;
    public final long idForPauseCount;
    public final long idForConsumptionDuration;
    public final long idForConsumptionPercentage;
    public final long idForUserProfileId;
    public final long idForViewerCount;
    public final long idForConsumerIdList;
    public final long idForZapIdList;

    private final Map<String, DataType> identifierMapping = new HashMap<>();

    public DataTypeCodes(final DataTypeManager manager) {
        rating = manager.getByIdentifier(DataType.RATING);
        catalogueId = manager.getByIdentifier(DataType.CATALOGUE_ID);
        deviceId = manager.getByIdentifier(DataType.DEVICE_ID);
        wasPurchased = manager.getByIdentifier(DataType.WAS_PURCHASED);
        product1 = manager.getByIdentifier(DataType.PRODUCT1);
        product2 = manager.getByIdentifier(DataType.PRODUCT2);
        deviceType = manager.getByIdentifier(DataType.DEVICE_TYPE);
        watchOffset = manager.getByIdentifier(DataType.WATCH_OFFSET);
        watchDuration = manager.getByIdentifier(DataType.WATCH_DURATION);
        watchDelay = manager.getByIdentifier(DataType.WATCH_DELAY);
        contentDuration = manager.getByIdentifier(DataType.CONTENT_DURATION);
        price = manager.getByIdentifier(DataType.PRICE);
        origin = manager.getByIdentifier(DataType.ORIGIN);
        interactionType = manager.getByIdentifier(DataType.INTERACTION_TYPE);
        tvProgrammeId = manager.getByIdentifier(DataType.TV_PROGRAMME_ID);
        tvChannelId = manager.getByIdentifier(DataType.TV_CHANNEL_ID);
        feedback = manager.getByIdentifier(DataType.FEEDBACK);
        zapCount = manager.getByIdentifier(DataType.ZAP_COUNT);
        zapOffset = manager.getByIdentifier(DataType.ZAP_OFFSET);
        watchPercentage = manager.getByIdentifier(DataType.WATCH_PERCENTAGE);
        action = manager.getByIdentifier(DataType.ACTION);
        speed = manager.getByIdentifier(DataType.SPEED);
        status = manager.getByIdentifier(DataType.STATUS);
        _errProductType = manager.getByIdentifier(DataType._ERR_PRODUCT_TYPE);
        _errProductId = manager.getByIdentifier(DataType._ERR_PRODUCT_ID);
        originId = manager.getByIdentifier(DataType.ORIGIN_ID);
        originCode = manager.getByIdentifier(DataType.ORIGIN_CODE);
        originWait = manager.getByIdentifier(DataType.ORIGIN_WAIT);
        contentWatched = manager.getByIdentifier(DataType.CONTENT_WATCHED);
        fastForwardDuration = manager.getByIdentifier(DataType.FAST_FORWARD_DURATION);
        contentFastForwarded = manager.getByIdentifier(DataType.CONTENT_FAST_FORWARDED);
        rewindDuration = manager.getByIdentifier(DataType.REWIND_DURATION);
        contentRewound = manager.getByIdentifier(DataType.CONTENT_REWOUND);
        playCount = manager.getByIdentifier(DataType.PLAY_COUNT);
        fastForwardCount = manager.getByIdentifier(DataType.FAST_FORWARD_COUNT);
        rewindCount = manager.getByIdentifier(DataType.REWIND_COUNT);
        stopCount = manager.getByIdentifier(DataType.STOP_COUNT);
        pauseCount = manager.getByIdentifier(DataType.PAUSE_COUNT);
        consumptionDuration = manager.getByIdentifier(DataType.CONSUMPTION_DURATION);
        consumptionPercentage = manager.getByIdentifier(DataType.CONSUMPTION_PERCENTAGE);
        userProfileId = manager.getByIdentifier(DataType.USER_PROFILE_ID);
        viewerCount = manager.getByIdentifier(DataType.VIEWER_COUNT);
        consumerIdList = manager.getByIdentifier(DataType.CONSUMER_ID_LIST);
        zapIdList = manager.getByIdentifier(DataType.ZAP_ID_LIST);

        final StringBuilder logBuilder = new StringBuilder();
        idForRating = getId(rating, DataType.RATING, logBuilder);
        idForCatalogueId = getId(catalogueId, DataType.CATALOGUE_ID, logBuilder);
        idForDeviceId = getId(deviceId, DataType.DEVICE_ID, logBuilder);
        idForWasPurchased = getId(wasPurchased, DataType.WAS_PURCHASED, logBuilder);
        idForProduct1 = getId(product1, DataType.PRODUCT1, logBuilder);
        idForProduct2 = getId(product2, DataType.PRODUCT2, logBuilder);
        idForDeviceType = getId(deviceType, DataType.DEVICE_TYPE, logBuilder);
        idForWatchOffset = getId(watchOffset, DataType.WATCH_OFFSET, logBuilder);
        idForWatchDuration = getId(watchDuration, DataType.WATCH_DURATION, logBuilder);
        idForWatchDelay = getId(watchDelay, DataType.WATCH_DELAY, logBuilder);
        idForContentDuration = getId(contentDuration, DataType.CONTENT_DURATION, logBuilder);
        idForPrice = getId(price, DataType.PRICE, logBuilder);
        idForOrigin = getId(origin, DataType.ORIGIN, logBuilder);
        idForInteractionType = getId(interactionType, DataType.INTERACTION_TYPE, logBuilder);
        idForTvProgrammeId = getId(tvProgrammeId, DataType.TV_PROGRAMME_ID, logBuilder);
        idForTvChannelId = getId(tvChannelId, DataType.TV_CHANNEL_ID, logBuilder);
        idForFeedback = getId(feedback, DataType.FEEDBACK, logBuilder);
        idForZapCount = getId(zapCount, DataType.ZAP_COUNT, logBuilder);
        idForZapOffset = getId(zapOffset, DataType.ZAP_OFFSET, logBuilder);
        idForWatchPercentage = getId(watchPercentage, DataType.WATCH_PERCENTAGE, logBuilder);
        idForAction = getId(action, DataType.ACTION, logBuilder);
        idForSpeed = getId(speed, DataType.SPEED, logBuilder);
        idForStatus = getId(status, DataType.STATUS, logBuilder);
        idFor_errProductType = getId(_errProductType, DataType._ERR_PRODUCT_TYPE, logBuilder);
        idFor_errProductId = getId(_errProductId, DataType._ERR_PRODUCT_ID, logBuilder);
        idForOriginId = getId(originId, DataType.ORIGIN_ID, logBuilder);
        idForOriginCode = getId(originCode, DataType.ORIGIN_CODE, logBuilder);
        idForOriginWait = getId(originWait, DataType.ORIGIN_WAIT, logBuilder);
        idForContentWatched = getId(contentWatched, DataType.CONTENT_WATCHED, logBuilder);
        idForFastForwardDuration = getId(fastForwardDuration, DataType.FAST_FORWARD_DURATION, logBuilder);
        idForContentFastForwarded = getId(contentFastForwarded, DataType.CONTENT_FAST_FORWARDED, logBuilder);
        idForRewindDuration = getId(rewindDuration, DataType.REWIND_DURATION, logBuilder);
        idForContentRewound = getId(contentRewound, DataType.CONTENT_REWOUND, logBuilder);
        idForPlayCount = getId(playCount, DataType.PLAY_COUNT, logBuilder);
        idForFastForwardCount = getId(fastForwardCount, DataType.FAST_FORWARD_COUNT, logBuilder);
        idForRewindCount = getId(rewindCount, DataType.REWIND_COUNT, logBuilder);
        idForStopCount = getId(stopCount, DataType.STOP_COUNT, logBuilder);
        idForPauseCount = getId(pauseCount, DataType.PAUSE_COUNT, logBuilder);
        idForConsumptionDuration = getId(consumptionDuration, DataType.CONSUMPTION_DURATION, logBuilder);
        idForConsumptionPercentage = getId(consumptionPercentage, DataType.CONSUMPTION_PERCENTAGE, logBuilder);
        idForUserProfileId = getId(userProfileId, DataType.USER_PROFILE_ID, logBuilder);
        idForViewerCount = getId(viewerCount, DataType.VIEWER_COUNT, logBuilder);
        idForConsumerIdList = getId(consumerIdList, DataType.CONSUMER_ID_LIST, logBuilder);
        idForZapIdList = getId(zapIdList, DataType.ZAP_ID_LIST, logBuilder);

        for (final DataType t : manager.list()) {
            identifierMapping.put(t.getIdentifier(), t);
        }

        if (logBuilder.length() > 0) {
            final Logger logger = LogManager.getLogger(ConsumerEventTypeCodes.class);
            logger.error("NOT ALL DATA-TYPE CODES ARE PRESENT IN THE DATABASE!\n======================================================================\nDatabase is missing the following data_type definitions: " + logBuilder.toString() + "\n======================================================================");
        }
    }

    public final DataType byIdentifier(final String identifier) {
        return identifierMapping.get(identifier);
    }

    private static long getId(final DataType dataType, final String typeName, final StringBuilder logBuilder) {
        if ((dataType == null) || (dataType.getId() == null)) {
            if (logBuilder.length() > 0) logBuilder.append(", ");
            logBuilder.append(typeName);
            return -1L;
        }
        return dataType.getId();
    }
}
