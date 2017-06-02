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

import com.gurucue.recommendations.Transaction;
import com.gurucue.recommendations.blender.DataSet;
import com.gurucue.recommendations.blender.VideoData;
import com.gurucue.recommendations.entity.Attribute;
import com.gurucue.recommendations.entity.Partner;
import com.gurucue.recommendations.entity.ProductType;
import com.gurucue.recommendations.entity.product.*;
import com.gurucue.recommendations.entity.value.Value;
import gnu.trove.set.TLongSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * {@link com.gurucue.recommendations.entity.Product} management methods.
 */
public interface ProductManager {
    /**
     * Returns the product of the specified partner identified by the specified product type and product
     * code.
     * If so specified the product is locked before it is returned, so nobody else can lock it until the given transaction
     * is either committed or rolled back. Note that even if a <code>null</code> is returned, and thus no
     * product identified by the given values exists, the given combination of the values is locked for the
     * duration of the transaction so nobody else can create a product identified by the same values.
     *
     * @param transaction the transaction that will be the owner of the lock
     * @param partner the partner that is the owner of the product, must be partner "zero" for internal (matcher) products
     * @param productType the type of the product
     * @param productCode the code of the product, should be null for internal (matcher) products
     * @param locked whether to lock the product for the duration of the transaction, so no modification outside of this transaction is possible
     * @return the (locked) product having the specified properties
     */
    Product getProductByPartnerAndTypeAndCode(Transaction transaction, Partner partner, ProductType productType, String productCode, boolean locked);

    /**
     * Returns the product having the specified internal ID.
     * The returned product will already contain a list of its attributes.
     *
     * @param transaction the transaction that will be the owner of the lock
     * @param partner the partner to which the product belongs to, as a hint, because product cache is segmented by partners; may be null; TODO: remove this hint by reverse ID mapping in the caching layer
     * @param productId the internal ID of the product
     * @param locked whether to lock the product for the duration of the transaction, so no modification outside of this transaction is possible
     * @return the (locked) product with the specified internal ID
     */
    Product getById(Transaction transaction, Partner partner, long productId, boolean locked);

    /**
     * Marks as deleted the product of the specified partner and product type with the
     * specified product code. The deletion mark bears the timestamp of the moment when
     * the product was physically deleted (database timestamp of deletion occurrence).
     * The product is locked for the duration of the transaction, it is deleted only
     * after the transaction is committed.
     *
     * @param transaction the transaction that will be the owner of the lock
     * @param partner the partner that is the owner of the product, must not be partner zero
     * @param productType the type of the product to delete
     * @param productCode the code of the product to delete
     */
    void deleteByPartnerAndTypeAndCode(Transaction transaction, Partner partner, ProductType productType, String productCode);

    /**
     * Marks as deleted the product with the specified ID. The deletion mark bears the timestamp of the moment when
     * the product was physically deleted (database timestamp of deletion occurrence).
     * The product is locked for the duration of the transaction, it is deleted only
     * after the transaction is committed.
     *
     * @param transaction the transaction to use
     * @param id ID of the Product to delete
     * @return the deleted Product instance
     */
    Product delete(Transaction transaction, long id);

    /**
     * Either creates or modifies the product in the database. If the given
     * product has a null ID, then a new product will be created and the ID
     * generated by the database stored in the returned product. Otherwise, the
     * product identified by the ID will have all its fields modified to the
     * values in the given product, and the new product returned.
     * The product is locked for the duration of the transaction, it is
     * modified/created after the transaction is committed.
     *
     * @param transaction the transaction that will be the owner of the lock
     * @param product the product to create or modify
     * @return the product created or modified
     */
    Product save(Transaction transaction, Product product);

    /**
     * Finds products of the specified product type, belonging to the specified
     * partner, where for each of the specified attribute values the product
     * has exactly the attribute value. If the given attribute value is null,
     * then the product must not contain a value for the specified attribute.
     * Each returned product will already contain the list of its attributes.
     * (Returns all matched products in the database, even those marked as
     * deleted.??TODO: is this true??)
     *
     * @param transaction the transaction to use
     * @param productType the type of products
     * @param partner the partner whose is the product
     * @param attributeValues the attribute values by which to filter
     * @return a list of products with the specified product type and partner, filtered by given attribute values
     */
    List<Product> findProductsHavingAttributes(Transaction transaction, ProductType productType, Partner partner, Map<Attribute, Value> attributeValues);

    /**
     * Remove all tv-programmes that overlap with the given tv-programme on the
     * same channels. The overlap interval is defined with the tv-programme's
     * begin-time and end-time attributes.
     *
     * @param transaction the transaction to use
     * @param tvProgramme the tv-programme whose overlaps to remove
     * @return a list of removed products
     */
    List<Product> removeTvProgrammesOverlappingInterval(Transaction transaction, TvProgrammeProduct tvProgramme);

    /**
     * Returns tv-programme products for the given partner within the given interval.
     * Returned are all the shows active at <code>recommendTimeMillis</code>, those
     * that will start playing in <code>positiveOffsetMillis</code> from the
     * <code>recommendTimeMillis</code>, and those that have started playing in
     * <code>negativeOffsetMillis</code> from recommendTimeMillis but only if they
     * were played on catch-up-able TV channels, and only to the extent of the
     * catch-up time on the TV-channel a show was played on.
     * Products marked as deleted are not included in the result.
     *
     * @param transaction the transaction to use
     * @param partner the partner for which to retrieve tv-programmes
     * @param recommendTimeMillis timestamp in milliseconds from the epoch, for which to find active tv-programmes
     * @param negativeOffsetMillis maximum offset into the past relative to recommendTimeMillis, in milliseconds
     * @param positiveOffsetMillis maximum offset into the future relative to recommendTimeMillis, in milliseconds
     * @return a list of tv-programmes with additional attributes, for the given partner withing the given interval
     */
    List<TvProgrammeInfo> tvProgrammesInIntervalForPartner(Transaction transaction, Partner partner, long recommendTimeMillis, long negativeOffsetMillis, long positiveOffsetMillis);

    /**
     * Returns video products for the given partner, that were/are/will be active at the specified timestamp.
     *
     * @param transaction the transaction to use
     * @param partner the partner for which to retrieve videos
     * @param recommendTimeMillis timestamp in milliseconds from the epoch, for which to find active video products
     * @return a list of videos with additional attributes, for the given partner
     */
    List<VideoInfo> vodForPartner(Transaction transaction, Partner partner, long recommendTimeMillis);

    /**
     * Returns the TV-programme that was/is/will be running at the specified
     * time on the specified TV-channel with the specified partner.
     *
     * @param transaction the transaction to use
     * @param partner the partner whose TV-programmes to consider
     * @param tvChannel the TV-channel for which to find the TV-programme
     * @param timeMillis the time for which to find a running TV-programme
     * @return the TV-programme that was/is/will be running at the specified time on the specified TV-channel
     */
    TvProgrammeProduct tvProgrammeAtTimeForTvChannelAndPartner(Transaction transaction, Partner partner, TvChannelProduct tvChannel, long timeMillis);

    /**
     * Returns the first TV-programme that was/is/will be running after the specified
     * time on the specified TV-channel with the specified partner.
     *
     * @param transaction the transaction to use
     * @param partner the partner whose TV-programmes to consider
     * @param tvChannel the TV-channel for which to find the TV-programme
     * @param timeMillis the time after which to find a TV-programme that starts closest to
     * @return the nearest TV-programme that starts after the specified time on the specified TV-channel
     */
    TvProgrammeProduct firstTvProgrammeAfterTimeForTvChannelAndPartner(Transaction transaction, Partner partner, TvChannelProduct tvChannel, long timeMillis);

    /**
     * Returns the VOD catalogue with the specified catalogue ID from the specified
     * partner.
     *
     * @param transaction the transaction to use
     * @param partner the partner whose video-on-demand catalogues to consider
     * @param catalogueId the ID of the video-on-demand catalogue
     * @return the VOD catalogue with the specified ID
     */
    VodProduct getVodCatalogue(Transaction transaction, Partner partner, String catalogueId);

    /**
     * Returns a list of subscription packages that contain the VOD catalogue with
     * the specified ID.
     *
     * @param transaction the transaction to use
     * @param partner the partner whose video-on-demand catalogues and subscriptions to consider
     * @param catalogueId the ID of the video-on-demand catalogue
     * @return the list of subscription packages containing the specified VOD catalogue
     */
    List<PackageProduct> getPackagesForCatalogue(Transaction transaction, Partner partner, String catalogueId);

    /**
     * Returns a list of subscription packages that contain the TV-channel with
     * the specified ID.
     *
     * @param transaction the transaction to use
     * @param partner the partner whose TV-channels and subscriptions to consider
     * @param tvChannelCode the code of the TV-channel for which to find subscription packages
     * @return the list of subscription packages containing the specified TV-channel
     */
    List<PackageProduct> getPackagesForTvChannel(Transaction transaction, Partner partner, String tvChannelCode);

    /**
     * Get all matchers matching the given key. The returned matchers are not
     * locked.
     *
     * @param transaction the transaction to use
     * @param key the key which to match with existing matchers
     * @return a collection of matchers where each matcher matches the given key
     */
    Set<Matcher> getMatchers(Transaction transaction, MatcherKey key);

    /**
     * Modifies all products having the specified <code>oldValue</code> for the
     * specified <code>attribute</code> in the related field to contain the
     * specified <code>newValue</code>.
     *
     * @param transaction the transaction to use
     * @param attribute the attribute in the related field whose value to change
     * @param oldValue the old value of the attribute that will be changed
     * @param newValue the new value of the attribute
     */
    void setRelatedFieldForAll(Transaction transaction, Attribute attribute, Value oldValue, Value newValue);

    /**
     * Selects tv-programme products for the given partner within the given interval
     * and passes them to the given {@link DataSet} builder.
     * Selected are all the shows active at <code>recommendTimeMillis</code>, those
     * that will start playing in <code>positiveOffsetMillis</code> from the
     * <code>recommendTimeMillis</code>, and those that have started playing in
     * <code>negativeOffsetMillis</code> from recommendTimeMillis but only if they
     * were played on catch-up-able TV channels, and only to the extent of the
     * catch-up time on the TV-channel a show was played on.
     * Products marked as deleted are not included.
     *
     * @param transaction the transaction to use
     * @param partner the partner for which to retrieve tv-programmes
     * @param recommendTimeMillis timestamp in milliseconds from the epoch, for which to find active tv-programmes
     * @param negativeOffsetMillis maximum offset into the past relative to recommendTimeMillis, in milliseconds
     * @param positiveOffsetMillis maximum offset into the future relative to recommendTimeMillis, in milliseconds
     * @param subscriptionPackageIds set of IDs of products of type package, identifying active subscription packages of a consumer, to be used in determining the {@link VideoData#isSubscribed} flag of a content
     * @param outputBuilder the builder to use for storing TV-programmes
     */
    void tvProgrammesInIntervalForPartner(Transaction transaction, Partner partner, long recommendTimeMillis, long negativeOffsetMillis, long positiveOffsetMillis, TLongSet subscriptionPackageIds, DataSet.Builder<VideoData> outputBuilder);

    /**
     * Selects video products for the given partner, that were/are/will be active at
     * the specified timestamp, and passes them to the given {@link DataSet} builder.
     *
     * @param transaction the transaction to use
     * @param partner the partner for which to retrieve videos
     * @param recommendTimeMillis timestamp in milliseconds from the epoch, for which to find active video products
     * @param subscriptionPackageIds set of IDs of products of type package, identifying active subscription packages of a consumer, to be used in determining the {@link VideoData#isSubscribed} flag of a content
     * @param outputBuilder the builder to use for storing video products
     */
    void vodForPartner(Transaction transaction, Partner partner, long recommendTimeMillis, TLongSet subscriptionPackageIds, DataSet.Builder<VideoData> outputBuilder);

    /**
     * Equips the given video and/or tv-programme products with additional data required
     * for filtering, and adds it to the provided dataset builder. This is usefule in
     * cases where the products have been externally provided, but we need additional
     * data to be able to perform filtering.
     *
     * @param transaction the transaction to use
     * @param videos the list of video and/or tv-programme products
     * @param subscriptionPackageIds set of IDs of products of type package, identifying active subscription packages of a consumer, to be used in determining the {@link VideoData#isSubscribed} flag of a content
     * @param outputBuilder the builder to use for storing TV-programmes
     */
    void buildDatasetFromVideos(Transaction transaction, Iterable<? extends GeneralVideoProduct> videos, TLongSet subscriptionPackageIds, DataSet.Builder<VideoData> outputBuilder);

    /**
     * Returns the list of TV-channels for the given partner.
     *
     * @param transaction the transaction to use
     * @param partner the partner for which to list TV-channels
     * @return the list of TV-channels for the given partner
     */
    List<TvChannelProduct> getTvChannelsForPartner(Transaction transaction, Partner partner);

    /**
     * Invokes the given consumer with every tv-programme intersecting or contained in the given time range for the
     * given tv-channel.
     *
     * @param transaction the transaction to use
     * @param partner the partner for which to retrieve tv-programmes
     * @param tvChannelCode the tv-channel where to find tv-programmes
     * @param beginTimeMillis the start of the time range, in milliseconds
     * @param endTimeMillis the end of the time range, in milliseconds
     * @param consumer the consumer to process each tv-programme
     */
    void forEachOverlappingTvProgramme(Transaction transaction, Partner partner, String tvChannelCode, Long beginTimeMillis, Long endTimeMillis, Consumer<TvProgrammeProduct> consumer);
}
