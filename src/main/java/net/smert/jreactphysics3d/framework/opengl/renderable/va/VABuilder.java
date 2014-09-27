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
package net.smert.jreactphysics3d.framework.opengl.renderable.va;

import java.nio.ByteBuffer;
import java.util.List;
import net.smert.jreactphysics3d.framework.opengl.mesh.Mesh;
import net.smert.jreactphysics3d.framework.opengl.renderable.shared.AbstractDrawCall;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class VABuilder extends net.smert.jreactphysics3d.framework.opengl.renderable.shared.Builder {

    public AbstractDrawCall createDrawCall(Mesh mesh, ByteBuffer vertexIndexBuffer) {
        AbstractDrawCall drawCall;

        int totalSegments = mesh.getTotalSegments();

        if (mesh.hasIndexes()) {

            assert (vertexIndexBuffer != null);

            // Create concrete class and set specific data
            DrawElements drawElements = new DrawElements();
            drawElements.setVertexIndexBuffer(vertexIndexBuffer);

            // Make sure we set the abstract class
            drawCall = drawElements;
        } else {

            // Convert first indexes
            int[] firstElements = new int[totalSegments];
            List<Integer> firstIndexes = mesh.getFirstIndexes();
            for (int i = 0, max = firstElements.length; i < max; i++) {
                firstElements[i] = firstIndexes.get(i);
            }

            // Create concrete class and set specific data
            DrawArrays drawArrays = new DrawArrays();
            drawArrays.setFirstElements(firstElements);

            // Make sure we set the abstract class
            drawCall = drawArrays;
        }

        // Convert element counts from each segment
        int[] elementCounts = new int[totalSegments];
        for (int i = 0, max = elementCounts.length; i < max; i++) {
            elementCounts[i] = mesh.getSegment(i).getVertices().size();
        }

        // Convert primitive modes from each segment
        int[] primitiveModes = new int[totalSegments];
        for (int i = 0, max = primitiveModes.length; i < max; i++) {
            primitiveModes[i] = mesh.getSegment(i).getPrimitiveMode();
        }

        drawCall.setElementCounts(elementCounts);
        drawCall.setPrimitiveModes(primitiveModes);

        return drawCall;
    }

}
