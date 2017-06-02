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

import com.gurucue.recommendations.entity.ProductType;
import com.gurucue.recommendations.entitymanager.ProductTypeManager;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.HashMap;
import java.util.Map;

public final class ProductTypeCodes {
    public final ProductType video;
    public final ProductType tvProgramme;
    public final ProductType tvChannel;
    public final ProductType package_;
    public final ProductType tvod;
    public final ProductType svod;
    public final ProductType interactive;
    public final ProductType series;

    public final long idForVideo;
    public final long idForTvProgramme;
    public final long idForTvChannel;
    public final long idForPackage;
    public final long idForTvod;
    public final long idForSvod;
    public final long idForInteractive;
    public final long idForSeries;

    private final Map<String, ProductType> identifierMapping = new HashMap<>();
    private final TLongObjectMap<ProductType> idMapping = new TLongObjectHashMap<>();

    public ProductTypeCodes(final ProductTypeManager productTypeManager) {
        video = productTypeManager.getByIdentifier(ProductType.VIDEO);
        tvProgramme = productTypeManager.getByIdentifier(ProductType.TV_PROGRAMME);
        tvChannel = productTypeManager.getByIdentifier(ProductType.TV_CHANNEL);
        package_ = productTypeManager.getByIdentifier(ProductType.PACKAGE);
        tvod = productTypeManager.getByIdentifier(ProductType.TVOD);
        svod = productTypeManager.getByIdentifier(ProductType.SVOD);
        interactive = productTypeManager.getByIdentifier(ProductType.INTERACTIVE);
        series = productTypeManager.getByIdentifier(ProductType.SERIES);

        idForVideo = video.getId();
        idForTvProgramme = tvProgramme.getId();
        idForTvChannel = tvChannel.getId();
        idForPackage = package_.getId();
        idForTvod = tvod.getId();
        idForSvod = svod.getId();
        idForInteractive = interactive.getId();
        idForSeries = series.getId();

        for (final ProductType pt : productTypeManager.list()) {
            identifierMapping.put(pt.getIdentifier(), pt);
            idMapping.put(pt.getId(), pt);
        }
    }

    public final ProductType byIdentifier(final String identifier) {
        return identifierMapping.get(identifier);
    }

    public final ProductType byId(final long id) {
        return idMapping.get(id);
    }
}
