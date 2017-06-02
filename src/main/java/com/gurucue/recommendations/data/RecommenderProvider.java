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
package com.gurucue.recommendations.data;

import com.gurucue.recommendations.blender.StatefulFilter;
import com.gurucue.recommendations.blender.VideoData;
import com.gurucue.recommendations.recommender.RecommendationSettings;

/**
 * Provides methods for accessing recommenders.
 * TODO: add support for different DataValue implementations.
 */
public interface RecommenderProvider {
    /**
     * Constructs and returns a recommender as a stateful filter.
     *
     * @param recommenderName the name of the recommender
     * @param consumerId the ID of the consumer for which recommendations will be requested
     * @param settings settings for the recommender; do not set tags, as they will be set by the filter from any taggers in the chain before it
     * @return the recommender as a stateful filter
     * @see RecommendationSettings
     */
    StatefulFilter<VideoData> recommendationsFilter(String recommenderName, long consumerId, RecommendationSettings settings);

    /**
     * Constructs and returns a recommender for similar products as a stateful filter.
     *
     * @param recommenderName the name of the recommender
     * @param productIdsForSimilar the "source" products to which the recommender selects similar products
     * @param settings settings for the recommender, as they will be set by the filter from any taggers in the chain before it
     * @return the recommender for similar products as a stateful filter
     * @see RecommendationSettings
     */
    StatefulFilter<VideoData> similarFilter(String recommenderName, long[] productIdsForSimilar, RecommendationSettings settings);
}
