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

import com.google.inject.Inject;

class RelativizingPathHandler extends CompactingPathHandler {
    @Inject
    RelativizingPathHandler(final int precision) {
        super(precision);
    }

    @Override
    public void arcAbs(final float rx, final float ry,
            final float xAxisRotation, final boolean largeArcFlag,
            final boolean sweepFlag, final float x, final float y) {
        arcRel(rx, ry, xAxisRotation, largeArcFlag, sweepFlag,
                (float) (x - positionX), (float) (y - positionY));
    }

    @Override
    public void curvetoCubicAbs(final float x1, final float y1, final float x2,
            final float y2, final float x, final float y) {
        curvetoCubicRel((float) (x1 - positionX), (float) (y1 - positionY),
                (float) (x2 - positionX), (float) (y2 - positionY),
                (float) (x - positionX), (float) (y - positionY));
    }

    @Override
    public void curvetoCubicSmoothAbs(final float x2, final float y2,
            final float x, final float y) {
        curvetoCubicSmoothRel((float) (x2 - positionX),
                (float) (y2 - positionY), (float) (x - positionX),
                (float) (y - positionY));
    }

    @Override
    public void curvetoQuadraticAbs(final float x1, final float y1,
            final float x, final float y) {
        curvetoQuadraticRel((float) (x1 - positionX), (float) (y1 - positionY),
                (float) (x - positionX), (float) (y - positionY));
    }

    @Override
    public void curvetoQuadraticSmoothAbs(final float x, final float y) {
        curvetoQuadraticSmoothRel((float) (x - positionX),
                (float) (y - positionY));
    }

    @Override
    public void linetoAbs(final float x, final float y) {
        linetoRel((float) (x - positionX), (float) (y - positionY));
    }

    @Override
    public void linetoHorizontalAbs(final float x) {
        linetoHorizontalRel((float) (x - positionX));
    }

    @Override
    public void linetoVerticalAbs(final float y) {
        linetoVerticalRel((float) (y - positionY));
    }

    @Override
    public void movetoAbs(final float x, final float y) {
        movetoRel((float) (x - positionX), (float) (y - positionY));
    }
}
