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
package com.gurucue.recommendations.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Value types enumeration. Used in the product attributes to
 * specify what values can a given attribute accept.
 * <p>
 * Constructor syntax: <tt>TYPENAME(type_id, type_name)</tt><br>
 * Where:
 * <ul>
 * <li><tt>type_id</tt> is the id of the type in the database (attribute.value_type),</li>
 * <li><tt>type_name</tt> is the name of the type, primarily intended for GUI.</li>
 * </ul>
 */
public enum ValueType {
	// IMPORTANT: ID values must be continuous, without holes
	// just append new members here; no changes below required
	INTEGER  (0, "Integer",   Boolean.FALSE),
	STRING   (1, "String",    Boolean.FALSE),
	BOOLEAN  (2, "Boolean",   Boolean.FALSE),
	DATE     (3, "Date",      Boolean.FALSE),
	@Deprecated
	LISTING  (4, "Listing",   Boolean.TRUE),
	FLOAT    (5, "Float",     Boolean.FALSE),
    /** A timestamp that can be represented as a date, a date with time, or number of seconds since the epoch (1.1.1970 00:00 GMT). */
    TIMESTAMP(6, "Timestamp", Boolean.FALSE),
    /** An interval between two timestamps, which are represented the same way as the <code>TIMESTAMP</code>, delimited by a space. */
    TIMESTAMP_INTERVAL(7, "Timestamp interval", Boolean.FALSE);
		// all the possible values must be listed in attribute_code - if changed
	    // from 4 to some other value then change the ValueType constructor too!

	/* static variables for utility methods */
	public static final Integer minId;
	public static final Integer maxId;
	private static final ValueType[] valueTypes; // indexed by ID, used when converting from ID value to ValueType in fromId()
	private static final List<ValueType> sortedValueTypesByDescription; // utility method used by GUI for combo boxes etc.

	/* instance variables */
	private final Integer id;
	private final String description;
	@Deprecated
	private final Boolean isCode;

	/* enumeration initialization, performed after all the members have already been instantiated */
	static {
		Integer mi = Integer.MAX_VALUE;
		Integer ma = Integer.MIN_VALUE;
		for (ValueType v : values()) { // find min/max values
			if (v.id < mi) mi = v.id;
			if (v.id > ma) ma = v.id;
		}
		minId = mi;
		maxId = ma;
		valueTypes = new ValueType[maxId - minId + 1];
		ArrayList<ValueType> sortedValueTypes = new ArrayList<ValueType>(maxId - minId + 1);
		for (ValueType v: values()) {
			valueTypes[minId + v.id] = v;
			// sorted insertion into the array
			int i = sortedValueTypes.size() - 1;
			while (i >= 0) {
				if (sortedValueTypes.get(i).description.compareTo(v.description) < 0) {
					sortedValueTypes.add(i + 1, v);
					break;
				}
				i--;
			}
			if (i < 0) {
				sortedValueTypes.add(0, v);
			}
		}
		sortedValueTypesByDescription = Collections.unmodifiableList(sortedValueTypes);
	}

	ValueType(final Integer id, final String description, final Boolean isCode) {
		this.id = id;
		this.description = description;
		this.isCode = isCode;
	}

	public Integer getId() { return this.id; }

	public String getDescription() { return this.description; }
	
	@Deprecated
	public Boolean getIsCode() { return this.isCode; }

	public static ValueType fromId(Integer id) {
		if (id == null) {
			throw new IllegalArgumentException("Cannot match a ValueType: illegal ID value: null");
		}
		if ((id < minId) || (id > maxId)) {
			throw new IllegalArgumentException("Cannot match a ValueType: ID value out of range: " + id.toString());
		}
		return valueTypes[minId + id];
	}

	public static List<ValueType> sortedByDescription() {
		return sortedValueTypesByDescription;
	}
}
