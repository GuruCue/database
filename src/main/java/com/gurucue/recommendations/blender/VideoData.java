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
import com.gurucue.recommendations.dto.ConsumerEntity;
import com.gurucue.recommendations.entity.Partner;
import com.gurucue.recommendations.entity.product.GeneralVideoProduct;
import com.gurucue.recommendations.entity.product.PackageProduct;
import com.gurucue.recommendations.entity.product.TvProgrammeProduct;
import com.gurucue.recommendations.entity.product.VideoProduct;
import com.gurucue.recommendations.entity.product.VodProduct;
import com.gurucue.recommendations.entitymanager.ProductManager;
import gnu.trove.set.TLongSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains data required and optimized for video and tv-programme filtering.
 *
 * @see DataValue
 * @see DataSet
 */
public final class VideoData extends DataValue {
    private static final Logger log = LogManager.getLogger(VideoData.class);

    /** The actual video item being processed. */
    public final GeneralVideoProduct video;
    /**
     * <code>true</code> for TV-programmes (<code>video</code> is of type
     * {@link com.gurucue.recommendations.entity.product.TvProgrammeProduct}),
     * <code>false</code> for videos (<code>video</code> is of type
     * {@link com.gurucue.recommendations.entity.product.VideoProduct}).
     */
    public final boolean isTvProgramme; // it's either a VideoProduct or a TvProgrammeProduct

    // ===== in case this is a VideoProduct =====

    /**
     * If <code>isTvProgramme==false</code>: the {@link VodProduct}
     * representing the video-on-demand catalogue that this <code>video</code>
     * belongs to; <code>null</code> otherwise.
     */
    public final VodProduct vod;
    /**
     * If <code>isTvProgramme==false</code>: the list of {@link PackageProduct}
     * representing all the subscription packages that contain the
     * <code>vod</code> catalogue having this <code>video</code>;
     * <code>null</code> otherwise.
     */
    public final List<PackageProduct> productPackages;

    // ===== in case this is a TvProgrammeProduct =====

    /**
     * If <code>isTvProgramme==true</code>: the list of {@link TvChannelData}
     * representing all TV-channels that this TV-programme (<code>video</code>)
     * is broadcast on; <code>null</code> otherwise.
     */
    public final List<TvChannelData> availableTvChannels;
    /**
     * If <code>isTvProgramme==true</code>: the list of {@link TvChannelData}
     * representing TV-channels that this TV-programme (<code>video</code>)
     * is broadcast on and that are allowed to be used further down the
     * filter pipeline, and included in recommendations at the end;
     * <code>null</code> otherwise. It must be a subset of
     * {@link #availableTvChannels}.
     */
    public final Set<TvChannelData> chosenTvChannels;

    // ===== the rest of the attributes =====

    /** Whether the consumer is subscribed to a package that contains this content. */
    public final boolean isSubscribed;
    /** Tags to include with this item for a recommender. Tags are set by filters. */
    public final Set<String> tags = new HashSet<>();

    // ===== attributes set by recommender =====

    /** Filled by the grid recommender: the line in the grid. */
    public int gridLine = -1;
    /** Filled by a recommender: the prediction. */
    public double prediction = -1;
    /** Filled by a recommender: explanation, */
    public String explanation = null;
    /**
     * Filled by a recommender: formatted explanation that can be displayed to
     * the user. Every explanation maps to its weight (relevancy), so the explanations
     * can be sorted.
     */
    public Map<String, Float> prettyExplanations;

    // ===== attributes set by processing, such as filters, blenders =====

    public Rank rank = null;

    public VideoData(
            final GeneralVideoProduct video,
            final boolean isTvProgramme,
            final VodProduct vod,
            final List<PackageProduct> productPackages,
            final List<TvChannelData> availableTvChannels,
            final boolean isSubscribed
    ) {
        this.video = video;
        this.isTvProgramme = isTvProgramme;
        this.vod = vod;
        this.productPackages = productPackages;
        this.availableTvChannels = availableTvChannels;
        this.isSubscribed = isSubscribed;
        if (isTvProgramme) {
            this.chosenTvChannels = new HashSet<>(availableTvChannels.size());
            if (isSubscribed) {
                availableTvChannels.forEach((final TvChannelData cd) -> { if (cd.isSubscribed) chosenTvChannels.add(cd); });
            }
            else this.chosenTvChannels.addAll(availableTvChannels);
        }
        else this.chosenTvChannels = null;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (!(other instanceof VideoData)) return false;
        return ((VideoData)other).video.videoMatchId == video.videoMatchId;
    }

    @Override
    public int hashCode() {
        final long id = video.videoMatchId;
        return (int)(id ^ (id >>> 32));
    }

    /**
     * Factory method for creating an instance containing a {@link VideoProduct} type instance.
     *
     * @param video the {@link VideoProduct} instance
     * @param vod the {@link VodProduct} instance corresponding to the catalogue to which the video belongs
     * @param productPackages the subscription packages that can be currently bought in order to get the video
     * @param isSubscribed whether the consumer is already subscribed to a package through which this video is currently available
     * @return the instance for the {@link VideoProduct} type
     */
    public static VideoData forVideo(
            final VideoProduct video,
            final VodProduct vod,
            final List<PackageProduct> productPackages,
            final boolean isSubscribed
    ) {
        return new VideoData(video, false, vod, productPackages, null, isSubscribed);
    }

    /**
     * Factory method for creating an instance containing a {@link TvProgrammeProduct} type instance.
     *
     * @param tvProgramme the {@link TvProgrammeProduct} instance
     * @param availableTvChannels the list of TV-channels through which this TV-programme is currently available
     * @param isSubscribed whether the consumer is already subscribed to a package containing a TV-channel through which the TV-programme is available
     * @return the instance for the {@link TvProgrammeProduct} type
     */
    public static VideoData forTvProgramme(
            final TvProgrammeProduct tvProgramme,
            final List<TvChannelData> availableTvChannels,
            final boolean isSubscribed
    ) {
        return new VideoData(tvProgramme, true, null, null, availableTvChannels, isSubscribed);
    }

    public static DataSet<VideoData> buildDataSetOfVideosAndTvProgrammes(
            final Transaction transaction,
            final Partner partner,
            final ConsumerEntity consumer,
            final DuplicateResolver<VideoData> duplicateResolver,
            final long requestTimestampMillis,
            final long maxCatchupMillis,
            final long maxLiveMillis
    ) {
        final TLongSet activeSubscriptionPackages = consumer.activeRelationProductIds(requestTimestampMillis);
        final ProductManager pm = transaction.getLink().getProductManager();
        final DataSet.Builder<VideoData> dataBuilder = new DataSet.Builder<>(duplicateResolver, null);
        final int zeroSize = dataBuilder.size();
        final long timeTvProgramme = System.nanoTime();
        pm.tvProgrammesInIntervalForPartner(transaction, partner, requestTimestampMillis, maxCatchupMillis, maxLiveMillis, activeSubscriptionPackages, dataBuilder);
        final long timeVod = System.nanoTime();
        final int middleSize = dataBuilder.size();
        pm.vodForPartner(transaction, partner, requestTimestampMillis, activeSubscriptionPackages, dataBuilder);
        final long timeEnd = System.nanoTime();
        final int finalSize = dataBuilder.size();
        final DataSet<VideoData> ds = dataBuilder.build();
        ds.log(new DataSet.InfoLogStringBuilder(new StringBuilder(128).append("----- Building tv-programme data from ").append(middleSize - zeroSize).append(" items: ").append(timeVod - timeTvProgramme).append(" ns, building video data from ").append(finalSize - middleSize).append(" items: ").append(timeEnd - timeVod).append(" ns\n")));
        return ds;
    }

    public static DataSet<VideoData> buildDataSetOfVideos(
            final Transaction transaction,
            final Partner partner,
            final ConsumerEntity consumer,
            final DuplicateResolver<VideoData> duplicateResolver,
            final long requestTimestampMillis
    ) {
        final DataSet.Builder<VideoData> dataBuilder = new DataSet.Builder<>(duplicateResolver, null);
        final long timeStart = System.nanoTime();
        transaction.getLink().getProductManager().vodForPartner(transaction, partner, requestTimestampMillis, consumer.activeRelationProductIds(requestTimestampMillis), dataBuilder);
        final long timeEnd = System.nanoTime();
        final DataSet<VideoData> ds = dataBuilder.build();
        ds.log(new DataSet.InfoLogStringBuilder(new StringBuilder(128).append("----- Building video data from ").append(ds.size()).append(" items: ").append(timeEnd - timeStart).append(" ns\n")));
        return ds;
    }

    public static DataSet<VideoData> buildDataSetOfTvProgrammes(
            final Transaction transaction,
            final Partner partner,
            final ConsumerEntity consumer,
            final DuplicateResolver<VideoData> duplicateResolver,
            final long requestTimestampMillis,
            final long maxCatchupMillis,
            final long maxLiveMillis
    ) {
        final DataSet.Builder<VideoData> dataBuilder = new DataSet.Builder<>(duplicateResolver, null);
        final long timeStart = System.nanoTime();
        transaction.getLink().getProductManager().tvProgrammesInIntervalForPartner(transaction, partner, requestTimestampMillis, maxCatchupMillis, maxLiveMillis, consumer.activeRelationProductIds(requestTimestampMillis), dataBuilder);
        final long timeEnd = System.nanoTime();
        final DataSet<VideoData> ds = dataBuilder.build();
        ds.log(new DataSet.InfoLogStringBuilder(new StringBuilder(128).append("----- Building tv-programme data from ").append(ds.size()).append(" items: ").append(timeEnd - timeStart).append(" ns\n")));
        return ds;
    }

    public static DataSet<VideoData> buildDataSet(
            final Transaction transaction,
            final ConsumerEntity consumer,
            final DuplicateResolver<VideoData> duplicateResolver,
            final long requestTimestampMillis,
            final Iterable<? extends GeneralVideoProduct> products
    ) {
        final DataSet.Builder<VideoData> dataBuilder = new DataSet.Builder<>(duplicateResolver, null);
        final long timeStart = System.nanoTime();
        transaction.getLink().getProductManager().buildDatasetFromVideos(transaction, products, consumer.activeRelationProductIds(requestTimestampMillis), dataBuilder);
        final long timeEnd = System.nanoTime();
        final DataSet<VideoData> ds = dataBuilder.build();
        ds.log(new DataSet.InfoLogStringBuilder(new StringBuilder(128).append("----- Building data from provided products into a dataset of ").append(ds.size()).append(" items: ").append(timeEnd - timeStart).append(" ns\n")));
        return ds;
    }
}
