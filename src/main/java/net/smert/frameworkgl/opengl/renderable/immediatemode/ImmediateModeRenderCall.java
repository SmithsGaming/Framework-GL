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
package net.smert.frameworkgl.opengl.renderable.immediatemode;

import net.smert.frameworkgl.opengl.mesh.Mesh;
import net.smert.frameworkgl.opengl.renderable.Renderable;
import net.smert.frameworkgl.opengl.renderable.shared.AbstractRenderCall;
import net.smert.frameworkgl.opengl.renderable.shared.DrawCommands;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class ImmediateModeRenderCall extends AbstractRenderCall {

    private DrawCommands[] drawCommands;
    private Mesh mesh;

    public DrawCommands[] getDrawCommands() {
        return drawCommands;
    }

    public void setDrawCommands(DrawCommands[] drawCommands) {
        this.drawCommands = drawCommands;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void render() {
        for (int i = 0; i < drawCommands.length; i++) {
            DrawCommands drawCommand = drawCommands[i];
            Renderable.renderCallBindState.bind(uniqueShaderIDs[i], textureTypeMappings[i]);
            drawCommand.execCommands(mesh);
        }
    }

}
