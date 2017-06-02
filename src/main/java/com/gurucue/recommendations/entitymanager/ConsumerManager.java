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
package com.gurucue.recommendations.entitymanager;

import com.gurucue.recommendations.dto.ConsumerEntity;
import com.gurucue.recommendations.dto.RelationConsumerProductEntity;

import java.util.List;

/**
 * {@link com.gurucue.recommendations.dto.ConsumerEntity} management methods.
 */
public interface ConsumerManager {
    /**
     * Retrieves the consumer having the given ID.
     * @param id consumer ID
     * @return the consumer with the given ID, or null if there is no such consumer
     */
    ConsumerEntity getById(long id);

    /**
     * Retrieves the consumer having the given username with the given partner.
     * @param partnerId the partner that created this consumer
     * @param username the username of the consumer
     * @param consumerTypeId the type of the consumer
     * @param parentId ID of the parent consumer, or 0L if there is no parent
     * @return the resulting consumer instance, or null if there is no such consumer
     */
    ConsumerEntity getByPartnerIdAndUsernameAndTypeAndParent(long partnerId, String username, long consumerTypeId, long parentId);

    /**
     * Returns the list of all consumers.
     * @return the list of all active consumers
     */
    List<ConsumerEntity> list();

    /**
     * Either creates a new consumer with the given username with the given partner
     * and assigns given relations to it, or if the consumer already exists then it
     * merges the given relations to existing relations. If the <code>resetEvents</code>
     * flag is set, then physically a new consumer with the same username and
     * partner is created, and the old one is flagged as inactive, to start a new
     *
     * @param partnerId the partner that created this consumer
     * @param username the username of the consumer
     * @param resetEvents whether to discard events of this consumer (a new consumer instance with a new ID gets created)
     * @param relations product relations of this consumer (e.g. package subscriptions)
     * @param consumerTypeId the type of the consumer
     * @param parentId ID of the parent consumer, or 0L if there is no parent
     * @return the resulting consumer instance
     */
    ConsumerEntity merge(long partnerId, String username, boolean resetEvents, List<RelationConsumerProductEntity> relations, long consumerTypeId, long parentId);

    /**
     * Either creates a new consumer with the given username with the given partner
     * and assigns given relations to it, or if the consumer already exists then it
     * updates the current relations with the given relations. If the <code>resetEvents</code>
     * flag is set, then physically a new consumer with the same username and
     * partner is created, and the old one is flagged as inactive, to start a new
     * <br>
     * The given <code>relations</code> must be sorted ascending by <code>relationStart</code>.
     * The resulting consumer will retain history of subscriptions up till the oldest
     * among the given <code>relations</code>, afterwards the given <code>relations</code>
     * will be set as active. Any existing subscriptions that are not among the given
     * <code>relations</code> will have their <code>relationEnd</code> set to the moment
     * of the request.
     *
     * @param partnerId the partner that created this consumer
     * @param username the username of the consumer
     * @param resetEvents whether to discard events of this consumer (a new consumer instance with a new ID gets created)
     * @param relations product relations of this consumer (e.g. package subscriptions)
     * @param consumerTypeId the type of the consumer
     * @param parentId ID of the parent consumer, or 0L if there is no parent
     * @return the resulting consumer instance
     */
    ConsumerEntity update(long partnerId, String username, boolean resetEvents, List<RelationConsumerProductEntity> relations, long consumerTypeId, long parentId);

    /**
     * Deletes the consumer having the given username with the given partner,
     * and returns it. If no such consumer exists, then a <code>null</code>
     * is returned.
     *
     * @param partnerId the partner that created this consumer
     * @param username the username of the consumer
     * @param consumerTypeId the type of the consumer
     * @param parentId ID of the parent consumer, or 0L if there is no parent
     * @param delayedAnonymization whether to delay anonymization
     * @return the deleted consumer instance
     */
    ConsumerEntity delete(long partnerId, String username, long consumerTypeId, long parentId, boolean delayedAnonymization);

    /**
     * Returns a list of all currently active children of the consumer with the
     * given consumer ID.
     *
     * @param consumerId the ID of the parent consumer
     * @param forUpdate whether to lock the rows in the database
     * @return a list of children consumers
     */
    List<ConsumerEntity> getActiveChildren(long consumerId, boolean forUpdate);
}
