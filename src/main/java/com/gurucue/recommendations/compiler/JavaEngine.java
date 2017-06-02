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

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the run-time compiling of Java source files.
 */
public final class JavaEngine {
    private static final Logger log = LogManager.getLogger(JavaEngine.class);

    private final String rootDirectory;
    private Map<String, Class<?>> classes;
    private Map<String, Long> fileTimestamps;

    public JavaEngine(final String rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.classes = Collections.emptyMap(); // starting state
        this.fileTimestamps = Collections.emptyMap();
    }

    public boolean refresh() {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler(); // this does not work under Tomcat: because it is loaded with a different class loader, the file manager does not resolve app libs
        if (compiler == null) throw new ProcessingException("Missing Java compiler; is JDK installed?");
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        // setup the source path
        final List<File> sourceDirectory = Collections.singletonList(new File(rootDirectory));
        final StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(diagnostics, null, Charset.forName("UTF-8"));
        try {
            stdFileManager.setLocation(StandardLocation.SOURCE_PATH, sourceDirectory);
            stdFileManager.setLocation(StandardLocation.SOURCE_OUTPUT, sourceDirectory); // in case we ever want to write sources in the future
        }
        catch (IOException e) {
            try {
                stdFileManager.close();
            } catch (IOException e1) {
                log.error("Failed to close the standard file manager: " + e1.toString(), e1);
            }
            throw new ProcessingException("Failed to set source path to \"" + rootDirectory + "\": " + e.toString(), e);
        }

        // obtain and process the source file paths, to find out if a timestamp changed or a file (dis)appeared, to warrant (re)compilation
        final Iterable<JavaFileObject> sources;
        try {
            sources = stdFileManager.list(StandardLocation.SOURCE_PATH, "", Collections.singleton(JavaFileObject.Kind.SOURCE), true);
        }
        catch (IOException e) {
            try {
                stdFileManager.close();
            } catch (IOException e1) {
                log.error("Failed to close the standard file manager: " + e1.toString(), e1);
            }
            throw new ProcessingException("Failed to obtain sources: " + e.toString(), e);
        }
        int sourceCount = 0;
        int modifiedCount = 0;
        final Map<String, Long> oldFileTimestamp = fileTimestamps; // local variable access, for faster access
        final Map<String, Long> newFileTimestamps = new HashMap<>();
        for (final JavaFileObject obj : sources) {
            final long lastModified = obj.getLastModified();
            final String fileName = obj.getName();
            newFileTimestamps.put(fileName, lastModified);
            final Long oldLastModified = oldFileTimestamp.get(fileName);
            if ((oldLastModified == null) || (oldLastModified.longValue() != lastModified)) modifiedCount++;
            sourceCount++;
        }
        synchronized (this) {
            if ((modifiedCount == 0) && (sourceCount == oldFileTimestamp.size())) {
                log.debug("Refresh will not be done: source files' timestamps have not been modified and no source file is missing");
                try {
                    stdFileManager.close();
                } catch (IOException e1) {
                    log.error("Failed to close the standard file manager: " + e1.toString(), e1);
                }
                return false; // nothing to do
            }
        }

        // setup the classpaths from the existing classloaders
        final List<File> classPaths = new ArrayList<>();
        int classLoaderCount = 0;
        final StringBuilder classLoaderPathLog = new StringBuilder(1024);
        classLoaderPathLog.append("Setting compiler class paths based on paths obtained from class loaders:");
        for (ClassLoader loader = getClass().getClassLoader(); loader != null; loader = loader.getParent()) {
            classLoaderCount++;
            if (loader instanceof URLClassLoader) {
                final URL[] urls = ((URLClassLoader) loader).getURLs();
                if (urls == null) continue;
                classLoaderPathLog.append("\n  Class loader #").append(classLoaderCount).append(" (").append(loader.getClass().getCanonicalName()).append("):");
                final int n = urls.length;
                for (int i = 0; i < n; i++) {
                    final String path = urls[i].getPath();
                    if ((path != null) && (path.length() > 0)) {
                        classPaths.add(new File(path));
                        classLoaderPathLog.append(" ").append(path);
                    }
                }
            }
        }
        classLoaderPathLog.append("\n  Iterated over ").append(classLoaderCount).append(" class loaders");
        log.debug(classLoaderPathLog.toString());
        try {
            stdFileManager.setLocation(StandardLocation.CLASS_PATH, classPaths);
        } catch (IOException e) {
            try {
                stdFileManager.close();
            } catch (IOException e1) {
                log.error("Failed to close the standard file manager: " + e1.toString(), e1);
            }
            throw new ProcessingException("Failed to set class path based on class loaders' paths: " + e.toString(), e);
        }

        // setup the caching file manager and do the compilation
        final CachingFileManager fileManager = new CachingFileManager(stdFileManager);
        try {
            log.debug("Compiling " + sourceCount + " sources in " + rootDirectory);

            final JavaCompiler.CompilationTask job = compiler.getTask(null, fileManager, diagnostics, null, null, sources);
            final boolean compilationSucceeded = job.call();
            final List<Diagnostic<? extends JavaFileObject>> diags = diagnostics.getDiagnostics();
            if (!diags.isEmpty()) {
                final StringBuilder logBuilder = new StringBuilder(diags.size() * 512); // guesstimate
                logBuilder.append("Compile output:");
                diags.forEach((final Diagnostic diag) -> logBuilder.append("\n  ").append(diag.toString()));
                log.debug(logBuilder.toString());
            }

            if (compilationSucceeded) {
                final Map<String, Class<?>> compiledClasses = fileManager.getCachedClasses();
                if (compiledClasses != null) {
                    synchronized (this) {
                        classes = compiledClasses;
                        fileTimestamps = newFileTimestamps;
                    }
                    return true;
                }
            }
        }
        finally {
            try {
                fileManager.close();
            } catch (IOException e) {
                log.error("Failed to close the file manager: " + e.toString(), e);
            }
        }
        return false;
    }

    public Class<?> getClass(final String className) {
        synchronized (this) {
            return classes.get(className);
        }
    }

    public <C> List<Class<? extends C>> getInstancesOf(final Class<C> clazz) {
        final List<Class<? extends C>> result = new ArrayList<>();
        synchronized (this) {
            classes.forEach((final String className, final Class<?> cl) -> {
                if (clazz.isAssignableFrom(cl)) {
                    result.add(cl.asSubclass(clazz));
                }
            });
        }
        return result;
    }
}
