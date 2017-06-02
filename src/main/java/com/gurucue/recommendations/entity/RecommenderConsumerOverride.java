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

public class RecommenderConsumerOverride {
    Long id;
    Consumer consumer;
    PartnerRecommender originalPartnerRecommender;
    PartnerRecommender overridePartnerRecommender;

    public RecommenderConsumerOverride() {}

    public RecommenderConsumerOverride(final Long id, final Consumer consumer, final PartnerRecommender originalPartnerRecommender, final PartnerRecommender overridePartnerRecommender) {
        this.id = id;
        this.consumer = consumer;
        this.originalPartnerRecommender = originalPartnerRecommender;
        this.overridePartnerRecommender = overridePartnerRecommender;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(final Consumer consumer) {
        this.consumer = consumer;
    }

    public PartnerRecommender getOriginalPartnerRecommender() {
        return originalPartnerRecommender;
    }

    public void setOriginalPartnerRecommender(final PartnerRecommender originalPartnerRecommender) {
        this.originalPartnerRecommender = originalPartnerRecommender;
    }

    public PartnerRecommender getOverridePartnerRecommender() {
        return overridePartnerRecommender;
    }

    public void setOverridePartnerRecommender(final PartnerRecommender overridePartnerRecommender) {
        this.overridePartnerRecommender = overridePartnerRecommender;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof RecommenderConsumerOverride) {
            RecommenderConsumerOverride other = (RecommenderConsumerOverride) obj;
            // compare ids
            boolean ret = (this.getId() == other.getId()) ||
                    ((this.getId() != null) && this.getId().equals(other.getId()));
            // compare consumers
            ret = ret && ((this.getConsumer() == other.getConsumer()) ||
                    ((this.getConsumer() != null) && this.getConsumer().equals(other.getConsumer())));
            // compare partner recommenders
            ret = ret && ((this.getOriginalPartnerRecommender() == other.getOriginalPartnerRecommender()) ||
                    ((this.getOriginalPartnerRecommender() != null) && this.getOriginalPartnerRecommender().equals(other.getOriginalPartnerRecommender())));
            // compare recommenders
            ret = ret && ((this.getOverridePartnerRecommender() == other.getOverridePartnerRecommender()) ||
                    ((this.getOverridePartnerRecommender() != null) && this.getOverridePartnerRecommender().equals(other.getOverridePartnerRecommender())));
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
        result = 31 * result + (null == getOriginalPartnerRecommender() ? 0 : getOriginalPartnerRecommender().hashCode());
        result = 31 * result + (null == getOverridePartnerRecommender() ? 0 : getOverridePartnerRecommender().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PartnerRecommender(id=" + (null == getId() ? "null" : getId().toString()) +
                ", consumer=" + (null == getConsumer() ? "null" : getConsumer().toString()) +
                ", originalPartnerRecommender=" + (null == getOriginalPartnerRecommender() ? "null" : getOriginalPartnerRecommender().toString()) +
                ", overridePartnerRecommender=" + (null == getOverridePartnerRecommender() ? "null" : getOverridePartnerRecommender().toString()) +
                ")";
    }
}
