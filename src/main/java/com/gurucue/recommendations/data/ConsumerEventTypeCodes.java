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

import com.gurucue.recommendations.entity.ConsumerEventType;
import com.gurucue.recommendations.entitymanager.ConsumerEventTypeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class ConsumerEventTypeCodes {
    public final ConsumerEventType rating;
    public final ConsumerEventType consumption;
    public final ConsumerEventType productPairVote;
    public final ConsumerEventType purchase;
    public final ConsumerEventType zap;
    public final ConsumerEventType interaction;
    public final ConsumerEventType liveTvConsumption;
    public final ConsumerEventType feedback;
    public final ConsumerEventType offlineConsumption;
    public final ConsumerEventType offlineLiveTvConsumption;
    public final ConsumerEventType viewership;

    public final long idForRating;
    public final long idForConsumption;
    public final long idForProductPairVote;
    public final long idForPurchase;
    public final long idForZap;
    public final long idForInteraction;
    public final long idForLiveTvConsumption;
    public final long idForFeedback;
    public final long idForOfflineConsumption;
    public final long idForOfflineLiveTvConsumption;
    public final long idForViewership;

    private final Map<String, ConsumerEventType> identifierMapping = new HashMap<>();

    public ConsumerEventTypeCodes(final ConsumerEventTypeManager manager) {
        rating = manager.getByIdentifier(ConsumerEventType.RATING);
        consumption = manager.getByIdentifier(ConsumerEventType.CONSUMPTION);
        productPairVote = manager.getByIdentifier(ConsumerEventType.PRODUCT_PAIR_VOTE);
        purchase = manager.getByIdentifier(ConsumerEventType.PURCHASE);
        zap = manager.getByIdentifier(ConsumerEventType.ZAP);
        interaction = manager.getByIdentifier(ConsumerEventType.INTERACTION);
        liveTvConsumption = manager.getByIdentifier(ConsumerEventType.LIVE_TV_CONSUMPTION);
        feedback = manager.getByIdentifier(ConsumerEventType.FEEDBACK);
        offlineConsumption = manager.getByIdentifier(ConsumerEventType.OFFLINE_CONSUMPTION);
        offlineLiveTvConsumption = manager.getByIdentifier(ConsumerEventType.OFFLINE_LIVE_TV_CONSUMPTION);
        viewership = manager.getByIdentifier(ConsumerEventType.VIEWERSHIP);

        final StringBuilder logBuilder = new StringBuilder();
        idForRating = getId(rating, ConsumerEventType.RATING, logBuilder);
        idForConsumption = getId(consumption, ConsumerEventType.CONSUMPTION, logBuilder);
        idForProductPairVote = getId(productPairVote, ConsumerEventType.PRODUCT_PAIR_VOTE, logBuilder);
        idForPurchase = getId(purchase, ConsumerEventType.PURCHASE, logBuilder);
        idForZap = getId(zap, ConsumerEventType.ZAP, logBuilder);
        idForInteraction = getId(interaction, ConsumerEventType.INTERACTION, logBuilder);
        idForLiveTvConsumption = getId(liveTvConsumption, ConsumerEventType.LIVE_TV_CONSUMPTION, logBuilder);
        idForFeedback = getId(feedback, ConsumerEventType.FEEDBACK, logBuilder);
        idForOfflineConsumption = getId(offlineConsumption, ConsumerEventType.OFFLINE_CONSUMPTION, logBuilder);
        idForOfflineLiveTvConsumption = getId(offlineLiveTvConsumption, ConsumerEventType.OFFLINE_LIVE_TV_CONSUMPTION, logBuilder);
        idForViewership = getId(viewership, ConsumerEventType.VIEWERSHIP, logBuilder);

        for (final ConsumerEventType cet : manager.list()) {
            identifierMapping.put(cet.getIdentifier(), cet);
        }

        if (logBuilder.length() > 0) {
            final Logger logger = LogManager.getLogger(ConsumerEventTypeCodes.class);
            logger.error("NOT ALL CONSUMER-EVENT-TYPE CODES ARE PRESENT IN THE DATABASE!\n======================================================================\nDatabase is missing the following consumer_event_type definitions: " + logBuilder.toString() + "\n======================================================================");
        }
    }

    public final ConsumerEventType byIdentifier(final String identifier) {
        return identifierMapping.get(identifier);
    }

    private static long getId(final ConsumerEventType consumerEventType, final String typeName, final StringBuilder logBuilder) {
        if ((consumerEventType == null) || (consumerEventType.getId() == null)) {
            if (logBuilder.length() > 0) logBuilder.append(", ");
            logBuilder.append(typeName);
            return -1L;
        }
        return consumerEventType.getId();
    }
}
