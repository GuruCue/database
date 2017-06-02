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
import com.gurucue.recommendations.entity.value.TimestampIntervalValue;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Video product abstraction.
 */
public final class VideoProduct extends GeneralVideoProduct {

    public final String catalogueId;
    public final double price; // 0 -> null
    public final String[] vodCategories;
    public final TimestampIntervalValue[] validities; // ordered from the earliest to the latest considering end-time

    public VideoProduct(
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
        catalogueId = attributes.getAsString(attributeCodes.catalogueId);
        price = attributes.getAsFloat(attributeCodes.price);
        vodCategories = attributes.getAsStrings(attributeCodes.vodCategory);
        validities = attributes.getAsTimestampIntervals(attributeCodes.validity);
        sortValidities(validities);
    }

    /**
     * Sorts validities ascending according to their end-time.
     * @param validities array of validities to sort in-place
     */
    public static void sortValidities(final TimestampIntervalValue[] validities) {
        Arrays.sort(validities, ValidityComparator.INSTANCE);
    }

    static final class ValidityComparator implements Comparator<TimestampIntervalValue> {
        public static final ValidityComparator INSTANCE = new ValidityComparator();

        @Override
        public int compare(final TimestampIntervalValue o1, final TimestampIntervalValue o2) {
            if (o1.endMillis < o2.endMillis) return -1;
            if (o1.endMillis > o2.endMillis) return 1;
            if (o1.beginMillis < o2.beginMillis) return -1;
            if (o1.beginMillis > o2.beginMillis) return 1;
            return 0;
        }
    }
}
