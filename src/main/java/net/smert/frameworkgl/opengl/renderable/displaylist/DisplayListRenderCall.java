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
package net.smert.frameworkgl.opengl.renderable.displaylist;

import net.smert.frameworkgl.opengl.GL;
import net.smert.frameworkgl.opengl.mesh.Segment;
import net.smert.frameworkgl.opengl.renderable.Renderable;
import net.smert.frameworkgl.opengl.renderable.shared.AbstractRenderCall;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class DisplayListRenderCall extends AbstractRenderCall {

    private int[] displayListIDs;

    public int[] getDisplayListIDs() {
        return displayListIDs;
    }

    public void setDisplayListIDs(int[] displayListIDs) {
        this.displayListIDs = displayListIDs;
    }

    @Override
    public void render() {
        for (int i = 0; i < segments.length; i++) {
            Segment segment = segments[i];
            Renderable.colorState.changeColor(segment);
            Renderable.textureBindState.bindTextures(segment);
            Renderable.shaderBindState.sendUniformsOncePerRenderCall(segment);
            int displayListID = displayListIDs[i];
            GL.displayListHelper.call(displayListID);
        }
    }

}
