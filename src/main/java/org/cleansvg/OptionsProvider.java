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
import java.util.List;
import com.google.inject.*;
import org.kohsuke.args4j.*;
import org.cleansvg.CommandLineModule.RawOptions;

class OptionsProvider implements Provider<OptionsModule> {
    private final List<String> arguments;
    private final CmdLineParser parser;
    private final OptionsModule options;

    @Inject
    OptionsProvider(final List<String> arguments,
            final CmdLineParser parser,
            @RawOptions final OptionsModule options) {
        this.arguments = arguments;
        this.parser = parser;
        this.options = options;
    }

    @Override
    public OptionsModule get() {
        try {
            parser.parseArgument(arguments);
        } catch (final CmdLineException e) {
            throw CommandLineException.causedBy(e);
        }
        return options;
    }
}
