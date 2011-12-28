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
import com.google.common.base.*;
import com.google.common.io.*;
import com.google.inject.*;
import org.kohsuke.args4j.*;

@ProvidedBy(OptionsProvider.class)
class OptionsModule extends AbstractModule {

    @Option(name = "-o", aliases = "--out",
            usage = "file to write the output")
    private File output;

    // @Option(name = "-p", aliases = "--precision",
    //         usage = "precision used for coordinates")
    // private int precision = 8;

    // @Option(name = "-c", aliases = "--compact-paths",
    //         usage = "compacts path data")
    // private boolean compactPaths;

    // @Option(name = "-r", aliases = "--relativize",
    //         usage = "make path coordinates relative")
    // private boolean relativize;

    @Option(name = "-h", aliases = {"-?", "--help"},
            usage = "display this help and exit")
    private boolean help;

    // @Option(name = "-d", aliases = "--remove-defaults",
    //         usage = "remove default attribute values")
    // private boolean removeDefaults;

    // @Option(name = "-s", aliases = "--style-attrs",
    //         usage = "convert style attributes")
    // private boolean styleAttrs;

    // @Option(name = "-n", aliases = "--namespaces",
    //         usage = "remove junk namespaces")
    // private boolean namespaces;

    // @Option(name = "-w", aliases = "--whitespace",
    //         usage = "remove extra whitespace")
    // private boolean whitespace;

    // @Option(name = "-t", aliases = "--doctype",
    //         usage = "remove doctype definition")
    // private boolean doctype;

    // @Option(name = "-e", aliases = "--empty",
    //         usage = "remove empty elements")
    // private boolean empty;

    // @Option(name = "-l", aliases = "--line-width",
    //         usage = "specity line-width")
    // private int lineWidth = 72;

    @Argument(usage = "path of SVG image")
    private File file;

    @Override
    protected void configure() {
        bind(new TypeLiteral<OutputSupplier<? extends Writer>>() {})
                .toProvider(OutputProvider.class);
        bindConstant().annotatedWith(InputURI.class)
                .to(file.toURI().toASCIIString());
        bindConstant().annotatedWith(Help.class).to(help);
        install(new SVGModule());
    }

    @Provides
    public Optional<File> providesOutput() {
        return Optional.fromNullable(output);
    }

    @Provides
    public InputSupplier<? extends Reader> provideReader() {
        return Files.newReaderSupplier(file, Charsets.UTF_8);
    }

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface InputURI {}

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface Help {}
}
