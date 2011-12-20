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
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.inject.*;
import org.kohsuke.args4j.CmdLineParser;

class CommandLineModule extends AbstractModule {
    private final String[] arguments;

    @Inject
    CommandLineModule(final String[] arguments) {
        this.arguments = arguments;
    }

    @Override
    protected void configure() {
        bind(PrintStream.class).annotatedWith(StandardOutput.class)
                .toInstance(System.out);
        bind(PrintStream.class).annotatedWith(StandardError.class)
                .toInstance(System.err);
        bind(String[].class).annotatedWith(Arguments.class)
                .toInstance(arguments);
        bindConstant().annotatedWith(LineSeparator.class)
                .to(System.getProperty("line.separator"));
        bind(new TypeLiteral<List<String>>() {})
                .toInstance(ImmutableList.copyOf(arguments));
        bind(new TypeLiteral<ExceptionHandler<IOException>>() {})
                .to(IOExceptionHandler.class);
        bind(new TypeLiteral<ExceptionHandler<CommandLineException>>() {})
                .to(CommandLineExceptionHandler.class);
        bind(OptionsModule.class).annotatedWith(RawOptions.class)
                .toInstance(new OptionsModule());
    }

    @Provides @Singleton
    public CmdLineParser provideCommandLineParser(
            @RawOptions final OptionsModule options) {
        return new CmdLineParser(options);
    }

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface RawOptions {}

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface StandardOutput {}

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface StandardError {}

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface LineSeparator {}

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
    @interface Arguments {}
}
