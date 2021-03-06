/**
 * Copyright 2014 Jason Sorensen (sorensenj@smert.net)
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
package net.smert.frameworkgl.examples.gl1vertexbufferobjectinterleaved;

import net.smert.frameworkgl.Fw;
import net.smert.frameworkgl.Screen;
import net.smert.frameworkgl.examples.common.CubeMeshForQuads;
import net.smert.frameworkgl.examples.common.CubeMeshForQuadsWithPerVertexColors;
import net.smert.frameworkgl.examples.common.CubeMeshForTriangles;
import net.smert.frameworkgl.helpers.Keyboard;
import net.smert.frameworkgl.opengl.GL;
import net.smert.frameworkgl.opengl.camera.LegacyCamera;
import net.smert.frameworkgl.opengl.camera.LegacyCameraController;
import net.smert.frameworkgl.opengl.constants.GetString;
import net.smert.frameworkgl.opengl.mesh.Mesh;
import net.smert.frameworkgl.opengl.renderable.AbstractRenderable;
import net.smert.frameworkgl.utils.FpsTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class VertexBufferObjectInterleaved extends Screen {

    private final static Logger log = LoggerFactory.getLogger(VertexBufferObjectInterleaved.class);

    private AbstractRenderable renderableQuads;
    private AbstractRenderable renderableQuadsWithPerVertexColors;
    private AbstractRenderable renderableTriangles;
    private FpsTimer fpsTimer;
    private LegacyCamera camera;
    private LegacyCameraController cameraController;
    private Mesh meshQuads;
    private Mesh meshQuadsWithPerVertexColors;
    private Mesh meshTriangles;

    public VertexBufferObjectInterleaved(String[] args) {
    }

    private void handleInput() {
        if (Fw.input.isKeyDown(Keyboard.ESCAPE)) {
            Fw.app.stopRunning();
        }
        cameraController.update();
    }

    @Override
    public void destroy() {
        renderableQuads.destroy();
        renderableQuadsWithPerVertexColors.destroy();
        renderableTriangles.destroy();
        Fw.input.removeInputProcessor(cameraController);
        Fw.input.releaseMouseCursor();
    }

    @Override
    public void init() {

        // Create timer
        fpsTimer = new FpsTimer();

        // Setup camera and controller
        camera = GL.cameraFactory.createLegacyCamera();
        camera.setPerspectiveProjection(
                70f,
                (float) Fw.config.getCurrentWidth() / (float) Fw.config.getCurrentHeight(),
                .05f, 128f);
        camera.setPosition(0f, 0f, 5f);
        cameraController = GL.cameraFactory.createLegacyCameraController();
        cameraController.setCamera(camera);

        // Create meshes
        meshQuads = new CubeMeshForQuads();
        meshQuadsWithPerVertexColors = new CubeMeshForQuadsWithPerVertexColors();
        meshTriangles = new CubeMeshForTriangles();

        // Create vertex buffer object interleaved renderables
        renderableQuads = Fw.graphics.createInterleavedRenderable();
        renderableQuads.create(meshQuads);
        renderableQuadsWithPerVertexColors = Fw.graphics.createInterleavedRenderable();
        renderableQuadsWithPerVertexColors.create(meshQuadsWithPerVertexColors);
        renderableTriangles = Fw.graphics.createInterleavedRenderable();
        renderableTriangles.create(meshTriangles);

        // OpenGL settings
        GL.o1.enableCulling();
        GL.o1.cullBackFaces();
        GL.o1.enableDepthTest();
        GL.o1.setDepthFuncLess();
        GL.o1.enableDepthMask();
        GL.o1.setClearDepth(1f);
        GL.o1.clear();

        log.info("OpenGL version: " + GL.o1.getString(GetString.VERSION));

        // Add camera controller to input
        Fw.input.addInputProcessor(cameraController);
        Fw.input.grabMouseCursor();
    }

    @Override
    public void pause() {
    }

    @Override
    public void render() {
        fpsTimer.update();

        if (Fw.timer.isGameTick()) {
            // Do nothing
        }

        if (Fw.timer.isRenderTick()) {
            handleInput();

            // Clear screen
            GL.o1.clear();

            // Update camera
            camera.updateOpenGL();

            // Render directly
            Fw.graphics.render(renderableTriangles, -2f, 0f, 0f);
            Fw.graphics.render(renderableQuads, 2f, 0f, 0f);
            Fw.graphics.render(renderableQuadsWithPerVertexColors, 0f, 2f, 0f);
        }
    }

    @Override
    public void resize(int width, int height) {
        GL.o1.setViewport(0, 0, width, height);
    }

    @Override
    public void resume() {
    }

}
