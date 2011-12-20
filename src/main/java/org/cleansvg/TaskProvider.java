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

import com.google.inject.*;
import org.cleansvg.OptionsModule.Help;

class TaskProvider implements Provider<Task> {
    private final boolean help;
    private final Provider<CleanTask> mainTaskProvider;
    private final Provider<HelpTask> helpTaskProvider;

    @Inject
    TaskProvider(@Help final boolean help,
            final Provider<CleanTask> mainTaskProvider,
            final Provider<HelpTask> helpTaskProvider) {
        this.help = help;
        this.mainTaskProvider = mainTaskProvider;
        this.helpTaskProvider = helpTaskProvider;
    }

    @Override
    public Task get() {
        if (help) {
            return helpTaskProvider.get();
        }
        return mainTaskProvider.get();
    }
}
