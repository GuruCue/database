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

public class ConsumerEventType implements Serializable {
    private static final long serialVersionUID = 8430663816754697722L;

    /** Identifier of the rating event type. */
    public static final String RATING = "rating";
    /** Identifier of the consumption event type. */
    public static final String CONSUMPTION = "consumption";
    /** Identifier of the product-pair vote. */
    public static final String PRODUCT_PAIR_VOTE = "product-pair-vote";
    /** Identifier of the purchase event type. */
    public static final String PURCHASE = "purchase";
    /** Identifier of the zap event type. */
    public static final String ZAP = "zap";
    /** Identifier of the user interaction event type. */
    public static final String INTERACTION = "interaction";
    /** Identifier of the live-tv consumption event type. */
    public static final String LIVE_TV_CONSUMPTION = "live-tv-consumption";
    /** Identifier of the feedback event type. */
    public static final String FEEDBACK = "feedback";
    /** Identifier of the offline-computed consumption event type. */
    public static final String OFFLINE_CONSUMPTION = "offline-consumption";
    /** Identifier of the offline-computed live-tv consumption event type. */
    public static final String OFFLINE_LIVE_TV_CONSUMPTION = "offline-live-tv-consumption";
    /** Identifier of the viewership event type. */
    public static final String VIEWERSHIP = "viewership";

    Long id;
    String identifier;

    public ConsumerEventType() {}
    
    public ConsumerEventType(final Long id, final String identifier) {
        this.id = id;
        this.identifier = identifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof ConsumerEventType) {
            ConsumerEventType other = (ConsumerEventType) obj;
            // compare ids
            boolean ret = (this.getId() == other.getId()) ||
                ((this.getId() != null) && this.getId().equals(other.getId()));
            // compare identifiers
            ret = ret && ((this.getIdentifier() == other.getIdentifier()) ||
                ((this.getIdentifier() != null) && this.getIdentifier().equals(other.getIdentifier())));
            return ret;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int result = 17;
        result = 31 * result + (getId() == null ? 0 : getId().hashCode());
        result = 31 * result + (getIdentifier() == null ? 0 : getIdentifier().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ConsumerEventType(id=" + (null == getId() ? "null" : getId().toString()) +
            ", identifier=" + (null == getIdentifier() ? "null" : "\"" + getIdentifier().replace("\"", "\\\"") + "\"") +
            ")";
    }
}
