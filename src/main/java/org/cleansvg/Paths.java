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

import org.apache.batik.parser.PathParser;

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
// TODO: eliminate statics.
public final class Paths {
    private Paths() {
        // Cannot instantiate.
    }

    public static String compact(final String path,
            final int precision) {
        final PathParser parser = new PathParser();
        final CompactingPathHandler handler = new CompactingPathHandler(
                precision);
        parser.setPathHandler(handler);
        parser.parse(path);
        return handler.getResult();
    }

    public static String compactAndRelativize(final String path,
            final int precision) {
        final PathParser parser = new PathParser();
        final CompactingPathHandler handler = new RelativizingPathHandler(
                precision);
        parser.setPathHandler(handler);
        parser.parse(path);
        return handler.getResult();
    }
}
