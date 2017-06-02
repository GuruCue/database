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
package com.gurucue.recommendations.entity;

import java.io.Serializable;

/**
 * ID class for the entity {@link ProductTypeAttribute}.
 */
public class ProductTypeAttributeId implements Serializable {
    private static final long serialVersionUID = -8329419267464610069L;

    ProductType productType;
    Attribute attribute;

    public ProductTypeAttributeId() {}

    public ProductTypeAttributeId(final ProductType productType, final Attribute attribute) {
        this.productType = productType;
        this.attribute = attribute;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(final ProductType productType) {
        this.productType = productType;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(final Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof ProductTypeAttributeId) {
            ProductTypeAttributeId other = (ProductTypeAttributeId) obj;
            // compare product types
            boolean ret = (this.getProductType() == other.getProductType()) ||
                    ((this.getProductType() != null) && this.getProductType().equals(other.getProductType()));
            // compare attributes
            ret = ret && ((this.getAttribute() == other.getAttribute()) ||
                    ((this.getAttribute() != null) && this.getAttribute().equals(other.getAttribute())));
            return ret;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int result = 17;
        result = 31 * result + (getProductType() == null ? 0 : getProductType().hashCode());
        result = 31 * result + (getAttribute() == null ? 0 : getAttribute().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ProductTypeAttributeId(productType=" + (null == getProductType() ? "null" : getProductType().toString()) +
                ", attribute=" + (null == getAttribute() ? "null" : getAttribute().toString()) +
                ")";
    }
}
