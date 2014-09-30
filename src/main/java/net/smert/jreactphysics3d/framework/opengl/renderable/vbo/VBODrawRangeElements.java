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
package net.smert.jreactphysics3d.framework.opengl.renderable.vbo;

import net.smert.jreactphysics3d.framework.opengl.GL;
import net.smert.jreactphysics3d.framework.opengl.renderable.factory.Renderable;
import net.smert.jreactphysics3d.framework.opengl.renderable.shared.AbstractDrawCall;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class VBODrawRangeElements extends AbstractDrawCall {

    private int[] maxIndexes;
    private int[] minIndexes;

    public int[] getMaxIndexes() {
        return maxIndexes;
    }

    public void setMaxIndexes(int[] maxIndexes) {
        this.maxIndexes = maxIndexes;
    }

    public int[] getMinIndexes() {
        return minIndexes;
    }

    public void setMinIndexes(int[] minIndexes) {
        this.minIndexes = minIndexes;
    }

    @Override
    public void render() {
        for (int i = 0; i < primitiveModes.length; i++) {
            GL.vboHelper.drawRangeElements(
                    primitiveModes[i], minIndexes[i], maxIndexes[i], elementCounts[i], Renderable.config.getIndexType());
        }
    }

}
