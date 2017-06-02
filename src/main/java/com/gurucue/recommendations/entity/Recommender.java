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

public class Recommender implements Serializable {
    private static final long serialVersionUID = -5616040134703954291L;
    
    Long id;
    String name;

    public Recommender() {}
    
    public Recommender(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof Recommender) {
            Recommender other = (Recommender)obj;
            // compare ids
            boolean ret = (this.getId() == other.getId()) ||
                    ((this.getId() != null) && this.getId().equals(other.getId()));
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
        result = 31 * result + (getId() == null ? 0 : getId().hashCode());
        result = 31 * result + (getName() == null ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Recommender(id=" + (null == getId() ? "null" : getId()) +
            ", name=" + (null == getName() ? "null" : "\"" + getName().replace("\"", "\\\"") + "\"") +
            ")";
    }
}
