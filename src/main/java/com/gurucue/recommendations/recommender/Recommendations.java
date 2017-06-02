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

/**
 * An envelope for recommendations returned by a recommender.
 */
public final class Recommendations implements Externalizable {
    private static final long serialVersionUID = 3368646384222867430L;
    public Recommendation[] recommendations;

    public Recommendations() {}

    public Recommendations(final Recommendation[] recommendations) {
        this.recommendations = recommendations;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        if ((recommendations == null) || (recommendations.length == 0)) out.writeInt(0);
        else {
            out.writeInt(recommendations.length);
            final int n = recommendations.length;
            for (int i = 0; i < n; i++) recommendations[i].writeExternal(out);
        }
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final int n = in.readInt();
        recommendations = new Recommendation[n];
        for (int i = 0; i < n; i++) {
            final Recommendation r;
            recommendations[i] = r = new Recommendation();
            r.readExternal(in);
        }
    }
}
