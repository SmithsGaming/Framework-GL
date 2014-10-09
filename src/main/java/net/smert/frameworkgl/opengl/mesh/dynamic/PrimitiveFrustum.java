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

import net.smert.frameworkgl.math.Vector3f;
import net.smert.frameworkgl.opengl.GL;
import net.smert.frameworkgl.opengl.constants.Primitives;
import net.smert.frameworkgl.utils.Color;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class PrimitiveFrustum extends AbstractDynamicMesh {

    @Override
    public void create(boolean reset, ConstructionInfo constructionInfo) {
        float halfBottomX = constructionInfo.size.getX() * .5f;
        float halfBottomZ = constructionInfo.size.getZ() * .5f;
        float halfY = constructionInfo.size.getY() * .5f;
        float halfTopX = halfBottomX * constructionInfo.radius.getX();
        float halfTopZ = halfBottomZ * constructionInfo.radius.getZ();
        final Color color0 = constructionInfo.getColor(0);

        // Reset
        if (reset == true) {
            GL.tessellator.setConvertToTriangles(constructionInfo.convertToTriangles);
            GL.tessellator.reset();
        }
        GL.tessellator.setLocalPosition(constructionInfo.localPosition);

        GL.tessellator.start(Primitives.QUADS);

        // Face +Z
        final Vector3f pos1 = new Vector3f(halfTopX, halfY, halfTopZ);
        final Vector3f pos2 = new Vector3f(-halfTopX, halfY, halfTopZ);
        final Vector3f pos3 = new Vector3f(-halfBottomX, -halfY, halfBottomZ);
        final Vector3f pos4 = new Vector3f(halfBottomX, -halfY, halfBottomZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormal(pos1, pos2, pos3);
        GL.tessellator.addVertex(pos1);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos2);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos3);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos4);

        // Face +X
        pos1.set(halfTopX, halfY, -halfTopZ);
        pos2.set(halfTopX, halfY, halfTopZ);
        pos3.set(halfBottomX, -halfY, halfBottomZ);
        pos4.set(halfBottomX, -halfY, -halfBottomZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormal(pos1, pos2, pos3);
        GL.tessellator.addVertex(pos1);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos2);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos3);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos4);

        // Face -Z
        pos1.set(-halfTopX, halfY, -halfTopZ);
        pos2.set(halfTopX, halfY, -halfTopZ);
        pos3.set(halfBottomX, -halfY, -halfBottomZ);
        pos4.set(-halfBottomX, -halfY, -halfBottomZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormal(pos1, pos2, pos3);
        GL.tessellator.addVertex(pos1);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos2);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos3);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos4);

        // Face -X
        pos1.set(-halfTopX, halfY, halfTopZ);
        pos2.set(-halfTopX, halfY, -halfTopZ);
        pos3.set(-halfBottomX, -halfY, -halfBottomZ);
        pos4.set(-halfBottomX, -halfY, halfBottomZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormal(pos1, pos2, pos3);
        GL.tessellator.addVertex(pos1);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos2);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos3);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(pos4);

        // Face +Y
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormal(0f, 1f, 0f);
        GL.tessellator.addVertex(halfTopX, halfY, -halfTopZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(-halfTopX, halfY, -halfTopZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(-halfTopX, halfY, halfTopZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(halfTopX, halfY, halfTopZ);

        // Face -Y
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormal(0f, -1f, 0f);
        GL.tessellator.addVertex(halfBottomX, -halfY, halfBottomZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(-halfBottomX, -halfY, halfBottomZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(-halfBottomX, -halfY, -halfBottomZ);
        GL.tessellator.addColor(color0);
        GL.tessellator.addNormalAgain();
        GL.tessellator.addVertex(halfBottomX, -halfY, -halfBottomZ);

        GL.tessellator.stop();
        GL.tessellator.addSegment("Primitive Frustum");
    }

}
