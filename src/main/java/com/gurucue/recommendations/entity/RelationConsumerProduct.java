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

public class RelationConsumerProduct implements Serializable {
    private static final long serialVersionUID = 6223508057619599812L;

    /* IMPORTANT!
	 * ==========
	 * When adding or removing fields, also change equals() and hashCode() accordingly!
	 * (TODO: Except for sets/relations?)
	 */
	Long id;
    Consumer consumer;
    com.gurucue.recommendations.entity.product.Product product;
    RelationType relationType;
    Timestamp relationStart;
    Timestamp relationEnd;

	public RelationConsumerProduct() {}

	public RelationConsumerProduct(final Long id, final Consumer consumer, final com.gurucue.recommendations.entity.product.Product product, final RelationType relationType, final Timestamp relationStart, final Timestamp relationEnd) {
		this.id = id;
        this.consumer = consumer;
        this.product = product;
        this.relationType = relationType;
        this.relationStart = relationStart;
        this.relationEnd = relationEnd;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public com.gurucue.recommendations.entity.product.Product getProduct() {
        return product;
    }

    public void setProduct(com.gurucue.recommendations.entity.product.Product product) {
        this.product = product;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public Timestamp getRelationStart() {
        return relationStart;
    }

    public void setRelationStart(Timestamp relationStart) {
        this.relationStart = relationStart;
    }

    public Timestamp getRelationEnd() {
        return relationEnd;
    }

    public void setRelationEnd(Timestamp relationEnd) {
        this.relationEnd = relationEnd;
    }

    @Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj instanceof RelationConsumerProduct) {
			RelationConsumerProduct other = (RelationConsumerProduct) obj;
			// compare ids
			boolean ret = (this.getId() == other.getId()) ||
					((this.getId() != null) && this.getId().equals(other.getId()));
			// compare consumers
			ret = ret && ((this.getConsumer() == other.getConsumer()) ||
					((this.getConsumer() != null) && this.getConsumer().equals(other.getConsumer())));
            // compare products
            ret = ret && ((this.getProduct() == other.getProduct()) ||
                    ((this.getProduct() != null) && this.getProduct().equals(other.getProduct())));
            // compare relation types
            ret = ret && ((this.getRelationType() == other.getRelationType()) ||
                    ((this.getRelationType() != null) && this.getRelationType().equals(other.getRelationType())));
            // compare relationStart
            ret = ret && ((this.getRelationStart() == other.getRelationStart()) ||
                    ((this.getRelationStart() != null) && this.getRelationStart().equals(other.getRelationStart())));
            // compare relationEnd
            ret = ret && ((this.getRelationEnd() == other.getRelationEnd()) ||
                    ((this.getRelationEnd() != null) && this.getRelationEnd().equals(other.getRelationEnd())));
            return ret;
		}
		return false;
	}

	@Override
	public int hashCode() {
		// recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
		int result = 17;
		result = 31 * result + (null == getId() ? 0 : getId().hashCode());
        result = 31 * result + (null == getConsumer() ? 0 : getConsumer().hashCode());
        result = 31 * result + (null == getProduct() ? 0 : getProduct().hashCode());
        result = 31 * result + (null == getRelationType() ? 0 : getRelationType().hashCode());
        result = 31 * result + (null == getRelationStart() ? 0 : getRelationStart().hashCode());
        result = 31 * result + (null == getRelationEnd() ? 0 : getRelationEnd().hashCode());
        return result;
	}

	@Override
	public String toString() {
		return "RelationConsumerProduct(id=" + (null == getId() ? "null" : getId().toString()) + ")";
	}
}
