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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Deprecated
public class PartnerRecommender implements Serializable {
    private static final long serialVersionUID = 3147977520641818211L;

    Long id;
    Partner partner;
    Recommender recommender;
    String name;

    public PartnerRecommender() {}

    public PartnerRecommender(final Long id, final Partner partner, final Recommender recommender, final String name) {
        this.id = id;
        this.partner = partner;
        this.recommender = recommender;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public Recommender getRecommender() {
        return recommender;
    }

    public void setRecommender(Recommender recommender) {
        this.recommender = recommender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof PartnerRecommender) {
            PartnerRecommender other = (PartnerRecommender) obj;
            // compare ids
            boolean ret = (this.getId() == other.getId()) ||
                    ((this.getId() != null) && this.getId().equals(other.getId()));
            // compare partners
            ret = ret && ((this.getPartner() == other.getPartner()) ||
                    ((this.getPartner() != null) && this.getPartner().equals(other.getPartner())));
            // compare recommenders
            ret = ret && ((this.getRecommender() == other.getRecommender()) ||
                    ((this.getRecommender() != null) && this.getRecommender().equals(other.getRecommender())));
            // compare names
            ret = ret && ((this.getName() == other.getName()) ||
                    ((this.getName() != null) && this.getName().equals(other.getName())));
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
        result = 31 * result + (null == getRecommender() ? 0 : getRecommender().hashCode());
        result = 31 * result + (null == getName() ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PartnerRecommender(id=" + (null == getId() ? "null" : getId().toString()) +
                ", partner=" + (null == getPartner() ? "null" : getPartner().toString()) +
                ", recommender=" + (null == getRecommender() ? "null" : getRecommender().toString()) +
                ", name=" + (null == getName() ? "null" : "\"" + getName().replace("\"", "\\\"") + "\"") +
                ")";
    }
}
