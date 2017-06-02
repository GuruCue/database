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

public class ProductType implements Serializable {
    private static final long serialVersionUID = -88208194592764033L;

    /** Identifier of the video product type. This is for the abstract base product that groups product properties common to all materialised products. */
    public static final String VIDEO = "video";
    /** Identifier of the TV programme product type. */
    public static final String TV_PROGRAMME = "tv-programme";
    /** Identifier of the TV channel product type. */
    public static final String TV_CHANNEL = "tv-channel";
    /** Identifier of the package product type. Used for programme/channel/movie/etc. subscriber packages by service providers. */
    public static final String PACKAGE = "package";
    /** Identifier of the transaction video-on-demand product type. Used for subscriber packages by service providers containing TVOD. */
    public static final String TVOD = "tvod";
    /** Identifier of the subscription video-on-demand product type. Used for subscriber packages by service providers containing SVOD. */
    public static final String SVOD = "svod";
    /** Identifier of the product type for interactive content. Used for subscriber packages by service providers containing interactive content. */
    public static final String INTERACTIVE = "interactive";
    /** Identifier of the product type for series. Used to hold a series's title. */
    public static final String SERIES = "series";
    
    /* IMPORTANT!
     * ==========
     * When adding or removing fields, also change equals() and hashCode() accordingly!
     * (Except for sets/relations.)
     */
    Long id;
    String identifier;

    public ProductType() {}

    public ProductType(final Long id, final String identifier) {
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
        if (obj instanceof ProductType) {
            ProductType other = (ProductType) obj;
            // compare id
            boolean ret = (this.getId() == other.getId()) ||
                    ((this.getId() != null) && this.getId().equals(other.getId()));
            // compare identifier
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
        result = 31 * result + (null == getId() ? 0 : getId().hashCode());
        result = 31 * result + (null == getIdentifier() ? 0 : getIdentifier().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ProductType(id=" + (null == getId() ? "null" : getId().toString()) +
                ", identifier=" + (null == getIdentifier() ? "null" : "\"" + getIdentifier().replace("\"", "\\\"") + "\"") +
                ")";
    }
}
