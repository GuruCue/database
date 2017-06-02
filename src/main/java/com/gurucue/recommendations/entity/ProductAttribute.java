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
import java.util.ArrayList;
import java.util.List;

@Deprecated
public final class ProductAttribute implements Serializable {
    private static final long serialVersionUID = -3017901386782795589L;

    /* IMPORTANT!
     * ==========
     * When adding or removing fields, also change equals() and hashCode() accordingly!
     */
    Long id;
    Product product;
    Attribute attribute;
    String originalValue;
    Language originalLanguage;

    List<ProductAttributeTranslation> translations;

    public ProductAttribute() {}

    public ProductAttribute(final Long id, final Product product, final Attribute attribute, final String originalValue, final Language originalLanguage) {
        this.id = id;
        this.product = product;
        this.attribute = attribute;
        this.originalLanguage = originalLanguage;
        this.originalValue = originalValue;
        this.translations = null;
    }

    /**
     * Copy constructor, optionally deep copies the list of translations.
     *
     * @param existingProductAttribute instance to copy
     * @param copyTranslations whether to deep copy translations
     */
    public ProductAttribute(final ProductAttribute existingProductAttribute, final boolean copyTranslations) {
        this.id = existingProductAttribute.getId();
        this.product = existingProductAttribute.getProduct();
        this.attribute = existingProductAttribute.getAttribute();
        this.originalLanguage = existingProductAttribute.getOriginalLanguage();
        this.originalValue = existingProductAttribute.getOriginalValue();
        if (!copyTranslations || (existingProductAttribute.getTranslations() == null)) {
            this.translations = null;
        }
        else {
            this.translations = new ArrayList<ProductAttributeTranslation>();
            for (final ProductAttributeTranslation existingTranslation : existingProductAttribute.getTranslations()) {
                final ProductAttributeTranslation copy = new ProductAttributeTranslation(existingTranslation);
                copy.setProductAttribute(this);
                this.translations.add(copy);
            }
        }
    }

    /**
     * Copy constructor, creates a deep copy of the given instance.
     *
     * @param existingProductAttribute instance to copy
     */
    public ProductAttribute(final ProductAttribute existingProductAttribute) {
        this(existingProductAttribute, true);
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(final Product product) {
        this.product = product;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(final Attribute attribute) {
        this.attribute = attribute;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(final String value) {
        this.originalValue = value;
    }

    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(final Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public List<ProductAttributeTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(final List<ProductAttributeTranslation> translations) {
        this.translations = translations;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof ProductAttribute) {
            ProductAttribute other = (ProductAttribute) obj;
            // compare IDs
            boolean ret = (this.getId() == other.getId()) ||
                    ((this.getId() != null) && this.getId().equals(other.getId()));
            // compare products
            ret = ret && ((this.getProduct() == other.getProduct()) ||
                    ((this.getProduct() != null) && this.getProduct().equals(other.getProduct())));
            // compare attributes
            ret = ret && ((this.getAttribute() == other.getAttribute()) ||
                    ((this.getAttribute() != null) && this.getAttribute().equals(other.getAttribute())));
            // compare languages
            ret = ret && ((this.getOriginalLanguage() == other.getOriginalLanguage()) ||
                    ((this.getOriginalLanguage() != null) && this.getOriginalLanguage().equals(other.getOriginalLanguage())));
            // compare values
            ret = ret && ((this.getOriginalValue() == other.getOriginalValue()) ||
                    ((this.getOriginalValue() != null) && this.getOriginalValue().equals(other.getOriginalValue())));
            return ret;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int result = 17;
        result = 31 * result + (null == getId() ? 0 : getId().hashCode());
        result = 31 * result + (null == getProduct() ? 0 : getProduct().hashCode());
        result = 31 * result + (null == getAttribute() ? 0 : getAttribute().hashCode());
        result = 31 * result + (null == getOriginalLanguage() ? 0 : getOriginalLanguage().hashCode());
        result = 31 * result + (null == getOriginalValue() ? 0 : getOriginalValue().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ProductAttribute(id=" + (null == getId() ? "null" : getId().toString()) +
            ", product=" + (null == getProduct() ? "null" : getProduct().toString()) +
            ", attribute=" + (null == getAttribute() ? "null" : getAttribute().toString()) +
            ", originalLanguage=" + (null == getOriginalLanguage() ? "null" : getOriginalLanguage().toString()) +
            ", originalValue=" + (null == getOriginalValue() ? "null" : "\"" + getOriginalValue().replace("\"", "\\\"") + "\"") +
            ")";
    }
}
