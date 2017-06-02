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
package com.gurucue.recommendations.blender;

import com.gurucue.recommendations.Transaction;
import com.gurucue.recommendations.data.DataProvider;
import com.gurucue.recommendations.data.RecommenderProvider;
import com.gurucue.recommendations.dto.ConsumerEntity;
import com.gurucue.recommendations.entity.Partner;

/**
 * Contains stuff needed for blender operation: providers, entities, ...
 */
public final class BlendEnvironment {
    public final DataProvider dataProvider;
    public final RecommenderProvider recommenderProvider;
    public final Transaction transaction;
    public final Partner partner;
    public final ConsumerEntity consumer;
    public final long requestTimestampMillis;
    public final boolean debug;

    /**
     * Constructs the environment for a blender from the given arguments.
     *
     * @param dataProvider the database provider
     * @param recommenderProvider the recommender provider
     * @param transaction the transaction to use for database operations
     * @param partner the partner for which to make a DataSet
     * @param consumer the consumer for which to make a DataSet
     * @param requestTimestampMillis the timestamp at which the blend is occurring
     * @param debug whether the blender should generate logs for debugging purposes
     */
    public BlendEnvironment(
            final DataProvider dataProvider,
            final RecommenderProvider recommenderProvider,
            final Transaction transaction,
            final Partner partner,
            final ConsumerEntity consumer,
            final long requestTimestampMillis,
            final boolean debug
    ) {
        this.dataProvider = dataProvider;
        this.recommenderProvider = recommenderProvider;
        this.transaction = transaction;
        this.partner = partner;
        this.consumer = consumer;
        this.requestTimestampMillis = requestTimestampMillis;
        this.debug = debug;
    }
}
