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

import java.lang.reflect.ParameterizedType;
import com.google.inject.*;
import com.google.inject.spi.Message;
import com.google.inject.util.Types;

class ProvisionExceptionHandler {
    private final Injector injector;

    @Inject
    ProvisionExceptionHandler(final Injector injector) {
        this.injector = injector;
    }

    public void handle(final ProvisionException e) {
        for (final Message message : e.getErrorMessages()) {
            handleException(message.getCause());
        }
    }

    private void handleException(final Throwable throwable) {
        final ParameterizedType type = Types.newParameterizedType(
                ExceptionHandler.class, throwable.getClass());
        @SuppressWarnings("unchecked")
        final ExceptionHandler<Throwable> handler =
            (ExceptionHandler<Throwable>) injector.getInstance(Key.get(type));
        handler.handle(throwable);
    }
}
