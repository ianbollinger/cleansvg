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
import org.apache.batik.parser.PathHandler;

class CompactingPathHandler implements PathHandler {
    protected double positionX;
    protected double positionY;
    private final int precision;
    private final StringBuilder result;
    private final Formatter formatter;

    @Inject
    CompactingPathHandler(final int precision) {
        this.precision = precision;
        this.result = new StringBuilder();
        // TODO: inject
        this.formatter = new Formatter();
    }

    public String getResult() {
        return result.toString();
    }

    @Override
    public void arcAbs(final float rx, final float ry,
            final float xAxisRotation, final boolean largeArcFlag,
            final boolean sweepFlag, final float x, final float y) {
        positionX = x;
        positionY = y;
        result.append('A')
            .append(formatter.formatFloat(rx, false, precision))
            .append(formatter.formatFloat(ry, true, precision))
            .append(formatter.formatFloat(xAxisRotation, true, precision))
            .append(largeArcFlag ? " 1" : " 0")
            .append(sweepFlag ? " 1" : " 0")
            .append(formatter.formatFloat(x, true, precision))
            .append(formatter.formatFloat(y, true, precision));
    }

    @Override
    public void arcRel(final float rx, final float ry,
            final float xAxisRotation, final boolean largeArcFlag,
            final boolean sweepFlag, final float x, final float y) {
        positionX += x;
        positionY += y;
        result.append('a')
                .append(formatter.formatFloat(rx, false, precision))
                .append(formatter.formatFloat(ry, true, precision))
                .append(formatter.formatFloat(xAxisRotation, true, precision))
                .append(largeArcFlag ? " 1" : " 0")
                .append(sweepFlag ? " 1" : " 0")
                .append(formatter.formatFloat(x, true, precision))
                .append(formatter.formatFloat(y, true, precision));
        // TODO: optimize
    }

    @Override
    public void closePath() {
        positionX = 0.0;
        positionY = 0.0;
        result.append('z');
    }

    @Override
    public void curvetoCubicAbs(final float x1, final float y1,
            final float x2, final float y2, final float x, final float y) {
        positionX = x;
        positionY = y;
        result.append('C')
                .append(formatter.formatFloat(x1, false, precision))
                .append(formatter.formatFloat(y1, true, precision))
                .append(formatter.formatFloat(x2, true, precision))
                .append(formatter.formatFloat(y2, true, precision))
                .append(formatter.formatFloat(x, true, precision))
                .append(formatter.formatFloat(y, true, precision));
    }

    @Override
    public void curvetoCubicRel(final float x1, final float y1,
            final float x2, final float y2, final float x, final float y) {
        final String stringX1 = formatter.formatFloat(x1, false, precision);
        final String stringY1 = formatter.formatFloat(y1, true, precision);
        final String stringX2 = formatter.formatFloat(x2, true, precision);
        final String stringY2 = formatter.formatFloat(y2, true, precision);
        if ("0".equals(stringX1) && " 0".equals(stringY1)
                && " 0".equals(stringX2) && " 0".equals(stringY2)) {
            linetoRel(x, y);
        }
        positionX += x;
        positionY += y;
        result.append('c')
                .append(stringX1).append(stringY1)
                .append(stringX2).append(stringY2)
                .append(formatter.formatFloat(x, true, precision))
                .append(formatter.formatFloat(y, true, precision));
        // TODO: optimize
    }

    @Override
    public void curvetoCubicSmoothAbs(final float x2, final float y2,
            final float x, final float y) {
        positionX = x;
        positionY = y;
        result.append('S')
                .append(formatter.formatFloat(x2, false, precision))
                .append(formatter.formatFloat(y2, true, precision))
                .append(formatter.formatFloat(x, true, precision))
                .append(formatter.formatFloat(y, true, precision));
    }

    @Override
    public void curvetoCubicSmoothRel(final float x2, final float y2,
            final float x, final float y) {
        positionX += x;
        positionY += y;
        result.append('s')
                .append(formatter.formatFloat(x2, false, precision))
                .append(formatter.formatFloat(y2, true, precision))
                .append(formatter.formatFloat(x, true, precision))
                .append(formatter.formatFloat(y, true, precision));
        // TODO: optimize
    }

    @Override
    public void curvetoQuadraticAbs(final float x1, final float y1,
            final float x, final float y)  {
        positionX = x;
        positionY = y;
        result.append('Q')
                .append(formatter.formatFloat(x1, false, precision))
                .append(formatter.formatFloat(y1, true, precision))
                .append(formatter.formatFloat(x, true, precision))
                .append(formatter.formatFloat(y, true, precision));
    }

    @Override
    public void curvetoQuadraticRel(final float x1, final float y1,
            final float x, final float y) {
        final String stringX1 = formatter.formatFloat(x1, false, precision);
        final String stringY1 = formatter.formatFloat(y1, true, precision);
        if ("0".equals(stringX1) && " 0".equals(stringY1)) {
            linetoRel(x, y);
        }
        positionX += x;
        positionY += y;
        result.append('q').append(stringX1).append(stringY1)
                .append(formatter.formatFloat(x, true, precision))
                .append(formatter.formatFloat(y, true, precision));
        // TODO: optimize
    }

    @Override
    public void curvetoQuadraticSmoothAbs(final float x, final float y) {
        positionX = x;
        positionY = y;
        result.append('T')
                .append(formatter.formatFloat(x, false, precision))
                .append(formatter.formatFloat(y, true, precision));
    }

    @Override
    public void curvetoQuadraticSmoothRel(final float x, final float y) {
        positionX += x;
        positionY += y;
        result.append('t')
                .append(formatter.formatFloat(x, false, precision))
                .append(formatter.formatFloat(y, true, precision));
        // TODO: optimize
    }

    @Override
    public void endPath() {
        // empty
    }

    @Override
    public void linetoAbs(final float x, final float y) {
        positionX = x;
        positionY = y;
        result.append('L');
        result.append(formatter.formatFloat(x, false, precision));
        result.append(formatter.formatFloat(y, true, precision));
    }

    @Override
    public void linetoHorizontalAbs(final float x) {
        positionX = x;
        result.append('H')
                .append(formatter.formatFloat(x, false, precision));
    }

    @Override
    public void linetoHorizontalRel(final float x) {
        final String stringX = formatter.formatFloat(x, false, precision);
        if ("0".equals(stringX)) {
            return;
        }
        positionX += x;
        result.append('h').append(stringX);
    }

    @Override
    public void linetoRel(final float x, final float y) {
        final String stringX = formatter.formatFloat(x, false, precision);
        if ("0".equals(stringX)) {
            linetoVerticalRel(y);
            return;
        }
        final String stringY = formatter.formatFloat(y, true, precision);
        if (" 0".equals(stringY)) {
            linetoHorizontalRel(x);
            return;
        }
        positionX += x;
        positionY += y;
        result.append('l').append(stringX).append(stringY);
    }

    @Override
    public void linetoVerticalAbs(final float y) {
        positionY = y;
        result.append('V').append(formatter.formatFloat(y, false, precision));
    }

    @Override
    public void linetoVerticalRel(final float y) {
        final String stringY = formatter.formatFloat(y, false, precision);
        if ("0".equals(stringY)) {
            return;
        }
        positionY += y;
        result.append('v').append(stringY);
    }


// (1 - t) * P0 + t * P1 = (1 - t)^2 * P0 + 2 * (1 - t) * t * P1 + t^2 * P2

// P0 + P1 = 2 * PH

// B1(t) = (1 - t)  *P0                                               + t  *P1
// B2(t) = (1 - t)^2*P0 + 2*(1 - t)   * t*P1                          + t^2*P2
// B3(t) = (1 - t)^3*P0 + 3*(1 - t)^2 * t*P1 + 3 * (1 - t) * t^2 * P2 + t^3*P3

    @Override
    public void movetoAbs(final float x, final float y) {
        positionX = x;
        positionY = y;
        result.append('M')
                .append(formatter.formatFloat(x, false, precision))
                .append(formatter.formatFloat(y, true, precision));
    }

    @Override
    public void movetoRel(final float x, final float y) {
        positionX += x;
        positionY += y;
        result.append('m')
                .append(formatter.formatFloat(x, false, precision))
                .append(formatter.formatFloat(y, true, precision));
    }

    @Override
    public void startPath() {
        // empty
    }

}
