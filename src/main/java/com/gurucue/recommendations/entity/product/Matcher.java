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
import com.gurucue.recommendations.entity.value.AttributeValues;

import java.sql.Timestamp;
import java.util.List;

/**
 * Matcher classes extend this class.
 */
public abstract class Matcher extends Product {
    public Matcher(
            final long id,
            final long productTypeId,
            final long partnerId,
            final String partnerProductCode,
            final Timestamp added,
            final Timestamp modified,
            final Timestamp deleted,
            final AttributeValues attributeValues,
            final AttributeValues relatedValues
    ) {
        super(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributeValues, relatedValues);
    }

    public abstract List<? extends MatcherKey> getKeys();

    public abstract Matcher merge(Matcher matcher, AttributeCodes attributeCodes);

    public abstract boolean contains(Matcher matcher);
}
