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
package net.smert.jreactphysics3d.framework.opengl.renderable.gl1;

import net.smert.jreactphysics3d.framework.opengl.GL;
import net.smert.jreactphysics3d.framework.opengl.VertexBufferObject;
import net.smert.jreactphysics3d.framework.opengl.constants.VertexBufferObjectTypes;
import net.smert.jreactphysics3d.framework.opengl.mesh.Mesh;
import net.smert.jreactphysics3d.framework.opengl.renderable.AbstractRenderable;
import net.smert.jreactphysics3d.framework.opengl.renderable.Renderable;
import net.smert.jreactphysics3d.framework.opengl.renderable.RenderableConfiguration;
import net.smert.jreactphysics3d.framework.opengl.renderable.shared.AbstractDrawCall;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class VertexBufferObjectRenderable extends AbstractRenderable {

    private final static int VBO_COLOR = 0;
    private final static int VBO_NORMAL = 1;
    private final static int VBO_TEXCOORD = 2;
    private final static int VBO_VERTEX = 3;
    private final static int VBO_VERTEX_INDEX = 4;

    private int renderableConfigID;
    private AbstractDrawCall drawCall;
    private final VertexBufferObject[] vbos;

    public VertexBufferObjectRenderable() {
        renderableConfigID = -1;
        drawCall = null;
        vbos = new VertexBufferObject[5];
    }

    @Override
    public void create(Mesh mesh) {

        // Get configuration
        renderableConfigID = mesh.getRenderableConfigID();
        RenderableConfiguration config = Renderable.configPool.get(renderableConfigID);

        // Destroy existing VBOs
        destroy();

        Renderable.byteBuffers.reset();

        // Create VBOs
        if ((mesh.hasColors()) || (mesh.hasNormals()) || (mesh.hasTexCoords()) || (mesh.hasVertices())) {
            Renderable.vboBuilder.createNonInterleavedBufferData(mesh, Renderable.byteBuffers, config);
        }

        // Send byte buffer data for colors
        if (mesh.hasColors()) {
            vbos[VBO_COLOR] = GL.glf.createVertexBufferObject();
            VertexBufferObject vboColor = vbos[VBO_COLOR];
            vboColor.create();
            GL.vboHelper.setBufferData(vboColor.getVboID(), Renderable.byteBuffers.getColor(),
                    VertexBufferObjectTypes.STATIC_DRAW);
        }

        // Send byte buffer data for normals
        if (mesh.hasNormals()) {
            vbos[VBO_NORMAL] = GL.glf.createVertexBufferObject();
            VertexBufferObject vboNormal = vbos[VBO_NORMAL];
            vboNormal.create();
            GL.vboHelper.setBufferData(vboNormal.getVboID(), Renderable.byteBuffers.getNormal(),
                    VertexBufferObjectTypes.STATIC_DRAW);
        }

        // Send byte buffer data for texture coordinates
        if (mesh.hasTexCoords()) {
            vbos[VBO_TEXCOORD] = GL.glf.createVertexBufferObject();
            VertexBufferObject vboTexCoord = vbos[VBO_TEXCOORD];
            vboTexCoord.create();
            GL.vboHelper.setBufferData(vboTexCoord.getVboID(), Renderable.byteBuffers.getTexCoord(),
                    VertexBufferObjectTypes.STATIC_DRAW);
        }

        // Send byte buffer data for vertices
        if (mesh.hasVertices()) {
            vbos[VBO_VERTEX] = GL.glf.createVertexBufferObject();
            VertexBufferObject vboVertex = vbos[VBO_VERTEX];
            vboVertex.create();
            GL.vboHelper.setBufferData(vboVertex.getVboID(), Renderable.byteBuffers.getVertex(),
                    VertexBufferObjectTypes.STATIC_DRAW);
        }

        // Create VBO for indexes
        if (mesh.hasIndexes()) {
            vbos[VBO_VERTEX_INDEX] = GL.glf.createVertexBufferObject();
            VertexBufferObject vboVertexIndex = vbos[VBO_VERTEX_INDEX];
            vboVertexIndex.create();
            Renderable.vboBuilder.createIndexBufferData(mesh, Renderable.byteBuffers, config);
            GL.vboHelper.setBufferElementData(vboVertexIndex.getVboID(), Renderable.byteBuffers.getVertexIndex(),
                    VertexBufferObjectTypes.STATIC_DRAW);
        }

        GL.vboHelper.unbind();

        // Create draw call
        drawCall = Renderable.vboBuilder.createDrawCall(mesh, config);
    }

    @Override
    public void destroy() {

        VertexBufferObject vboColor = vbos[VBO_COLOR];
        VertexBufferObject vboNormal = vbos[VBO_NORMAL];
        VertexBufferObject vboTexCoord = vbos[VBO_TEXCOORD];
        VertexBufferObject vboVertex = vbos[VBO_VERTEX];
        VertexBufferObject vboVertexIndex = vbos[VBO_VERTEX_INDEX];

        if (vboColor != null) {
            vboColor.destroy();
        }
        if (vboNormal != null) {
            vboNormal.destroy();
        }
        if (vboTexCoord != null) {
            vboTexCoord.destroy();
        }
        if (vboVertex != null) {
            vboVertex.destroy();
        }
        if (vboVertexIndex != null) {
            vboVertexIndex.destroy();
        }
        for (int i = 0; i < vbos.length; i++) {
            vbos[i] = null;
        }
    }

    @Override
    public void render() {

        VertexBufferObject vboColor = vbos[VBO_COLOR];
        VertexBufferObject vboNormal = vbos[VBO_NORMAL];
        VertexBufferObject vboTexCoord = vbos[VBO_TEXCOORD];
        VertexBufferObject vboVertex = vbos[VBO_VERTEX];
        VertexBufferObject vboVertexIndex = vbos[VBO_VERTEX_INDEX];

        // Switch the renderable configuration first
        Renderable.vboBindState.switchRenderableConfiguration(renderableConfigID);

        // Bind each VBO
        if (vboColor != null) {
            Renderable.vboBindState.bindColor(vboColor.getVboID(), 0, 0);
        }
        if (vboNormal != null) {
            Renderable.vboBindState.bindNormal(vboNormal.getVboID(), 0, 0);
        }
        if (vboTexCoord != null) {
            Renderable.vboBindState.bindTextureCoordinate(vboTexCoord.getVboID(), 0, 0);
        }
        if (vboVertex != null) {
            Renderable.vboBindState.bindVertex(vboVertex.getVboID(), 0, 0);
        }
        if (vboVertexIndex != null) {
            Renderable.vboBindState.bindVertexIndex(vboVertexIndex.getVboID());
        }

        drawCall.render();
    }

}
