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
package com.gurucue.recommendations.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Java file object that is cached in memory.
 */
public final class CachedJavaFileObject extends SimpleJavaFileObject {
    private final ByteArrayOutputStream fileObject = new ByteArrayOutputStream();

    CachedJavaFileObject(final URI uri, final Kind kind) {
        super(uri, kind);
    }

    @Override
    public OutputStream openOutputStream() {
        fileObject.reset();
        return fileObject;
    }

    byte[] getBytes() {
        final byte[] result = fileObject.toByteArray();
        return result;
    }
}
