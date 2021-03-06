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
package net.smert.frameworkgl.opengl.renderer;

import java.nio.FloatBuffer;
import java.util.List;
import net.smert.frameworkgl.Fw;
import net.smert.frameworkgl.gameobjects.GameObject;
import net.smert.frameworkgl.math.Transform4f;
import net.smert.frameworkgl.math.Vector2f;
import net.smert.frameworkgl.math.Vector3f;
import net.smert.frameworkgl.opengl.GL;
import net.smert.frameworkgl.opengl.camera.Camera;
import net.smert.frameworkgl.opengl.renderable.AbstractRenderable;
import net.smert.frameworkgl.opengl.renderable.gl1.DisplayListGL1Renderable;
import net.smert.frameworkgl.opengl.renderable.gl1.DynamicVertexBufferObjectInterleavedGL1Renderable;
import net.smert.frameworkgl.opengl.renderable.gl1.DynamicVertexBufferObjectNonInterleavedGL1Renderable;
import net.smert.frameworkgl.opengl.renderable.gl1.ImmediateModeGL1Renderable;
import net.smert.frameworkgl.opengl.renderable.gl1.VertexArrayGL1Renderable;
import net.smert.frameworkgl.opengl.renderable.gl1.VertexBufferObjectInterleavedGL1Renderable;
import net.smert.frameworkgl.opengl.renderable.gl1.VertexBufferObjectNonInterleavedGL1Renderable;
import net.smert.frameworkgl.opengl.shader.AbstractShader;
import net.smert.frameworkgl.utils.Color;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class RendererGL1 extends AbstractRendererGL {

    private FloatBuffer modelMatrixFloatBuffer;
    private FloatBuffer projectionMatrixFloatBuffer;
    private FloatBuffer viewMatrixFloatBuffer;

    public RendererGL1() {
        super();
    }

    private void render(AbstractRenderable renderable, FloatBuffer modelMatrixFloatBuffer) {
        pushMatrix();
        GL.o1.multiplyMatrix(modelMatrixFloatBuffer);
        renderable.render();
        popMatrix();
    }

    public VertexArrayGL1Renderable createArrayRenderable() {
        return GL.rf1.createArrayRenderable();
    }

    public DisplayListGL1Renderable createDisplayListRenderable() {
        return GL.rf1.createDisplayListRenderable();
    }

    public DynamicVertexBufferObjectInterleavedGL1Renderable createDynamicInterleavedRenderable() {
        return GL.rf1.createDynamicInterleavedRenderable();
    }

    public DynamicVertexBufferObjectNonInterleavedGL1Renderable createDynamicNonInterleavedRenderable() {
        return GL.rf1.createDynamicNonInterleavedRenderable();
    }

    public ImmediateModeGL1Renderable createImmediateModeRenderable() {
        return GL.rf1.createImmediateModeRenderable();
    }

    public VertexBufferObjectInterleavedGL1Renderable createInterleavedRenderable() {
        return GL.rf1.createInterleavedRenderable();
    }

    public VertexBufferObjectNonInterleavedGL1Renderable createNonInterleavedRenderable() {
        return GL.rf1.createNonInterleavedRenderable();
    }

    public void destroy() {
    }

    public void init() {
        modelMatrixFloatBuffer = GL.bufferHelper.createFloatBuffer(16);
        projectionMatrixFloatBuffer = GL.bufferHelper.createFloatBuffer(16);
        viewMatrixFloatBuffer = GL.bufferHelper.createFloatBuffer(16);
    }

    @Override
    public void color(float r, float g, float b, float a) {
        GL.o1.color(r, g, b, a);
    }

    @Override
    public void disableTexture2D() {
        GL.o1.disableTexture2D();
    }

    @Override
    public void disableTexture3D() {
        GL.o1.disableTexture3D();
    }

    @Override
    public void disableTextureCubeMap() {
        GL.o1.disableTextureCubeMap();
    }

    @Override
    public void enableTexture2D() {
        GL.o1.enableTexture2D();
    }

    @Override
    public void enableTexture3D() {
        GL.o1.enableTexture3D();
    }

    @Override
    public void enableTextureCubeMap() {
        GL.o1.enableTextureCubeMap();
    }

    @Override
    public void popMatrix() {
        GL.o1.popMatrix();
    }

    @Override
    public void pushMatrix() {
        GL.o1.pushMatrix();
    }

    @Override
    public void render(AbstractRenderable renderable) {
        renderable.render();
    }

    @Override
    public void render(AbstractRenderable renderable, float x, float y, float z) {
        pushMatrix();
        translate(x, y, z);
        render(renderable);
        popMatrix();
    }

    @Override
    public void render(AbstractRenderable renderable, Transform4f transform) {
        transform.toFloatBuffer(modelMatrixFloatBuffer);
        modelMatrixFloatBuffer.flip();
        render(renderable, modelMatrixFloatBuffer);
    }

    @Override
    public void render(AbstractRenderable renderable, Vector3f position) {
        pushMatrix();
        translate(position);
        render(renderable);
        popMatrix();
    }

    @Override
    public void render(GameObject gameObject) {
        gameObject.getWorldTransform().toFloatBuffer(modelMatrixFloatBuffer);
        modelMatrixFloatBuffer.flip();
        render(gameObject.getRenderable(), modelMatrixFloatBuffer);
    }

    @Override
    public void render(List<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            render(gameObject);
        }
    }

    @Override
    public void renderBlend(GameObject gameObject) {
        if (gameObject.getRenderableState().isOpaque()) {
            return;
        }
        GL.o1.enableBlending();
        render(gameObject);
        GL.o1.disableBlending();
    }

    @Override
    public void renderBlend(List<GameObject> gameObjects) {
        GL.o1.enableBlending();
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getRenderableState().isOpaque()) {
                continue;
            }
            render(gameObject);
        }
        GL.o1.disableBlending();
    }

    @Override
    public void renderOpaque(GameObject gameObject) {
        if (!gameObject.getRenderableState().isOpaque()) {
            return;
        }
        render(gameObject);
    }

    @Override
    public void renderOpaque(List<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            if (!gameObject.getRenderableState().isOpaque()) {
                continue;
            }
            render(gameObject);
        }
    }

    @Override
    public void scale(float x, float y, float z) {
        GL.o1.scale(x, y, z);
    }

    @Override
    public void scale(Vector3f scaling) {
        GL.o1.scale(scaling.getX(), scaling.getY(), scaling.getZ());
    }

    @Override
    public void set2DMode() {
        GL.o1.setProjectionOrtho(0f, Fw.config.getCurrentWidth(), 0f, Fw.config.getCurrentHeight(), -1f, 1f);
        GL.o1.setModelViewIdentity();
    }

    @Override
    public void set2DMode(int width, int height) {
        GL.o1.setProjectionOrtho(0f, width, 0f, height, -1f, 1f);
        GL.o1.setModelViewIdentity();
    }

    @Override
    public void setCamera(Camera camera) {
        camera.update();
        camera.getProjectionMatrix().toFloatBuffer(projectionMatrixFloatBuffer);
        camera.getViewMatrix().toFloatBuffer(viewMatrixFloatBuffer);
        projectionMatrixFloatBuffer.flip();
        viewMatrixFloatBuffer.flip();
        GL.o1.switchProjection();
        GL.o1.loadMatrix(projectionMatrixFloatBuffer);
        GL.o1.switchModelView();
        GL.o1.loadMatrix(viewMatrixFloatBuffer);
    }

    @Override
    public void switchShader(AbstractShader shader) {
        throw new UnsupportedOperationException("Not supported by this renderer");
    }

    @Override
    public void translate(float x, float y, float z) {
        GL.o1.translate(x, y, z);
    }

    @Override
    public void translate(Vector3f position) {
        GL.o1.translate(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public void unbindShader() {
        throw new UnsupportedOperationException("Not supported by this renderer");
    }

    @Override
    public void colorText(Color color) {
        color(color.getR(), color.getG(), color.getB(), color.getA());
    }

    @Override
    public AbstractRenderable createGlyphRenderable() {
        return createInterleavedRenderable();
    }

    @Override
    public float getTextDefaultX() {
        return textDefaultX;
    }

    @Override
    public float getTextDefaultY() {
        return textDefaultY;
    }

    @Override
    public Vector2f getTextPosition() {
        return textPosition;
    }

    @Override
    public void renderGlyph(AbstractRenderable renderable) {
        render(renderable);
    }

    @Override
    public void scaleText(float x, float y) {
        scale(x, y, 1f);
    }

    @Override
    public void translateText(float x, float y) {
        translate(x, y, 0f);
    }

}
