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
import net.smert.jreactphysics3d.framework.opengl.renderable.factory.Configuration;
import net.smert.jreactphysics3d.framework.opengl.renderable.shared.AbstractDrawCall;
import net.smert.jreactphysics3d.framework.opengl.renderable.vbo.VBOBindState;
import net.smert.jreactphysics3d.framework.opengl.renderable.vbo.VBOBuilder;
import net.smert.jreactphysics3d.framework.opengl.renderable.vbo.ByteBuffers;

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

    private static Configuration renderableConfig;
    private static VBOBindState vboBindState;
    private static VBOBuilder vboBuilder;
    private AbstractDrawCall drawCall;
    private final VertexBufferObject[] vbos;

    public VertexBufferObjectRenderable(Mesh mesh) {
        super(mesh);
        vbos = new VertexBufferObject[5];
    }

    @Override
    public void create() {

        // Destroy existing VBOs
        destroy();

        ByteBuffers byteBuffers = new ByteBuffers();

        // Create VBOs
        if ((mesh.hasColors()) || (mesh.hasNormals()) || (mesh.hasTexCoords()) || (mesh.hasVertices())) {
            vboBuilder.createNonInterleavedBufferData(mesh, renderableConfig, byteBuffers);
        }

        // Send byte buffer data for colors
        if (mesh.hasColors()) {
            vbos[VBO_COLOR] = new VertexBufferObject();
            VertexBufferObject vboColor = vbos[VBO_COLOR];
            vboColor.create();
            GL.vboHelper.setBufferData(
                    vboColor.getVboID(), byteBuffers.getColor(), VertexBufferObjectTypes.STATIC_DRAW);
        }

        // Send byte buffer data for normals
        if (mesh.hasNormals()) {
            vbos[VBO_NORMAL] = new VertexBufferObject();
            VertexBufferObject vboNormal = vbos[VBO_NORMAL];
            vboNormal.create();
            GL.vboHelper.setBufferData(
                    vboNormal.getVboID(), byteBuffers.getNormal(), VertexBufferObjectTypes.STATIC_DRAW);
        }

        // Send byte buffer data for texture coordinates
        if (mesh.hasTexCoords()) {
            vbos[VBO_TEXCOORD] = new VertexBufferObject();
            VertexBufferObject vboTexCoord = vbos[VBO_TEXCOORD];
            vboTexCoord.create();
            GL.vboHelper.setBufferData(
                    vboTexCoord.getVboID(), byteBuffers.getTexCoord(), VertexBufferObjectTypes.STATIC_DRAW);
        }

        // Send byte buffer data for vertices
        if (mesh.hasVertices()) {
            vbos[VBO_VERTEX] = new VertexBufferObject();
            VertexBufferObject vboVertex = vbos[VBO_VERTEX];
            vboVertex.create();
            GL.vboHelper.setBufferData(
                    vboVertex.getVboID(), byteBuffers.getVertex(), VertexBufferObjectTypes.STATIC_DRAW);
        }

        // Create VBO for indexes
        if (mesh.hasIndexes()) {
            vbos[VBO_VERTEX_INDEX] = new VertexBufferObject();
            VertexBufferObject vboVertexIndex = vbos[VBO_VERTEX_INDEX];
            vboVertexIndex.create();
            vboBuilder.createIndexBufferData(mesh, renderableConfig, byteBuffers);
            GL.vboHelper.setBufferElementData(
                    vboVertexIndex.getVboID(), byteBuffers.getVertexIndex(), VertexBufferObjectTypes.STATIC_DRAW);
        }

        GL.vboHelper.unbind();

        // Create draw call
        drawCall = vboBuilder.createDrawCall(mesh);
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

        // Bind each VBO
        if (vboColor != null) {
            vboBindState.bindColor(vboColor.getVboID(), 0, 0);
        }
        if (vboNormal != null) {
            vboBindState.bindNormal(vboNormal.getVboID(), 0, 0);
        }
        if (vboTexCoord != null) {
            vboBindState.bindTextureCoordinate(vboTexCoord.getVboID(), 0, 0);
        }
        if (vboVertex != null) {
            vboBindState.bindVertex(vboVertex.getVboID(), 0, 0);
        }
        if (vboVertexIndex != null) {
            vboBindState.bindVertexIndex(vboVertexIndex.getVboID());
        }

        drawCall.render();
    }

    public static void SetRenderableConfiguration(Configuration renderableConfig) {
        VertexBufferObjectRenderable.renderableConfig = renderableConfig;
        if (renderableConfig.isImmutable() == false) {
            throw new RuntimeException("Renderable configuration must be made immutable");
        }
    }

    public static void SetVboBindState(VBOBindState vboBindState) {
        VertexBufferObjectRenderable.vboBindState = vboBindState;
    }

    public static void SetVboBuilder(VBOBuilder vboBuilder) {
        VertexBufferObjectRenderable.vboBuilder = vboBuilder;
    }

}
