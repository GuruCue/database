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

import com.gurucue.recommendations.entity.ConsumerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The central class for data management.
 */
public final class DataManager {
    private static final Logger log = LogManager.getLogger(DataManager.class);

    /**
     * The currently used <code>DataProvider</code> for data persistence.
     * TODO: Guard it with read and write locks when the functionality for
     * TODO: run-time DataProvider switching is implemented.
     */
    private static DataProvider provider = null;

    private static final ThreadLocal<DataLink> dataLinkInstance = new ThreadLocal<DataLink>() {
        @Override
        protected DataLink initialValue() {
            if (provider == null) throw new IllegalStateException("No provider set. First configure and set a provider.");
            return provider.newDataLink();
        }
    };

    /**
     * Returns a <code>DataLink</code> instance for the thread.
     * If there is no link instance bound to the thread yet, it is first
     * created and bound to the thread.
     * The link instance must be closed by calling {@link #getCurrentLink()}.
     *
     * @return the thread's <code>DataLink</code> instance
     */
    public static DataLink getCurrentLink() {
        return dataLinkInstance.get();
    }

    /**
     * Closes and unbinds from the thread the current <code>DataLink</code>
     * instance.
     */
    public static void removeCurrentLink() {
        final DataLink link = getCurrentLink();
        dataLinkInstance.remove();
        link.close();
    }

    /**
     * Returns a new <code>DataLink</code> instance, not bound to any thread.
     * When the link instance is not needed anymore, it must be closed by its
     * own {@link com.gurucue.recommendations.data.DataLink#close()} method.
     *
     * @return a new <code>DataLink</code> instance, not used by anyone else
     */
    public static DataLink getNewLink() {
        if (provider == null) throw new IllegalStateException("No provider set. First configure and set a provider.");
        return provider.newDataLink();
    }

    /**
     * Sets the <code>DataProvider</code> instance to be used for data persistence.
     * TODO: Currently only initial set is supported, run-time switching is not supported.
     * @param provider the new data provider to be used for data persistence
     */
    public static void setProvider(final DataProvider provider) {
        if (DataManager.provider == null) DataManager.provider = provider;
        else throw new UnsupportedOperationException("Run-time switching of providers is not yet implemented");
    }

    /**
     * Returns the currently active provider.
     * @return the currently active data provider used for data persistence
     */
    public static DataProvider getProvider() {
        return provider;
    }

    /**
     * Clears any cached entities, if provider implements internal caching.
     */
    public static void clearCaches() {
        if (provider == null) throw new IllegalStateException("No provider set. First configure and set a provider.");
        provider.clearCaches();
    }

    /**
     * Closes the current provider. After that no provider is set in DataManager,
     * so before new links are requested a provider must be set. Note that closing
     * a provider will forcibly close any outstanding links.
     */
    public static void closeProvider() {
        if (provider == null) return; // already closed
        provider.close();
        provider = null;
    }

    /**
     * Returns an instance with all the attribute codes from the current provider.
     * @return the container with attribute codes
     */
    public static AttributeCodes getAttributeCodes() {
        try {
            return provider.getAttributeCodes();
        }
        catch (NullPointerException e) {
            log.error("Retrieving attribute codes from the current provider failed: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Returns an instance with all the product type codes from the current provider.
     * @return the container with product type codes
     */
    public static ProductTypeCodes getProductTypeCodes() {
        try {
            return provider.getProductTypeCodes();
        }
        catch (NullPointerException e) {
            log.error("Retrieving product type codes from the current provider failed: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Returns an instance with all the consumer event type codes from the current provider.
     * @return the container with consumer event type codes
     */
    public static ConsumerEventTypeCodes getConsumerEventTypeCodes() {
        try {
            return provider.getConsumerEventTypeCodes();
        }
        catch (NullPointerException e) {
            log.error("Retrieving consumer event type codes from the current provider failed: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Returns and instance with all the data type codes.
     * @return the container with data type codes
     */
    public static DataTypeCodes getDataTypeCodes() {
        try {
            return provider.getDataTypeCodes();
        }
        catch (NullPointerException e) {
            log.error("Retrieving data type codes from the current provider failed: " + e.toString(), e);
            return null;
        }
    }

    public static LanguageCodes getLanguageCodes() {
        try {
            return provider.getLanguageCodes();
        }
        catch (NullPointerException e) {
            log.error("Retrieving language codes from the current provider failed: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Queues results of the consumer event service for later (asynchronous)
     * storage. Therefore the data storage is done in a separate transaction,
     * in a separate thread.
     * Generated consumer events should supply a null log entry.
     * When a log entry is missing, then the request timestamp will be the
     * event timestamp.
     *
     * @param consumerEventLog the consumer event log instance generated by the consumer event service
     * @param consumerEvent the consumer event instance generated by the consumer event service
     * @throws InterruptedException if interrupted during the queueing procedure
     */
    /*public static void queueConsumerEvent(final LogSvcConsumerEvent consumerEventLog, final ConsumerEvent consumerEvent) throws InterruptedException {
        provider.queueConsumerEvent(consumerEventLog, consumerEvent);
    }*/

    /**
     * Queues results of the consumer event service for later (asynchronous)
     * storage. Therefore the data storage is done in a separate transaction,
     * in a separate thread.
     *
     * @param consumerEvent the consumer event instance generated by the consumer event service
     * @throws InterruptedException if interrupted during the queueing procedure
     */
    public static void queueConsumerEvent(final ConsumerEvent consumerEvent) throws InterruptedException {
        provider.queueConsumerEvent(consumerEvent);
    }

    /**
     * Resizes the buffer for consumer event queueing.
     *
     * @param newSize the new size of the consumer event queue
     */
    public static void resizeConsumerEventQueueSize(final int newSize) {
        provider.resizeConsumerEventQueueSize(newSize);
    }

    /**
     * Resizes the thread pool for processing the consumer event queue.
     *
     * @param newSize the new number of threads for processing the consumer event queue
     */
    public static void resizeConsumerEventQueueThreadPool(final int newSize) {
        provider.resizeConsumerEventQueueThreadPool(newSize);
    }
}
