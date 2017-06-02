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

public class Property implements Serializable {
    private static final long serialVersionUID = -3021257602222368444L;

    /* IMPORTANT!
     * ==========
     * When adding or removing fields, also change equals() and hashCode() accordingly!
     */
	Long id;
	String identifier;

	public Property() {}

	public Property(final Long id, final String identifier) {
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
		if (obj instanceof Property) {
			Property other = (Property) obj;
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
		return "Property(id=" + (null == getId() ? "null" : getId().toString()) +
			", identifier=" + (null == getIdentifier() ? "null" : "\"" + getIdentifier().replace("\"", "\\\"") + "\"") +
			")";
	}
}
