/**
 * Copyright 2012 Jason Sorensen (sorensenj@smert.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.smert.frameworkgl.opengl.mesh.dynamic;

import net.smert.frameworkgl.opengl.constants.Primitives;
import net.smert.frameworkgl.opengl.mesh.Tessellator;
import net.smert.frameworkgl.utils.Color;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class SimpleOrientationAxis extends AbstractDynamicMesh {

    @Override
    public void create(boolean reset, ConstructionInfo constructionInfo, Tessellator tessellator) {
        final Color color0 = constructionInfo.getColor(0);
        final Color color1 = constructionInfo.getColor(1);
        final Color color2 = constructionInfo.getColor(2);

        // Reset
        if (reset) {
            tessellator.setConvertToTriangles(constructionInfo.convertToTriangles);
            tessellator.reset();
        }
        tessellator.setLocalPosition(constructionInfo.localPosition);

        tessellator.start(Primitives.LINES);

        tessellator.addColor(color0);
        tessellator.addVertex(0.0f, 0.0f, 0.0f);
        tessellator.addColor(color0);
        tessellator.addVertex(1.0f, 0.0f, 0.0f);

        tessellator.addColor(color1);
        tessellator.addVertex(0.0f, 0.0f, 0.0f);
        tessellator.addColor(color1);
        tessellator.addVertex(0.0f, 1.0f, 0.0f);

        tessellator.addColor(color2);
        tessellator.addVertex(0.0f, 0.0f, 0.0f);
        tessellator.addColor(color2);
        tessellator.addVertex(0.0f, 0.0f, 1.0f);

        tessellator.stop();
        tessellator.addSegment("Simple Orientation Axis");
    }

}
