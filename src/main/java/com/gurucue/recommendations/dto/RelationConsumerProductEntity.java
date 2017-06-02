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

public class RelationConsumerProductEntity extends Entity {
    public final long consumerId;
    public final long productId;
    public final long relationTypeId;
    public final long relationStart; // timestamp, negative means null
    public final long relationEnd; // timestamp, negative means null

    public RelationConsumerProductEntity(final long id, final long consumerId, final long productId, final long relationTypeId, final long relationStart, final long relationEnd) {
        super(id);
        this.consumerId = consumerId;
        this.productId = productId;
        this.relationTypeId = relationTypeId;
        this.relationStart = relationStart;
        this.relationEnd = relationEnd;
    }
}
