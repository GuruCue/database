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

public class Language implements Serializable {
    private static final long serialVersionUID = 4753103972027352429L;

    /** Language iso639_2t code for the pseudo-language "unknown". It is used in places where a language is required, but it is not known or defined. */
    public static final String UNKNOWN = "unk";

    Long id;
    String iso639_2t;
    String iso639_1;

    public Language() {}

    public Language(final Long id, final String iso639_2t, final String iso639_1) {
        this.id = id;
        this.iso639_2t = iso639_2t;
        this.iso639_1 = iso639_1;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getIso639_2t() {
        return iso639_2t;
    }

    public void setIso639_2t(final String iso639_2t) {
        this.iso639_2t = iso639_2t;
    }

    public String getIso639_1() {
        return iso639_1;
    }

    public void setIso639_1(final String iso639_1) {
        this.iso639_1 = iso639_1;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof Language) {
            Language other = (Language)obj;
            // compare ids
            boolean ret = (this.getId() == other.getId()) ||
                    ((this.getId() != null) && this.getId().equals(other.getId()));
/*            // compare iso639_2t
            ret = ret && ((this.getIso639_2t() == other.getIso639_2t()) ||
                    ((this.getIso639_2t() != null) && this.getIso639_2t().equals(other.getIso639_2t())));
            // compare iso639_1
            ret = ret && ((this.getIso639_1() == other.getIso639_1()) ||
                    ((this.getIso639_1() != null) && this.getIso639_1().equals(other.getIso639_1())));*/
            return ret;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int result = 17;
        result = 31 * result + (getId() == null ? 0 : getId().hashCode());
/*        result = 31 * result + (getIso639_2t() == null ? 0 : getIso639_2t().hashCode());
        result = 31 * result + (getIso639_1() == null ? 0 : getIso639_1().hashCode());*/
        return result;
    }

    @Override
    public String toString() {
        return "Language(id=" + (null == getId() ? "null" : getId()) +
            ", iso639_2t=" + (null == getIso639_2t() ? "null" : "\"" + getIso639_2t().replace("\"", "\\\"") + "\"") +
            ", iso639_1=" + (null == getIso639_1() ? "null" : "\"" + getIso639_1().replace("\"", "\\\"") + "\"") +
            ")";
    }
}
