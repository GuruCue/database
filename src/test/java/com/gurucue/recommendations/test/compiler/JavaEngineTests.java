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
package com.gurucue.recommendations.test.compiler;

import com.gurucue.recommendations.compiler.JavaEngine;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Rudimentary tests of the Java compiler driver.
 */
public class JavaEngineTests {
    private static final String classEngineTest = "public class EngineTest {\n" +
            "public Runnable getRunnable() { return new RunnableTest(); }\n" +
            "}";

    private static final String classRunnableTest = "public class RunnableTest implements Runnable {\n" +
            "    @Override public void run() {\n" +
            "        System.currentTimeMillis();\n" +
            "    }\n" +
            "}";

    private static final String classLibraryTest = "import com.google.common.collect.ImmutableSet;\n" +
            "import java.util.Set;\n" +
            "public class GuavaTest {\n" +
            "    public Set<String> test(final String s) {\n" +
            "        return ImmutableSet.of(s);\n" +
            "    }\n" +
            "}\n";

    @Test
    public void testEngineBasic() throws IOException {
        System.out.println("---------- testEngineBasic ----------");
        Path pathEngineTest = null;
        Path pathRunnableTest = null;
        final Path tempPath = Files.createTempDirectory("java_compiler_test");
        try {
            pathEngineTest = tempPath.resolve("EngineTest.java");
            pathRunnableTest = tempPath.resolve("RunnableTest.java");
            Files.write(pathEngineTest, classEngineTest.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(pathRunnableTest, classRunnableTest.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            final JavaEngine engine = new JavaEngine(tempPath.toString());
            Assert.assertTrue("Compilation failed", engine.refresh());
            final Class<?> clazz1 = engine.getClass("EngineTest");
            Assert.assertNotNull("Cannot resolve class EngineTest", clazz1);
            final List<Class<? extends Runnable>> runnables = engine.getInstancesOf(Runnable.class);
            Assert.assertTrue("No Runnable implementations were found", runnables.size() == 1);
        }
        finally {
            if (pathRunnableTest != null) Files.deleteIfExists(pathRunnableTest);
            if (pathEngineTest != null) Files.deleteIfExists(pathEngineTest);
            Files.deleteIfExists(tempPath);
        }
    }

    @Test
    public void testEnginePackages() throws IOException {
        System.out.println("---------- testEnginePackages ----------");
        Path pathEngineTest = null;
        Path pathRunnableTest = null;
        Path pathPackageOne = null;
        Path pathPackageTwo = null;
        final Path tempPath = Files.createTempDirectory("java_compiler_test");
        try {
            pathPackageOne = tempPath.resolve("one");
            pathPackageTwo = tempPath.resolve("two");
            Files.createDirectory(pathPackageOne);
            Files.createDirectory(pathPackageTwo);
            pathEngineTest = pathPackageOne.resolve("EngineTest.java");
            pathRunnableTest = pathPackageTwo.resolve("RunnableTest.java");
            Files.write(pathEngineTest, ("package one;\nimport two.RunnableTest;\n" + classEngineTest).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(pathRunnableTest, ("package two;\n" + classRunnableTest).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            final JavaEngine engine = new JavaEngine(tempPath.toString());
            Assert.assertTrue("Compilation failed", engine.refresh());
            final Class<?> clazz1 = engine.getClass("one.EngineTest");
            Assert.assertNotNull("Cannot resolve class one.EngineTest", clazz1);
            final List<Class<? extends Runnable>> runnables = engine.getInstancesOf(Runnable.class);
            Assert.assertTrue("No Runnable implementations were found", runnables.size() == 1);
        }
        finally {
            if (pathRunnableTest != null) Files.deleteIfExists(pathRunnableTest);
            if (pathEngineTest != null) Files.deleteIfExists(pathEngineTest);
            if (pathPackageOne != null) Files.deleteIfExists(pathPackageOne);
            if (pathPackageTwo != null) Files.deleteIfExists(pathPackageTwo);
            Files.deleteIfExists(tempPath);
        }
    }

    @Test
    public void testLibraryUse() throws IOException {
        System.out.println("---------- testLibraryUse ----------");
        Path pathLibraryTest = null;
        final Path tempPath = Files.createTempDirectory("java_compiler_test");
        try {
            pathLibraryTest = tempPath.resolve("GuavaTest.java");
            Files.write(pathLibraryTest, classLibraryTest.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            final JavaEngine engine = new JavaEngine(tempPath.toString());
            Assert.assertTrue("Compilation failed", engine.refresh());
            final Class<?> clazz1 = engine.getClass("GuavaTest");
            Assert.assertNotNull("Cannot resolve class GuavaTest", clazz1);
        }
        finally {
            if (pathLibraryTest != null) Files.deleteIfExists(pathLibraryTest);
            Files.deleteIfExists(tempPath);
        }
    }

    @Test@Ignore // requires the presence of /opt/GuruCue/blenders
    public void testFilters() {
        System.out.println("---------- testFilters ----------");
        if (!Files.exists(Paths.get("/opt/GuruCue/blenders"))) {
            System.out.println("Not testing filters compilation: the path /opt/GuruCue/blenders does not exist");
            return;
        }
        final JavaEngine engine = new JavaEngine("/opt/GuruCue/blenders");
        Assert.assertTrue("The filters in /opt/GuruCue/blenders did not compile", engine.refresh());
    }
}
