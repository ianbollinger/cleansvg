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

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
public final class Main implements Runnable {
    private final Task runner;

    @Inject
    Main(final Task runner) {
        this.runner = runner;
    }

    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(
                new CommandLineModule(args));
        try {
            final Module module = injector.getInstance(OptionsModule.class);
            final Runnable main = injector.createChildInjector(module)
                    .getInstance(Main.class);
            main.run();
        } catch (final ProvisionException e) {
            /*try {
                injector.getInstance(ProvisionExceptionHandler.class)
                        .handle(e);
            } catch (final ConfigurationException e2) {
                throw e;
            }*/
            throw e;
        }
    }

    @Override
    public void run() {
        runner.run();
    }
}
