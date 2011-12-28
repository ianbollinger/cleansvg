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

import org.junit.Test;

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
public final class TestPaths {
    @Test
    public void shouldConvertAbsoluteSegmentsToRelativeOnes() {
        assertEquals("m10 20l40 40",
                Paths.compactAndRelativize("M10,20 L50,60", 16));
    }

    @Test
    public void shouldDetectVerticalLines() {
        assertEquals("m10 20v40",
                Paths.compactAndRelativize("M10,20 L10,60", 16));
    }

    @Test
    public void shouldDetectHorizontalLines() {
        assertEquals("m10 20h30",
                Paths.compactAndRelativize("M10,20 L40,20", 16));
    }

    @Test
    public void shouldEliminateEmptySegments() {
        assertEquals("m10 20h30",
                Paths.compactAndRelativize("M10,20 L10.2,20.2 L40,20", 3));
    }

    @Test
    public void shouldEliminateLastSegmentInPolygon() {
        assertEquals("m0 0v10h10z",
                Paths.compactAndRelativize("M0,0 L0,10 L10,10 L0,0 Z", 16));
    }

    @Test
    public void shouldCollapseStraightCurves() {
        assertEquals("m10 20h30",
                Paths.compactAndRelativize("M10,20 C20,20 30,20 40,20", 16));
    }

    @Test
    public void shouldDoQuadraticBezierCurves() {
        assertEquals("m200 300q200-250 400 0t400 0",
                Paths.compactAndRelativize(
                        "M200,300 Q400,50 600,300 T1000,300", 16));
    }
}
