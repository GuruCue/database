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
package com.gurucue.recommendations.dto;

public abstract class Entity {
    public final long id; // negative ID means null
    protected final int hash;

    Entity(final long id) {
        this.id = id;
        this.hash = ((527 + (int)(id ^ (id >>> 32))) * 31) + this.getClass().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (!getClass().equals(obj.getClass())) return false;
        return id == ((Entity)obj).id;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (id < 0 ? "null" : Long.toString(id)) + ")";
    }
}
