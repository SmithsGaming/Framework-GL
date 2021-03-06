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
package net.smert.frameworkgl.opengl.renderable.gl2;

import net.smert.frameworkgl.opengl.mesh.Mesh;
import net.smert.frameworkgl.opengl.renderable.shared.DynamicArrayRenderable;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class DynamicVertexArrayGL2Renderable extends VertexArrayGL2Renderable implements DynamicArrayRenderable {

    @Override
    public void update(Mesh mesh) {
        updateGL1AndGL2(mesh);
    }

}
