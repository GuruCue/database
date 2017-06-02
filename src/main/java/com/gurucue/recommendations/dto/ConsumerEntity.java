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
package com.gurucue.recommendations.dto;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.Collections;
import java.util.List;

public class ConsumerEntity extends Entity {
    public final String username;
    public final long partnerId;
    public final long activated; // timestamp
    public final long deleted; // timestamp
    public final short status;
    public final List<RelationConsumerProductEntity> relations;
    public final long consumerTypeId;
    public final long parentId; // parent consumer ID

    public ConsumerEntity(
            final long id,
            final String username,
            final long partnerId,
            final long activated,
            final long deleted,
            final short status,
            final List<RelationConsumerProductEntity> relations,
            final long consumerTypeId,
            final long parentId
    ) {
        super(id);
        this.username = username;
        this.partnerId = partnerId;
        this.activated = activated;
        this.deleted = deleted;
        this.status = status;
        if ((relations == null) || relations.isEmpty()) this.relations = Collections.emptyList();
//        else this.relations = Collections.unmodifiableList(relations);
        else this.relations = relations;
        this.consumerTypeId = consumerTypeId;
        this.parentId = parentId;
    }

    /**
     * Returns the set of product IDs from active relations.
     *
     * @param activeAtTimestampMillis the timestamp at which the relations should be active, in milliseconds
     * @return the set of product IDs from relations active at the specified timestamp
     */
    public TLongSet activeRelationProductIds(final long activeAtTimestampMillis) {
        final TLongSet result = new TLongHashSet();
        relations.forEach((final RelationConsumerProductEntity relation) -> {
            if ((relation.relationStart <= activeAtTimestampMillis) && ((relation.relationEnd <= 0L) || (relation.relationEnd > activeAtTimestampMillis))) {
                // the consumer's relation is active
                result.add(relation.productId);
            }
        });
        return result;
    }
}
