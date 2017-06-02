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

public class Partner implements Serializable {
    private static final long serialVersionUID = -5639759449662792146L;

    /** The ID of the pseudo-partner, identifying no partner. */
    public static final long PARTNER_ZERO_ID = 0L;

    /* IMPORTANT!
	 * ==========
	 * When adding or removing fields, also change equals() and hashCode() accordingly!
	 * (Except for sets/relations.)
	 */
	Long id;
	String name;
	String username;
	String loginSuffix;
	Language language;

	public Partner() {}

	public Partner(final Long id, final String name, final String username, final String loginSuffix, final Language language) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.loginSuffix = loginSuffix;
		this.language = language;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginSuffix() {
        return loginSuffix;
    }

    public void setLoginSuffix(String loginSuffix) {
        this.loginSuffix = loginSuffix;
    }

	public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Partner) {
			Partner other = (Partner) obj;
			// compare ids
			boolean ret = (this.getId() == other.getId()) ||
					((this.getId() != null) && this.getId().equals(other.getId()));
			// compare names
			ret = ret && ((this.getName() == other.getName()) ||
					((this.getName() != null) && this.getName().equals(other.getName())));
            // compare usernames
            ret = ret && ((this.getUsername() == other.getUsername()) ||
                    ((this.getUsername() != null) && this.getUsername().equals(other.getUsername())));
			// compare login suffix
			ret = ret && ((this.getLoginSuffix() == other.getLoginSuffix()) ||
			        ((this.getLoginSuffix() != null) && this.getLoginSuffix().equals(other.getLoginSuffix())));
            // compare languages
            ret = ret && ((this.getLanguage() == other.getLanguage()) ||
                    ((this.getLanguage() != null) && this.getLanguage().equals(other.getLanguage())));
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
        result = 31 * result + (getUsername() == null ? 0 : getUsername().hashCode());
        result = 31 * result + (getLoginSuffix() == null ? 0 : getLoginSuffix().hashCode());
        result = 31 * result + (getLanguage() == null ? 0 : getLanguage().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Partner(id=" + (null == getId() ? "null" : getId().toString()) +
			", name=" + (null == getName() ? "null" : "\"" + getName().replace("\"", "\\\"") + "\"") +
            ", username=" + (null == getUsername() ? "null" : "\"" + getUsername().replace("\"", "\\\"") + "\"") +
			", loginSuffix=" + (null == getLoginSuffix() ? "null" : "\"" + getLoginSuffix().replace("\"", "\\\"") + "\"") +
			", language=" + (null == getLanguage() ? "null" : getLanguage().toString()) +
			")";
	}
}
