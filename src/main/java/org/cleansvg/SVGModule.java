/*
 * Copyright 2011 Ian D. Bollinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleansvg;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.io.*;
import java.lang.annotation.*;
import java.util.*;
import com.google.common.base.Charsets;
import com.google.common.io.*;
import com.google.inject.*;

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
class SVGModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides @Singleton @Colors
    public Map<String, String> providesColors() {
        return loadProperties("colors");
    }

    @Provides @Singleton @Defaults
    public Map<String, String> providesDefaults() {
        return loadProperties("defaults");
    }

    @Provides @Singleton @Namespaces
    public Map<String, String> providesNamespaces() {
        return loadProperties("namespaces");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map<String, String> loadProperties(final String resource) {
        final String fileName = resource + ".properties";
        final File file = new File(fileName);
        final Reader reader;
        try {
            reader = Files.newReader(file, Charsets.UTF_8);
        } catch (final FileNotFoundException e) {
            // logger.log(Level.SEVERE, "Cannot open " + fileName + ".", e);
            throw new RuntimeException(e);
        }
        final Properties properties = new Properties();
        // boolean thrown = true;
        try {
            properties.load(reader);
            // thrown = false;
        } catch (final IOException e) {
            // logger.log(Level.SEVERE, "Cannot read " + fileName + ".", e);
        } finally {
            // Closeables.close(reader, thrown);
            Closeables.closeQuietly(reader);
        }
        return (Map) properties;
    }

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface Colors {}

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface Defaults {}

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface Namespaces {}
}
