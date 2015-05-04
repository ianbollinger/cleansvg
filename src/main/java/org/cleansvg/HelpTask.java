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

import java.io.*;
import com.google.inject.Inject;
import org.cleansvg.CommandLineModule.StandardOutput;
import org.kohsuke.args4j.*;

class HelpTask implements Task {
    private final CmdLineParser parser;
    private final PrintStream out;

    @Inject
    HelpTask(final CmdLineParser parser,
            @StandardOutput final PrintStream out) {
        this.parser = parser;
        this.out = out;
    }

    @Override
    public void run() {
        out.append("usage: cleansvg [options]... FILE\n");
        parser.printUsage(out);
    }
}
