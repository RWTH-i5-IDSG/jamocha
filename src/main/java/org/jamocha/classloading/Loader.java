/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.classloading;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class to load all class files in a directory or jar.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@UtilityClass
public class Loader {

    /**
     * Recursively loads all class files in the specified directory. The name of the directory is to be split up into
     * the part specifying the path to the start of the package hierarchy and the part specifying which part of the
     * hierarchy is to be loaded. <br /> <b>Example:</b> loadClassesInDirectory("target/classes",
     * "org/jamocha/function/impls/predicates"); loads all class files within
     * "target/classes/org/jamocha/function/impls/predicates"
     * and the files found in that folder there have the package specification "org.jamocha.function.impls.predicates".
     * Class files in sub directories of the predicates folder have their package specification adjusted accordingly.
     *
     * @param pathToDir
     *         the base path to the directory containing the package hierarchy (separator '/')
     * @param packageString
     *         the path within the package hierarchy (separator '/')
     */
    public static void loadClassesInDirectory(final String pathToDir, final String packageString) {
        log.debug("Loading classes in directory file:{}/{}", pathToDir, packageString);
        final Path basePath = FileSystems.getDefault().getPath(pathToDir);
        try (final Stream<Path> files = java.nio.file.Files
                .walk(FileSystems.getDefault().getPath(pathToDir, packageString))) {
            files.filter(file -> !file.toFile().isDirectory() && file.toString().endsWith(".class")).forEach(file -> {
                final Path filePath = basePath.relativize(file);
                final String clazzName = filePathToClassName(filePath.toString());
                log.debug("Loading class {}", clazzName);
                try {
                    Class.forName(clazzName);
                } catch (final ClassNotFoundException ex) {
                    log.catching(ex);
                } catch (final ExceptionInInitializerError ex) {
                    log.catching(ex);
                }
            });
        } catch (final IOException ex) {
            log.catching(ex);
        }
    }

    /**
     * Recursively loads all class files in the specified package.
     *
     * @param packageString
     *         the path within the package hierarchy (separator '/')
     */
    public static void loadClasses(final String packageString) {
        final String string =
                ClassLoader.getSystemResource(Loader.class.getName().replace('.', '/') + ".class").toString();
        if (string.startsWith("jar:")) {
            final String jarFile = string.substring("jar:file:".length(), string.indexOf('!'));
            Loader.loadClassesInJar(jarFile, packageString);
        } else {
            Loader.loadClassesInDirectory("target/classes", packageString);
        }
    }

    /**
     * Recursively loads all class files in the specified jar file within the specified package.
     *
     * @param pathToJar
     *         the path to the jar file containing the package hierarchy (separator '/')
     * @param packageString
     *         the path within the package hierarchy (separator '/')
     */
    public static void loadClassesInJar(final String pathToJar, final String packageString) {
        log.debug("Loading classes in jar:file:{}!/{}", pathToJar, packageString);
        try (final JarFile jarFile = new JarFile(pathToJar)) {
            final URL[] urls = {new URL("jar:file:" + pathToJar + "!/" + packageString)};
            try (final URLClassLoader loader = URLClassLoader.newInstance(urls)) {
                jarFile.stream()
                        .filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".class") && entry.getName()
                                .startsWith(packageString)).forEach(entry -> {
                    final String className = filePathToClassName(entry.getName());
                    log.debug("Loading class {}", className);
                    try {
                        Class.forName(className, true, loader);
                    } catch (final ClassNotFoundException ex) {
                        log.catching(ex);
                    } catch (final ExceptionInInitializerError ex) {
                        log.catching(ex);
                    }
                });
            }
        } catch (final IOException ex) {
            log.catching(ex);
        }
    }

    private static String filePathToClassName(final String filePath) {
        return StringUtils.substring(filePath.replace('/', '.').replace('\\', '.'), 0, -".class".length());
    }
}
