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

@Deprecated
public final class ProductAttributeTranslation implements Serializable {
    private static final long serialVersionUID = -5336492511024709012L;

    Long id;
    ProductAttribute productAttribute;
    Language language;
    String translatedValue;

    public ProductAttributeTranslation() {}

    public ProductAttributeTranslation(final Long id, final ProductAttribute productAttribute, final Language language, final String translatedValue) {
        this.id = id;
        this.productAttribute = productAttribute;
        this.language = language;
        this.translatedValue = translatedValue;
    }

    /**
     * Copy constructor.
     *
     * @param existingTranslation translation to copy
     */
    public ProductAttributeTranslation(final ProductAttributeTranslation existingTranslation) {
        this.id = existingTranslation.getId();
        this.productAttribute = existingTranslation.getProductAttribute();
        this.language = existingTranslation.getLanguage();
        this.translatedValue = existingTranslation.getTranslatedValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ProductAttribute getProductAttribute() {
        return productAttribute;
    }

    public void setProductAttribute(final ProductAttribute productAttribute) {
        this.productAttribute = productAttribute;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(final Language language) {
        this.language = language;
    }

    public String getTranslatedValue() {
        return translatedValue;
    }

    public void setTranslatedValue(final String translatedValue) {
        this.translatedValue = translatedValue;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof ProductAttributeTranslation) {
            ProductAttributeTranslation other = (ProductAttributeTranslation) obj;
            // compare IDs
            boolean ret = (this.getId() == other.getId()) ||
                    ((this.getId() != null) && this.getId().equals(other.getId()));
            // compare product attributes
            ret = ret && ((this.getProductAttribute() == other.getProductAttribute()) ||
                ((this.getProductAttribute() != null) && this.getProductAttribute().equals(other.getProductAttribute())));
            // compare languages
            ret = ret && ((this.getLanguage() == other.getLanguage()) ||
                ((this.getLanguage() != null) && this.getLanguage().equals(other.getLanguage())));
            // compare translated values
            ret = ret && ((this.getTranslatedValue() == other.getTranslatedValue()) ||
                ((this.getTranslatedValue() != null) && this.getTranslatedValue().equals(other.getTranslatedValue())));
            return ret;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int result = 17;
        result = 31 * result + (null == getId() ? 0 : getId().hashCode());
        result = 31 * result + (getProductAttribute() == null ? 0 : getProductAttribute().hashCode());
        result = 31 * result + (getLanguage() == null ? 0 : getLanguage().hashCode());
        result = 31 * result + (getTranslatedValue() == null ? 0 : getTranslatedValue().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ProductAttributeTranslation(id=" + (null == getId() ? "null" : getId().toString()) +
                ", productAttribute=" + (null == getProductAttribute() ? "null" : getProductAttribute().toString()) +
                ", language=" + (null == getLanguage() ? "null" : getLanguage().toString()) +
                ", translatedValue=" + (null == getTranslatedValue() ? "null" : "\"" + getTranslatedValue().replace("\"", "\\\"") + "\"") +
                ")";
    }
}
