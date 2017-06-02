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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public final class Product implements Serializable {
    private static final long serialVersionUID = 1285450476968916519L;

    /* IMPORTANT!
     * ==========
     * When adding or removing fields, also change equals() and hashCode() accordingly!
     * (Except for sets/relations.)
     */
	Long id;
    Partner partner;
	ProductType productType;
    String partnerProductCode;
    Product product;
    Timestamp deleted;

    List<ProductAttribute> productAttributes;

	public Product() {}

	public Product(final Long id, final Partner partner, final ProductType productType, final String partnerProductCode, final Product product, final Timestamp deleted) {
		this.id = id;
        this.partner = partner;
		this.productType = productType;
        this.partnerProductCode = partnerProductCode;
        this.product = product;
        this.deleted = deleted;
        this.productAttributes = null;
	}

    /**
     * Copy constructor, optionally deep copies the list of product attributes.
     *
     * @param existingProduct product to copy
     * @param copyProductAttributes whether to deep copy the list of product attributes
     */
    public Product(final Product existingProduct, final boolean copyProductAttributes) {
        this.id = existingProduct.getId();
        this.partner = existingProduct.getPartner();
        this.productType = existingProduct.getProductType();
        this.partnerProductCode = existingProduct.getPartnerProductCode();
        this.product = existingProduct.getProduct();
        this.deleted = existingProduct.getDeleted();
        if (!copyProductAttributes || (existingProduct.getProductAttributes() == null)) {
            this.productAttributes = null;
        }
        else {
            this.productAttributes = new ArrayList<ProductAttribute>();
            for (final ProductAttribute existingProductAttribute : existingProduct.getProductAttributes()) {
                final ProductAttribute copy = new ProductAttribute(existingProductAttribute);
                copy.setProduct(this);
                this.productAttributes.add(copy);
            }
        }
    }

    /**
     * Copy constructor, creates a deep copy of the given instance.
     *
     * @param existingProduct product to copy
     */
    public Product(final Product existingProduct) {
        this(existingProduct, true);
    }

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(final Partner partner) {
        this.partner = partner;
    }

	public ProductType getProductType() {
        return productType;
    }

    public void setProductType(final ProductType productType) {
        this.productType = productType;
    }

    public String getPartnerProductCode() {
        return partnerProductCode;
    }

    public void setPartnerProductCode(final String partnerProductCode) {
        this.partnerProductCode = partnerProductCode;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(final Product product) {
        this.product = product;
    }

    public Timestamp getDeleted() {
        return deleted;
    }

    public void setDeleted(final Timestamp deleted) {
        this.deleted = deleted;
    }

    public List<ProductAttribute> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(final List<ProductAttribute> productAttributes) {
        this.productAttributes = productAttributes;
    }

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj instanceof Product) {
			Product other = (Product) obj;
			// compare ids
			boolean ret = (this.getId() == other.getId()) ||
					((this.getId() != null) && this.getId().equals(other.getId()));
            // compare partner
            ret = ret && ((this.getPartner() == other.getPartner()) ||
                    ((this.getPartner() != null) && this.getPartner().equals(other.getPartner())));
			// compare product type
            ret = ret && ((this.getProductType() == other.getProductType()) ||
                    ((this.getProductType() != null) && this.getProductType().equals(other.getProductType())));
            // compare partner product code
            ret = ret && ((this.getPartnerProductCode() == other.getPartnerProductCode()) ||
                    ((this.getPartnerProductCode() != null) && this.getPartnerProductCode().equals(other.getPartnerProductCode())));
            // compare product
            ret = ret && ((this.getProduct() == other.getProduct()) ||
                    ((this.getProduct() != null) && this.getProduct().equals(other.getProduct())));
            // compare deleted
            ret = ret && ((this.getDeleted() == other.getDeleted()) ||
                    ((this.getDeleted() != null) && this.getDeleted().equals(other.getDeleted())));
			return ret;
		}
		return false;
	}

	@Override
	public int hashCode() {
		// recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
		int result = 17;
		result = 31 * result + (null == getId() ? 0 : getId().hashCode());
        result = 31 * result + (null == getPartner() ? 0 : getPartner().hashCode());
		result = 31 * result + (null == getProductType() ? 0 : getProductType().hashCode());
        result = 31 * result + (null == getPartnerProductCode() ? 0 : getPartnerProductCode().hashCode());
        result = 31 * result + (null == getProduct() ? 0 : getProduct().hashCode());
        result = 31 * result + (null == getDeleted() ? 0 : getDeleted().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return  "Product(id=" + (null == getId() ? "null" : getId().toString()) +
                ", partner=" + (null == getPartner() ? "null" : getPartner().toString()) +
			    ", productType=" + (null == getProductType() ? "null" : getProductType().toString()) +
                ", partnerProductCode=" + (null == getPartnerProductCode() ? "null" : "\"" + getPartnerProductCode().replace("\"", "\\\"") + "\"") +
                ", product=" + (null == getProduct() ? "null" : getProduct().toString()) +
                ", deleted=" + (null == getDeleted() ? "null" : "\"" + getDeleted().toString() + "\"") +
			    ")";
	}
}
