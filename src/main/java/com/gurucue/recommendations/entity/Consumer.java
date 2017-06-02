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
import java.sql.*;

public class Consumer implements Serializable {
    private static final long serialVersionUID = 6225508057619599812L;

    /* IMPORTANT!
	 * ==========
	 * When adding or removing fields, also change equals() and hashCode() accordingly!
	 * (TODO: Except for sets/relations?)
	 */
	Long id;
	String username;
    Partner partner;
    Timestamp activated;

	public Consumer() {}

	public Consumer(final Long id, final String username, final Partner partner, final Timestamp activated) {
		this.id = id;
		this.username = username;
        this.partner = partner;
        this.activated = activated;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getUsername() {
	    return username;
	}

	public void setUsername(final String username) {
	    this.username = username;
	}

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(final Partner partner) {
        this.partner = partner;
    }

    public Timestamp getActivated() {
        return activated;
    }

    public void setActivated(final Timestamp activated) {
        this.activated = activated;
    }

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj instanceof Consumer) {
			Consumer other = (Consumer) obj;
			// compare ids
			boolean ret = (this.getId() == other.getId()) ||
					((this.getId() != null) && this.getId().equals(other.getId()));
			// compare usernames
			ret = ret && ((this.getUsername() == other.getUsername()) ||
					((this.getUsername() != null) && this.getUsername().equals(other.getUsername())));
            // compare partners
            ret = ret && ((this.getPartner() == other.getPartner()) ||
                    ((this.getPartner() != null) && this.getPartner().equals(other.getPartner())));
            // compare activated
            ret = ret && ((this.getActivated() == other.getActivated()) ||
                    ((this.getActivated() != null) && this.getActivated().equals(other.getActivated())));
            return ret;
		}
		return false;
	}

	@Override
	public int hashCode() {
		// recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
		int result = 17;
		result = 31 * result + (null == getId() ? 0 : getId().hashCode());
		result = 31 * result + (null == getUsername() ? 0 : getUsername().hashCode());
        result = 31 * result + (null == getPartner() ? 0 : getPartner().hashCode());
        result = 31 * result + (null == getActivated() ? 0 : getActivated().hashCode());
        return result;
	}

	@Override
	public String toString() {
		return "Consumer(id=" + (null == getId() ? "null" : getId().toString()) +
			", username=" + (null == getUsername() ? "null" : "\"" + getUsername().replace("\"", "\\\"") + "\"") +
            ", partner=" + (null == getPartner() ? "null" : getPartner().toString()) +
            ", activated=" + (null == getActivated() ? "null" : getActivated().toString()) +
            ")";
	}
}
