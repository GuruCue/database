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
package com.gurucue.recommendations.entitymanager;

import com.gurucue.recommendations.entity.ConsumerEvent;

import java.sql.Timestamp;

/**
 * {@link com.gurucue.recommendations.entity.ConsumerEvent} management methods.
 */
public interface ConsumerEventManager {
    /**
     * Stores the given consumer event and any data within.
     *
     * @param consumerEvent the consumer event with any data within, to store
     */
    void save(ConsumerEvent consumerEvent);

    /**
     * Deletes the consumer event with the given ID, and all associated data.
     *
     * @param id the ID of the consumer event to delete
     */
    void deleteById(Long id);
}
