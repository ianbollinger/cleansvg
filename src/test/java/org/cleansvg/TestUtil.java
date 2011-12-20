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

import static org.junit.Assert.*;
import org.cleansvg.Formatter;
import org.junit.Test;

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
public final class TestUtil {
    private final Formatter formatter = new Formatter();

    @Test
    public void testFormatFloat() {
        assertEquals(".0625", formatter.formatFloat(0.0625f));
        assertEquals(".125", formatter.formatFloat(0.125f));
        assertEquals(".25", formatter.formatFloat(0.25f));
        assertEquals(".5", formatter.formatFloat(0.5f));
        assertEquals("0", formatter.formatFloat(0.0f));
        assertEquals("1", formatter.formatFloat(1.0f));
        assertEquals("10", formatter.formatFloat(10.0f));
        assertEquals("100", formatter.formatFloat(100.0f));
        assertEquals("1E3", formatter.formatFloat(1000.0f));
        assertEquals("1100", formatter.formatFloat(1100.0f));
        //assertEquals("1100.123", Util.formatFloat(1100.123f));
        assertEquals("11E3", formatter.formatFloat(11000.0f));
        assertEquals("-11E3", formatter.formatFloat(-11000.0f));
        assertEquals("9.765625E-4", formatter.formatFloat(0.0009765625f));
        assertEquals(".0078125", formatter.formatFloat(0.0078125f));
    }

    /*
     * @Test public void shouldRound() { svg.path :d => "M-3.7,-2.3 l7.1,8.9"
     * }.elements['path/@d'].value.should == "M-4-2l7,9" }
     */

    @Test
    public void shouldStripTrailingZeros() {
        assertEquals("-3.7", formatter.formatFloat(-3.7f, 1));
        assertEquals("-2", formatter.formatFloat(-2.0f, 1));
        assertEquals("7", formatter.formatFloat(7.0f, 1));
        assertEquals("8.9", formatter.formatFloat(8.9f, 1));
    }
}
