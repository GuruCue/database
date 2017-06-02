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
package com.gurucue.recommendations.recommender;

import java.rmi.RemoteException;

/**
 * External interface to a recommender. Designed to be used over RMI.
 *
 * @see com.gurucue.recommendations.recommender.BasicRecommenderRemote
 */
public interface BasicRecommender {
    /**
     * Returns a list of recommendations based on the given candidates for the given consumer and settings.
     *
     * @param consumerId the ID of the consumer for which to create recommendations
     * @param candidateProducts the products among which to choose recommendations
     * @param recset settings for the recommender
     * @return the recommendations from among the specified products for the specified consumer using the specified settings
     * @throws RecommenderNotReadyException if no AI model is active yet
     * @throws InterruptedException if the recommender was interrupted while waiting for the request to get serviced or while calculating recommendations
     * @throws RemoteException if there was an exception when performing a RMI operation
     */
    Recommendations recommendations(long consumerId, RecommendProduct[] candidateProducts, RecommendationSettings recset) throws RecommenderNotReadyException, InterruptedException, RemoteException;

    /**
     * Returns a list of similar products to seed products based on the given candidates for the given consumer and settings.
     *
     * @param seedProducts the products for which to find similar products
     * @param candidateProducts the products among which to find similar products
     * @param recset settings for the recommender
     * @return the products that are similar to the specified products for the specified consumer using the specified settings and the specified list of candidate products
     * @throws RecommenderNotReadyException if no AI model is active yet
     * @throws InterruptedException if the recommender was interrupted while waiting for the request to get serviced or while calculating recommendations
     * @throws RemoteException if there was an exception when performing a RMI operation
     */
    Recommendations similar(long[] seedProducts, RecommendProduct[] candidateProducts, RecommendationSettings recset) throws RecommenderNotReadyException, InterruptedException, RemoteException;

    /**
     * Returns the AI model update interval, in seconds.
     *
     * @return the length of interval, in seconds
     * @throws RemoteException if there was an exception when performing a RMI operation
     * @throws InterruptedException if the recommender was interrupted while retrieving data
     */
    long getUpdateInterval() throws InterruptedException, RemoteException;

    /**
     * Sets the AI model update interval, in seconds.
     *
     * @param updateInterval the new interval length, in seconds
     * @throws RemoteException if there was an exception when performing a RMI operation
     * @throws InterruptedException if the recommender was interrupted while setting data
     */
    void setUpdateInterval(long updateInterval) throws InterruptedException, RemoteException;

    /**
     * Returns the AI model persistence interval, in seconds. In other words:
     * the number of seconds between two file saves of the AI interval.
     *
     * @return the length of interval, in seconds
     * @throws RemoteException if there was an exception when performing a RMI operation
     * @throws InterruptedException if the recommender was interrupted while retrieving data
     */
    long getPersistInterval() throws InterruptedException, RemoteException;

    /**
     * Sets the new persistence interval, in seconds.
     *
     * @param persistInterval the new interval length, in seconds
     * @throws RemoteException if there was an exception when performing a RMI operation
     * @throws InterruptedException if the recommender was interrupted while setting data
     */
    void setPersistInterval(long persistInterval) throws InterruptedException, RemoteException;

    /**
     * Forces AI model update. If an update is already running, then this call is ignored.
     *
     * @throws RemoteException if there was an exception when performing a RMI operation
     */
    void updateNow() throws RemoteException;

    /**
     * Forces AI persistence.
     *
     * @throws RemoteException if there was an exception when performing a RMI operation
     */
    void persistNow() throws RemoteException;

    /**
     * Returns the ID of the recommender.
     *
     * @return ID of the recommender
     * @throws RemoteException if there was an exception when performing a RMI operation
     */
    long getRecommenderId() throws RemoteException;

    /**
     * Returns the name of the file to which model is persisted.
     *
     * @return the name of the file to which model is persisted
     * @throws RemoteException if there was an exception when performing a RMI operation
     */
    String getModelFilename() throws RemoteException;
}
