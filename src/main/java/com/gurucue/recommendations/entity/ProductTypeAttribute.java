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
 * Describes a possible attribute for products of a product type.
 */
public class ProductTypeAttribute implements Serializable {
    private static final long serialVersionUID = -2684944028271980520L;

    ProductType productType;
    Attribute attribute;
    Short identLevel;

    public ProductTypeAttribute() {}

    public ProductTypeAttribute(final ProductType productType, final Attribute attribute, final Short identLevel) {
        this.productType = productType;
        this.attribute = attribute;
        this.identLevel = identLevel;
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

    public Short getIdentLevel() {
        return identLevel;
    }

    public void setIdentLevel(final Short identLevel) {
        this.identLevel = identLevel;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof ProductTypeAttribute) {
            ProductTypeAttribute other = (ProductTypeAttribute) obj;
            // compare product types
            boolean ret = (this.getProductType() == other.getProductType()) ||
                    ((this.getProductType() != null) && this.getProductType().equals(other.getProductType()));
            // compare attributes
            ret = ret && ((this.getAttribute() == other.getAttribute()) ||
                    ((this.getAttribute() != null) && this.getAttribute().equals(other.getAttribute())));
            // compare identifying flag
            ret = ret && ((this.getIdentLevel() == other.getIdentLevel()) ||
                    ((this.getIdentLevel() != null) && this.getIdentLevel().equals(other.getIdentLevel())));
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
        result = 31 * result + (getIdentLevel() == null ? 0 : getIdentLevel().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ProductTypeAttribute(productType=" + (null == getProductType() ? "null" : getProductType().toString()) +
                ", attribute=" + (null == getAttribute() ? "null" : getAttribute().toString()) +
                ", identLevel=" + (null == getIdentLevel() ? "null" : getIdentLevel().toString()) +
                ")";
    }
}
