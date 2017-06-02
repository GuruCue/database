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

import com.gurucue.recommendations.entity.product.PackageProduct;
import com.gurucue.recommendations.entity.product.TvChannelProduct;

import java.util.List;

/**
 * Groups all data about a TV-channel at a specific point in time (which influences subscriptions).
 */
public final class TvChannelData {
    public final TvChannelProduct tvChannel; // the tv-channel product
    public final List<PackageProduct> productPackages; // the packages that the tv-channel can be bought through
    public final boolean isSubscribed;

    public TvChannelData(final TvChannelProduct tvChannel, final List<PackageProduct> productPackages, final boolean isSubscribed) {
        this.tvChannel = tvChannel;
        this.productPackages = productPackages;
        this.isSubscribed = isSubscribed;
    }
}
