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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EventDataType {
	RATING(Short.valueOf((short)0), "rating"),
	CATALOGUE_ID(Short.valueOf((short)1), "catalogue-id"),
	EXTERNAL_ID(Short.valueOf((short)2), "external-id"),
	MAC_ADDRESS(Short.valueOf((short)3), "mac-address"),
	WAS_PURCHASED(Short.valueOf((short)4), "was-purchased"),
	WAS_STREAMED(Short.valueOf((short)5), "was-streamed");
	
    /* instance variables */
	private final Short typeCode;
	private final String identifier;
	
    /* type variables */
    private static final Map<Short, EventDataType> codeToTypeMapping;
    private static final Map<String, EventDataType> identifierToTypeMapping;
    static {
    	final Map<Short, EventDataType> codeMapping = new HashMap<Short, EventDataType>();
        final Map<String, EventDataType> identifierMapping = new HashMap<String, EventDataType>();
    	for (EventDataType t : values()) {
    		codeMapping.put(t.getTypeCode(), t);
    		identifierMapping.put(t.getIdentifier(), t);
    	}
    	codeToTypeMapping = Collections.unmodifiableMap(codeMapping);
    	identifierToTypeMapping = Collections.unmodifiableMap(identifierMapping);
    }
    
	EventDataType(Short typeCode, String identifier) {
		this.typeCode = typeCode;
		this.identifier = identifier;
	}
	
    public Short getTypeCode() {
    	return typeCode;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public static EventDataType fromTypeCode(Short typeCode) {
    	if (null == typeCode) throw new NullPointerException("Provided type code is null");
    	EventDataType t = codeToTypeMapping.get(typeCode);
    	if (null == t) throw new IllegalArgumentException("There is no EventDataType having code " + typeCode);
    	return t;
    }
    
    public static EventDataType fromIdentifier(String identifier) {
        if (null == identifier) throw new NullPointerException("Provided identifier is null");
        EventDataType t = identifierToTypeMapping.get(identifier);
        if (null == t) throw new IllegalArgumentException("There is no EventDataType having identifier " + identifier);
        return t;
    }
}
