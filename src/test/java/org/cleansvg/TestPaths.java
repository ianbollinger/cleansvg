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

import org.cleansvg.Paths;
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

/*
    describe "transform" do
      it "should matrix" do
        tidy { |svg|
          svg.g :transform => "matrix(0 1 1 0 1 2)" do
            svg.path :d => "M10,20"
          end
        }.elements['g/path/@d'].value.should == "M21,12"
      end

      it "should translate" do
        tidy { |svg|
          svg.g :transform => "translate(-5 +5)" do
            svg.path :d => "M10,20"
          end
        }.elements['g/path/@d'].value.should == "M5,25"
      end

      it "should scale" do
        tidy { |svg|
          svg.g :transform => "scale(2)" do
            svg.path :d => "M10,20"
          end
        }.elements['g/path/@d'].value.should == "M20,40"
      end

      it "should stretch" do
        tidy { |svg|
          svg.g :transform => "scale(2,3)" do
            svg.path :d => "M10,20"
          end
        }.elements['g/path/@d'].value.should == "M20,60"
      end

      it "should rotate" do
        tidy { |svg|
          svg.g :transform => "rotate(30)" do
            svg.path :d => "M-50,0h100"
          end
        }.elements['g/path/@d'].value.should == "M-43-25l86,50"
      end

      it "should compose" do
        tidy { |svg|
          svg.g :transform => "translate(-10,-10) scale(2)" do
            svg.path :d => "M10,20"
          end
        }.elements['g/path/@d'].value.should == "M10,30"
      end

      it "should nest" do
        tidy { |svg|
          svg.g :transform => "translate(-10,-10)" do
            svg.g :transform => "scale(2)" do
              svg.path :d => "M10,20"
            end
          end
        }.elements['g/g/path/@d'].value.should == "M10,30"
      end

      it "should curve" do
        tidy { |svg|
          svg.g :transform => "scale(2)" do
            svg.path :d => "M0,0c10,15,20,30,40,40"
          end
        }.elements['g/path/@d'].value.should == "M0,0c20,30,40,60,80,80"
      end
    end

      it "should translate" do
        tidy(:size=>100, :grid=>true) { |svg|
          svg.svg :viewBox => '-50 -50 100 100' do
            svg.path :d => "M10,20"
          end
        }.elements['path/@d'].value.should == "M60,70"
      end

      it "should scale" do
        tidy(:size=>100, :grid=>true) { |svg|
          svg.svg :viewBox => '0 0 50 50' do
            svg.path :d => "M10,20"
          end
        }.elements['path/@d'].value.should == "M20,40"
      end
    end

    describe "polygon" do
      it "should become path" do
        tidy { |svg|
          svg.polygon :points => "10,20 20,30 20,40 10,50"
        }.elements['path/@d'].value.should == "M10,20l10,10v10l-10,10z"
      end
    end
*/
}
