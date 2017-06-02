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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents one candidate for recommendations. Input to a recommender
 * is a set of candidates among which to choose/recommend from.
 */
public final class RecommendProduct implements Externalizable {
    private static final long serialVersionUID = 566331093490585906L;
    public long productID;
    public Set<String> productTag;

    public RecommendProduct() {}

    public RecommendProduct(final long productID, final Set<String> productTag)
    {
        this.productID = productID;
        this.productTag = productTag;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong(productID);
        if ((productTag == null) || (productTag.size() == 0)) out.writeInt(0);
        else {
            out.writeInt(productTag.size());
            for (final String s : productTag) out.writeUTF(s);
        }
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        productID = in.readLong();
        final int n = in.readInt();
        productTag = new HashSet<>(n);
        for (int i = n; i > 0; i--) {
            productTag.add(in.readUTF());
        }
    }
}
