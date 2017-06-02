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

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.data.DataProvider;
import com.gurucue.recommendations.data.ProductTypeCodes;
import com.gurucue.recommendations.Id;
import com.gurucue.recommendations.entity.value.AttributeValues;

import java.sql.Timestamp;

/**
 * The basic Product abstraction. Generally there should be a Product
 * descendant defined for each product type, defining its own static
 * fields for relevant attribute values for fast access.
 */
public class Product implements Id<Long> {
    public final long id;
    public final long productTypeId;
    public final long partnerId;
    public final String partnerProductCode;
    public final Timestamp added;
    public final Timestamp modified;
    public final Timestamp deleted;
    public final AttributeValues attributes;
    public final AttributeValues related;

    public Product(final long id, final long productTypeId, final long partnerId, final String partnerProductCode, final Timestamp added, final Timestamp modified, final Timestamp deleted, final AttributeValues attributeValues, final AttributeValues relatedValues) {
        this.id = id;
        this.productTypeId = productTypeId;
        this.partnerId = partnerId;
        this.partnerProductCode = partnerProductCode;
        this.added = added;
        this.modified = modified;
        this.deleted = deleted;
        this.attributes = attributeValues;
        this.related = relatedValues;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        // no Product with a null ID is equal to any other instance
        return (id != 0L) && (obj instanceof Product) && (((Product)obj).id == id);
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        return (int)(id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(100);
        toString(sb);
        return sb.toString();
    }

    public void toString(final StringBuilder output) {
        output.append("Product(id=");
        if (id == 0L) output.append("(null)");
        else output.append(id);
        output.append(", productTypeId=");
        if (productTypeId == 0L) output.append("(null)");
        else output.append(productTypeId);
        output.append(", partnerId=");
        if (partnerId < 0L) output.append("(null)");
        else output.append(partnerId);
        output.append(", partnerProductCode=");
        if (partnerProductCode == null) output.append("(null)");
        else {
            output.append("\"");
            output.append(partnerProductCode.replace("\"", "\\\""));
            output.append("\"");
        }
        output.append(", added=");
        if (added == null) output.append("(null)");
        else {
            output.append("\"");
            output.append(added.toString());
            output.append("\"");
        }
        output.append(", deleted=");
        if (deleted == null) output.append("(null)");
        else {
            output.append("\"");
            output.append(deleted.toString());
            output.append("\"");
        }
        output.append(")");
    }

    public Product cloneAsNew(final DataProvider provider, final long newId) {
        return Product.create(newId, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
    }

    public Product cloneAsNew(final DataProvider provider, final long newId, final Timestamp added, final Timestamp modified, final Timestamp deleted) {
        return Product.create(newId, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
    }

    public static Product create(
            final long id,
            final long productTypeId,
            final long partnerId,
            final String partnerProductCode,
            final Timestamp added,
            final Timestamp modified,
            final Timestamp deleted,
            final String jsonAttributes,
            final String jsonRelated,
            final DataProvider provider,
            final Appendable log
    ) throws ResponseException {
        final AttributeValues attributes = AttributeValues.fromJson(jsonAttributes, provider, log);
        final AttributeValues related = AttributeValues.fromJson(jsonRelated, provider, log);
        return create(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
    }

    public static Product create(
            final long id,
            final long productTypeId,
            final long partnerId,
            final String partnerProductCode,
            final Timestamp added,
            final Timestamp modified,
            final Timestamp deleted,
            final AttributeValues attributes,
            final AttributeValues related,
            final DataProvider provider
    ) {
        final ProductTypeCodes productTypeCodes = provider.getProductTypeCodes();
        if (partnerId == 0L) {
            if (productTypeId == productTypeCodes.idForVideo)
                return new VideoMatch(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
            if (productTypeId == productTypeCodes.idForSeries)
                return new SeriesMatch(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
        }
        else {
            if (productTypeId == productTypeCodes.idForTvProgramme)
                return new TvProgrammeProduct(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
            if (productTypeId == productTypeCodes.idForVideo)
                return new VideoProduct(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
            if (productTypeId == productTypeCodes.idForPackage)
                return new PackageProduct(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
            if (productTypeId == productTypeCodes.idForTvChannel)
                return new TvChannelProduct(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
            if ((productTypeId == productTypeCodes.idForSvod) || (productTypeId == productTypeCodes.idForTvod))
                return new VodProduct(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related, provider);
        }
        return new Product(id, productTypeId, partnerId, partnerProductCode, added, modified, deleted, attributes, related);
    }

    @Override
    public Long getId() {
        return id == 0L ? null : id;
    }
}
