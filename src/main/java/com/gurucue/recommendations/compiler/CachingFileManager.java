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

import com.gurucue.recommendations.ProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * File manager for the run-time Java compiler.
 */
public final class CachingFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private static final Logger log = LogManager.getLogger(CachingClassLoader.class);

    private final Map<String, CachedJavaFileObject> cache = new ConcurrentHashMap<>();
    private final Set<String> packageNames = new ConcurrentSkipListSet<>();
    private final CachingClassLoader loader = new CachingClassLoader(this);

    CachingFileManager(final StandardJavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public Iterable<JavaFileObject> list(final Location location, final String packageName, final Set<JavaFileObject.Kind> kinds, final boolean recurse) throws IOException {
        final Iterable<JavaFileObject> parent = super.list(location, packageName, kinds, recurse);
        if ((location == StandardLocation.CLASS_PATH) && kinds.contains(JavaFileObject.Kind.CLASS) && packageNames.contains(packageName)) {
            // add our packages
            final List<JavaFileObject> result = new ArrayList<>();
            parent.forEach((final JavaFileObject o) -> result.add(o));
            if (recurse) {
                cache.forEach((final String entryName, final CachedJavaFileObject entryValue) -> {
                    if (entryName.startsWith(packageName)) {
                        result.add(entryValue);
                    }
                });
            }
            else {
                final int dotPos = packageName.length() == 0 ? -1 : packageName.length();
                cache.forEach((final String entryName, final CachedJavaFileObject entryValue) -> {
                    if (entryName.startsWith(packageName) && (entryName.lastIndexOf(".") == dotPos)) {
                        result.add(entryValue);
                    }
                });
            }
            return result;
        }

        return parent;
    }

    @Override
    public String inferBinaryName(final Location location, final JavaFileObject file) {
        final String result;
        if ((location == StandardLocation.CLASS_PATH) && (file instanceof CachedJavaFileObject)) {
            // this is what is given to loader.findClass()
            result = file.getName();
        }
        else result = super.inferBinaryName(location, file);
        return result;
    }

    @Override
    public CachingClassLoader getClassLoader(final Location location) {
        return loader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(final Location location, final String className, final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {
        final CachedJavaFileObject obj;
        try {
            obj = new CachedJavaFileObject(new URI(className), kind);
        }
        catch (URISyntaxException e) {
            throw new IOException("Cannot create a new cached java class object: " + e.toString(), e);
        }
        cache.put(className, obj);
        final int dotPos = className.lastIndexOf(".");
        if (dotPos > 0) {
            final String packageName = className.substring(0, dotPos);
            packageNames.add(packageName);
        }
        log.debug("Cached a new compiled class: " + className);
        return obj;
    }

    public Map<String, Class<?>> getCachedClasses() {
        final Map<String, Class<?>> result = new HashMap<>();
        cache.forEach((final String className, final CachedJavaFileObject fileObj) -> {
            try {
                result.put(className, loader.loadClass(className));
            }
            catch (ClassNotFoundException e) {
                throw new ProcessingException("Could not find class " + className + ": " + e.toString(), e);
            }
        });
        return result;
    }

    public static final class CachingClassLoader extends ClassLoader {
        private final CachingFileManager mgr;

        CachingClassLoader(final CachingFileManager mgr) {
            super(JavaEngine.class.getClassLoader());
            this.mgr = mgr;
        }

        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            final CachedJavaFileObject obj = mgr.cache.get(name);
            if (name == null) return super.findClass(name);
            final byte[] bytes = obj.getBytes();
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
