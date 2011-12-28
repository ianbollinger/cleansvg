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

import java.text.DecimalFormat;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
class Formatter {
    private final int maxFloatDigits;

    @Inject
    Formatter() {
        // TODO: inject;
        this(16);
    }

    private Formatter(final int maxFloatDigits) {
        this.maxFloatDigits = maxFloatDigits;
    }
    
    public String formatFloat(final float number) {
        return formatFloat(number, false, maxFloatDigits);
    }

    public String formatFloat(final float number, final int precision) {
        return formatFloat(number, false, precision);
    }

    String formatFloat(final float number, final boolean space,
            final int precision) {
        final long longNumber = (long) number;
        final String prefix = (space && number >= 0f) ? " " : "";
        if (longNumber == number) {
            return formatLong(longNumber, prefix);
        }
        if (Math.abs(number) < Math.pow(10.0, -precision)) {
            return space ? " 0" : "0";
        }
        final String formatString = (Math.abs(number) > 0.001f) ? "." : "#.#E0";
        final DecimalFormat format = new DecimalFormat(formatString);
        format.setMaximumFractionDigits(precision);
        return prefix + StringUtils.stripEnd(format.format(number), ".");
    }

    private String formatLong(final long longNumber, final String prefix) {
        if (longNumber == 0L || longNumber % 1000L != 0) {
            return prefix + longNumber;
        }
        int i = 3;
        while (longNumber % (long) Math.pow(10.0, i) == 0L) {
            ++i;
        }
        --i;
        return prefix + longNumber / (long) Math.pow(10.0, i) + "E" + i;
    }
}
