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

public class DataType implements Serializable {
    private static final long serialVersionUID = 3880701275678379267L;

    /** Identifier of the rating data type. */
    public static final String RATING = "rating";
    /** Identifier of the catalogue ID data type. */
    public static final String CATALOGUE_ID = "catalogue-id";
    /** Identifier of the MAC address data type. */
    public static final String DEVICE_ID = "device-id";
    /** Identifier of the device type data type. */
    public static final String DEVICE_TYPE = "device-type";
    /** Identifier of the boolean data type for specifying whether a content was purchased. */
    public static final String WAS_PURCHASED = "was-purchased";
    /** Identifier of the first product in the product-pair voting. */
    public static final String PRODUCT1 = "product1";
    /** Identifier of the second product in the product-pair voting. */
    public static final String PRODUCT2 = "product2";
    /** Offset from the start of video, where watching started (consumption), or first zap occurred (livetv-consumption). */
    public static final String WATCH_OFFSET = "watch-offset";
    /** Duration of watching. */
    public static final String WATCH_DURATION = "watch-duration";
    /** For real-time content, the delay with which watching started, relative to the actual airing of the content. */
    public static final String WATCH_DELAY = "watch-delay";
    /** How long was a content watched. */
    public static final String CONTENT_DURATION = "content-duration";
    /** Price of a product. */
    public static final String PRICE = "price";
    /** Origin of an event; what caused the event or where was the event triggered. */
    public static final String ORIGIN = "origin";
    /** For interaction type events, what kind of interaction was performed by the user, e.g.: displaying of content description. */
    public static final String INTERACTION_TYPE = "interaction-type";
    /** For zap type events, the tv-programme airing at the time of the zap. */
    public static final String TV_PROGRAMME_ID = "tv-programme-id";
    /** For catchup consumptions, the tv-channel on which a tv-programme has been watched from catchup. */
    public static final String TV_CHANNEL_ID = "tv-channel-id";
    /** For feedback events, the feedback value. */
    public static final String FEEDBACK = "feedback";
    /** For livetv-consumption events: the number of zaps that contributed to the consumption. */
    public static final String ZAP_COUNT = "zap-count";
    /** For livetv-consumption events: the number of seconds the zap occured before the show started playing. */
    public static final String ZAP_OFFSET = "zap-offset";
    /** For livetv-consumption events: the percentage of a show that the user watched. */
    public static final String WATCH_PERCENTAGE = "watch-percentage";
    /** For consumption and VOD zaps: the action taken, one of: play, pause, stop. */
    public static final String ACTION = "action";
    /** For consumption and VOD zaps having the play action: 1 for normal playback, anything else for winding */
    public static final String SPEED = "speed";
    /** For box status zaps, can be one of: power-on, power-off. */
    public static final String STATUS = "status";
    /** Special error descriptor, added when there's something wrong looking up the specified product, contains the raw product-type value */
    public static final String _ERR_PRODUCT_TYPE = "_err-product-type";
    /** Special error descriptor, added when there's something wrong looking up the specified product, contains the raw product-id value */
    public static final String _ERR_PRODUCT_ID = "_err-product-id";
    /** ID of the product representing the immediately previous state of the device to the consumption event. */
    public static final String ORIGIN_ID = "origin-id";
    /** Product type identifier and partner's product code, separated by space, identifying the product that represents the immediately previous state of the device to the consumption event. */
    public static final String ORIGIN_CODE = "origin-code";
    /** Number of seconds in the immediately previous state of the device to the consumption event. */
    public static final String ORIGIN_WAIT = "origin-wait";
    /** The sum of lengths of all content intervals that were watched. */
    public static final String CONTENT_WATCHED = "content-watched";
    /** Duration of all fast-forwardings during content consumption, in seconds. */
    public static final String FAST_FORWARD_DURATION = "fast-forward-duration";
    /** The sum of lengths of all content intervals that were fast-forwarded. */
    public static final String CONTENT_FAST_FORWARDED = "content-fast-forwarded";
    /** Duration of all rewindings during content consumption, in seconds. */
    public static final String REWIND_DURATION = "rewind-duration";
    /** The sum of lengths of all content intervals that were rewound. */
    public static final String CONTENT_REWOUND = "content-rewound";
    /** The count of play events encountered during a consumption. */
    public static final String PLAY_COUNT = "play-count";
    /** The count of fast-forward events encountered during a consumption. */
    public static final String FAST_FORWARD_COUNT = "fast-forward-count";
    /** The count of rewind events encountered during a consumption. */
    public static final String REWIND_COUNT = "rewind-count";
    /** The count of stop events enveountered during a consumption. */
    public static final String STOP_COUNT = "stop-count";
    /** The count of pause events encountered during a consumption. */
    public static final String PAUSE_COUNT = "pause-count";
    /** The time a consumer spent consuming a content. Besides watching it also includes content windings and pauses. */
    public static final String CONSUMPTION_DURATION = "consumption-duration";
    /** The percentage of consumption-duration relative to content-duration. */
    public static final String CONSUMPTION_PERCENTAGE = "consumption-percentage";
    /** The name of the user profile. */
    public static final String USER_PROFILE_ID = "user-profile-id";
    /** The number of viewers watching a tv-channel. */
    public static final String VIEWER_COUNT = "viewer-count";
    /** The list of consumer IDs. */
    public static final String CONSUMER_ID_LIST = "consumer-id-list";
    /** The list of consumer event IDs of type zap. */
    public static final String ZAP_ID_LIST = "zap-id-list";


    Long id;
    String identifier;

    public DataType() {}
    
    public DataType(final Long id, final String identifier) {
        this.id = id;
        this.identifier = identifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        if (obj instanceof DataType) {
            DataType other = (DataType) obj;
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
        return "DataType(id=" + (null == getId() ? "null" : getId().toString()) +
            ", identifier=" + (null == getIdentifier() ? "null" : "\"" + getIdentifier().replace("\"", "\\\"") + "\"") +
            ")";
    }
}
