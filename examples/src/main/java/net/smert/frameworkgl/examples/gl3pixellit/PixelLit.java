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
package net.smert.frameworkgl.examples.gl3pixellit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.smert.frameworkgl.Fw;
import net.smert.frameworkgl.Screen;
import net.smert.frameworkgl.examples.common.DynamicMeshWorld;
import net.smert.frameworkgl.examples.common.PixelLitGuiScreen;
import net.smert.frameworkgl.gameobjects.AABBGameObject;
import net.smert.frameworkgl.gameobjects.GameObject;
import net.smert.frameworkgl.gameobjects.SimpleOrientationAxisGameObject;
import net.smert.frameworkgl.gameobjects.SkyboxGameObject;
import net.smert.frameworkgl.gameobjects.ViewFrustumGameObject;
import net.smert.frameworkgl.helpers.Keyboard;
import net.smert.frameworkgl.math.AABB;
import net.smert.frameworkgl.math.Vector3f;
import net.smert.frameworkgl.math.Vector4f;
import net.smert.frameworkgl.opengl.GL;
import net.smert.frameworkgl.opengl.GLLight;
import net.smert.frameworkgl.opengl.camera.Camera;
import net.smert.frameworkgl.opengl.camera.CameraController;
import net.smert.frameworkgl.opengl.camera.FrustumCullingClipSpaceSymmetrical;
import net.smert.frameworkgl.opengl.constants.GetString;
import net.smert.frameworkgl.opengl.shader.AbstractShader;
import net.smert.frameworkgl.opengl.shader.basic.DiffuseTextureShader;
import net.smert.frameworkgl.opengl.shader.basic.SkyboxShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.BlinnPhongSpecularDirectionalShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.BlinnPhongSpecularPointShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.BlinnPhongSpecularSpotShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.BlinnPhongSpecularSpotTwoConeShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.DiffuseDirectionalShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.DiffusePointShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.DiffuseSpotShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.DiffuseSpotTwoConeShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.PhongSpecularDirectionalShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.PhongSpecularPointShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.PhongSpecularSpotShader;
import net.smert.frameworkgl.opengl.shader.pixellit.single.PhongSpecularSpotTwoConeShader;
import net.smert.frameworkgl.utils.FpsTimer;
import net.smert.frameworkgl.utils.MemoryUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class PixelLit extends Screen {

    private final static Logger log = LoggerFactory.getLogger(PixelLit.class);

    private boolean renderAabbs;
    private boolean renderSimpleOrientationAxis;
    private boolean wireframe;
    private AbstractShader currentShader;
    private AABBGameObject aabbGameObject;
    private BlinnPhongSpecularDirectionalShader pixelLitSingleBlinnPhongSpecularDirectionalShader;
    private BlinnPhongSpecularPointShader pixelLitSingleBlinnPhongSpecularPointShader;
    private BlinnPhongSpecularSpotShader pixelLitSingleBlinnPhongSpecularSpotShader;
    private BlinnPhongSpecularSpotTwoConeShader pixelLitSingleBlinnPhongSpecularSpotTwoConeShader;
    private Camera camera;
    private CameraController cameraController;
    private DiffuseDirectionalShader pixelLitSingleDiffuseDirectionalShader;
    private DiffusePointShader pixelLitSingleDiffusePointShader;
    private DiffuseSpotShader pixelLitSingleDiffuseSpotShader;
    private DiffuseSpotTwoConeShader pixelLitSingleDiffuseSpotTwoConeShader;
    private DiffuseTextureShader diffuseTextureShader;
    private DynamicMeshWorld dynamicMeshesWorld;
    private FpsTimer fpsTimer;
    private GLLight currentLight;
    private GLLight glLightDirectional;
    private GLLight glLightPoint;
    private GLLight glLightSpot;
    private final List<GameObject> gameObjectsToRender;
    private MemoryUsage memoryUsage;
    private PhongSpecularDirectionalShader pixelLitSinglePhongSpecularDirectionalShader;
    private PhongSpecularPointShader pixelLitSinglePhongSpecularPointShader;
    private PhongSpecularSpotShader pixelLitSinglePhongSpecularSpotShader;
    private PhongSpecularSpotTwoConeShader pixelLitSinglePhongSpecularSpotTwoConeShader;
    private PixelLitGuiScreen pixelLitGuiScreen;
    private SimpleOrientationAxisGameObject simpleOrientationAxisGameObject;
    private SkyboxGameObject skyboxGameObject;
    private SkyboxShader skyboxShader;
    private ViewFrustumGameObject viewFrustumGameObject;

    public PixelLit(String[] args) {
        renderAabbs = false;
        renderSimpleOrientationAxis = false;
        wireframe = false;
        gameObjectsToRender = new ArrayList<>();
    }

    private void handleInput() {
        if (Fw.input.isKeyDown(Keyboard.ESCAPE)) {
            Fw.app.stopRunning();
        }
        if (Fw.input.isKeyDown(Keyboard.F1) && !Fw.input.wasKeyDown(Keyboard.F1)) {
            wireframe = !wireframe;
            if (wireframe) {
                GL.o1.setPolygonModeFrontAndBackLine();
            } else {
                GL.o1.setPolygonModeFrontAndBackFill();
            }
        }
        if (Fw.input.isKeyDown(Keyboard.B) && !Fw.input.wasKeyDown(Keyboard.B)) {
            renderAabbs = !renderAabbs;
        }
        if (Fw.input.isKeyDown(Keyboard.O) && !Fw.input.wasKeyDown(Keyboard.O)) {
            renderSimpleOrientationAxis = !renderSimpleOrientationAxis;
        }
        if (Fw.input.isKeyDown(Keyboard.F)) {
            camera.updatePlanes();
            viewFrustumGameObject.getRenderableState().setInFrustum(true);
            viewFrustumGameObject.getWorldTransform().getRotation().set(camera.getRotationMatrix());
            viewFrustumGameObject.setWorldPosition(camera.getPosition());
            viewFrustumGameObject.update(camera.getAspectRatio(), camera.getFieldOfView(), camera.getZNear(),
                    camera.getZFar());
            Fw.graphics.updateAabb(viewFrustumGameObject);
            Fw.graphics.performCulling(camera, dynamicMeshesWorld.getGameObjects());
            updateGameObjectsToRender();
        }
        float spotInnerCutoff = pixelLitGuiScreen.getSpotInnerCutoff();
        float spotOuterCutoff = pixelLitGuiScreen.getSpotOuterCutoff();
        if (Fw.input.isKeyDown(Keyboard.C)) {
            if ((spotOuterCutoff == 90f) && !Fw.input.wasKeyDown(Keyboard.C)) {
                spotOuterCutoff = 180f;
            } else if ((spotOuterCutoff == 180f) && !Fw.input.wasKeyDown(Keyboard.C)) {
                spotOuterCutoff = 0f;
            } else if ((spotOuterCutoff != 90f) && (spotOuterCutoff != 180f)) {
                spotOuterCutoff += Fw.timer.getDelta() * 3f;
                if (spotOuterCutoff > 180f) {
                    spotOuterCutoff = 0f;
                }
                if (spotOuterCutoff > 90f) {
                    spotOuterCutoff = 90f;
                }
            }
            glLightSpot.setSpotOuterCutoff(spotOuterCutoff);
        }
        if (Fw.input.isKeyDown(Keyboard.V)) {
            if ((spotInnerCutoff == 90f) && !Fw.input.wasKeyDown(Keyboard.V)) {
                spotInnerCutoff = 180f;
            } else if ((spotInnerCutoff == 180f) && !Fw.input.wasKeyDown(Keyboard.V)) {
                spotInnerCutoff = 0f;
            } else if ((spotInnerCutoff != 90f) && (spotInnerCutoff != 180f)) {
                spotInnerCutoff += Fw.timer.getDelta() * 3f;
                if (spotInnerCutoff > 180f) {
                    spotInnerCutoff = 0f;
                }
                if (spotInnerCutoff > 90f) {
                    spotInnerCutoff = 90f;
                }
            }
            glLightSpot.setSpotInnerCutoff(spotInnerCutoff);
        }
        if (Fw.input.isKeyDown(Keyboard.LEFT_BRACKET) && !Fw.input.wasKeyDown(Keyboard.LEFT_BRACKET)) {
            pixelLitGuiScreen.decrementShaderIndex();
            updateCurrentShader();
        }
        if (Fw.input.isKeyDown(Keyboard.RIGHT_BRACKET) && !Fw.input.wasKeyDown(Keyboard.RIGHT_BRACKET)) {
            pixelLitGuiScreen.incrementShaderIndex();
            updateCurrentShader();
        }
        pixelLitGuiScreen.setSpotInnerCutoff(spotInnerCutoff);
        pixelLitGuiScreen.setSpotOuterCutoff(spotOuterCutoff);
        cameraController.update();
    }

    private void updateCurrentLight() {
        switch (pixelLitGuiScreen.getShaderIndex()) {
            case 0:
            case 4:
            case 8:
                currentLight = glLightDirectional;
                break;
            case 1:
            case 5:
            case 9:
                currentLight = glLightPoint;
                break;
            case 2:
            case 3:
            case 6:
            case 7:
            case 10:
            case 11:
                currentLight = glLightSpot;
                break;
        }
    }

    private void updateCurrentShader() {
        switch (pixelLitGuiScreen.getShaderIndex()) {
            case 0:
                currentShader = pixelLitSingleBlinnPhongSpecularDirectionalShader;
                break;
            case 1:
                currentShader = pixelLitSingleBlinnPhongSpecularPointShader;
                break;
            case 2:
                currentShader = pixelLitSingleBlinnPhongSpecularSpotShader;
                break;
            case 3:
                currentShader = pixelLitSingleBlinnPhongSpecularSpotTwoConeShader;
                break;
            case 4:
                currentShader = pixelLitSingleDiffuseDirectionalShader;
                break;
            case 5:
                currentShader = pixelLitSingleDiffusePointShader;
                break;
            case 6:
                currentShader = pixelLitSingleDiffuseSpotShader;
                break;
            case 7:
                currentShader = pixelLitSingleDiffuseSpotTwoConeShader;
                break;
            case 8:
                currentShader = pixelLitSinglePhongSpecularDirectionalShader;
                break;
            case 9:
                currentShader = pixelLitSinglePhongSpecularPointShader;
                break;
            case 10:
                currentShader = pixelLitSinglePhongSpecularSpotShader;
                break;
            case 11:
                currentShader = pixelLitSinglePhongSpecularSpotTwoConeShader;
                break;
        }
    }

    private void updateGameObjectsToRender() {
        gameObjectsToRender.clear();
        for (GameObject gameObject : dynamicMeshesWorld.getGameObjects()) {
            if (gameObject.getRenderableState().isInFrustum()) {
                gameObjectsToRender.add(gameObject);
            }
        }
    }

    @Override
    public void destroy() {
        for (GameObject gameObject : dynamicMeshesWorld.getGameObjects()) {
            gameObject.destroy();
        }
        viewFrustumGameObject.destroy();
        Fw.input.removeInputProcessor(cameraController);
        Fw.input.releaseMouseCursor();
    }

    @Override
    public void init() {

        // Register assets
        try {
            Fw.files.registerAssets("/net/smert/frameworkgl/examples/assets", true);
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

        // Switch renderer and factory to OpenGL 3
        Fw.graphics.switchOpenGLVersion(3);

        // Create timer
        fpsTimer = new FpsTimer();

        // Setup camera and controller
        camera = GL.cameraFactory.createCamera();
        camera.lookAt(new Vector3f(0f, 2f, 5f), new Vector3f(0f, 2f, -1f), Vector3f.WORLD_Y_AXIS);
        camera.setPerspectiveProjection(
                70f,
                (float) Fw.config.getCurrentWidth() / (float) Fw.config.getCurrentHeight(),
                .05f, 128f);
        cameraController = GL.cameraFactory.createCameraController();
        cameraController.setCamera(camera);

        // Memory usage
        memoryUsage = new MemoryUsage();

        // Create glLights and material light
        glLightDirectional = GL.glFactory.createGLLight();
        glLightDirectional.setPosition(new Vector4f(0f, 15f, 10f, 0f));
        glLightDirectional.setRadius(256f); // Shader uses this value and OpenGL does not
        glLightPoint = GL.glFactory.createGLLight();
        glLightPoint.setPosition(new Vector4f(0f, 15f, 10f, 1f));
        glLightPoint.setRadius(256f); // Shader uses this value and OpenGL does not
        glLightSpot = GL.glFactory.createGLLight();
        glLightSpot.setPosition(new Vector4f(0f, 15f, 10f, 1f));
        glLightSpot.setRadius(256f); // Shader uses this value and OpenGL does not
        glLightSpot.setSpotInnerCutoff(180f);
        glLightSpot.setSpotOuterCutoff(180f);
        glLightSpot.setSpotDirection(new Vector4f(0f, -15f, -10f, 1f));
        GL.uniformVariables.getDefaultMaterialLight().setShininess(16);
        // Effectively disables diffuse and per vertex color will be used
        GL.uniformVariables.getDefaultMaterialLight().setDiffuse(new Vector4f(1f, 1f, 1f, 1f));
        GL.uniformVariables.getDefaultMaterialLight().setSpecular(new Vector4f(.3f, .3f, .3f, 1f));

        // Load textures
        try {
            // Texture must be loaded before the renderable is created
            // Parameters ("skybox/grimmnight", "png") will create a texture named "skybox/grimmnight/cubemap.png"
            // from "skybox/grimmnight/xpos.png", "skybox/grimmnight/xneg.png", "skybox/grimmnight/ypos.png"
            // "skybox/grimmnight/yneg.png", "skybox/grimmnight/zpos.png" and "skybox/grimmnight/zneg.png"
            Fw.graphics.loadCubeMapTexture("skybox/grimmnight", "png");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // AABB game object
        aabbGameObject = new AABBGameObject();
        aabbGameObject.getColor0().set("yellow");
        aabbGameObject.init(new AABB()); // Empty AABB

        // Create dynamic mesh world
        dynamicMeshesWorld = new DynamicMeshWorld();
        dynamicMeshesWorld.init();

        // Simple axis game object
        simpleOrientationAxisGameObject = new SimpleOrientationAxisGameObject();
        simpleOrientationAxisGameObject.getColor0().set("red");
        simpleOrientationAxisGameObject.getColor1().set("green");
        simpleOrientationAxisGameObject.getColor2().set("blue");
        simpleOrientationAxisGameObject.init();

        // Skybox game object
        skyboxGameObject = new SkyboxGameObject();
        skyboxGameObject.init("skybox/grimmnight/cubemap.png");

        // View frustum game object
        viewFrustumGameObject = new ViewFrustumGameObject();
        viewFrustumGameObject.getColor0().set("black");
        viewFrustumGameObject.getColor1().set("yellow");
        viewFrustumGameObject.getColor2().set("yellow");
        viewFrustumGameObject.getColor3().set("white");
        viewFrustumGameObject.getColor3().setA(.4f);
        viewFrustumGameObject.getRenderableState().setInFrustum(false);
        viewFrustumGameObject.init(camera.getAspectRatio(), camera.getFieldOfView(), camera.getZNear(),
                camera.getZFar());

        // Frustum culling
        FrustumCullingClipSpaceSymmetrical frustumCulling = GL.cameraFactory.createFrustumCullingClipSpaceSymmetrical();
        camera.setFrustumCulling(frustumCulling);
        updateGameObjectsToRender();

        // Update AABBs
        Fw.graphics.updateAabb(dynamicMeshesWorld.getGameObjects());

        // Initialize GUI
        Fw.gui.init();

        // Create GUI screen
        pixelLitGuiScreen = new PixelLitGuiScreen();
        pixelLitGuiScreen.setSpotInnerCutoff(180f);
        pixelLitGuiScreen.setSpotOuterCutoff(180f);
        pixelLitGuiScreen.init(Fw.graphics.getRenderer());
        Fw.gui.setScreen(pixelLitGuiScreen);

        // Build shaders
        try {
            diffuseTextureShader = DiffuseTextureShader.Factory.Create();
            diffuseTextureShader.init();
            skyboxShader = SkyboxShader.Factory.Create();
            skyboxShader.init();
            pixelLitSingleBlinnPhongSpecularDirectionalShader = BlinnPhongSpecularDirectionalShader.Factory.Create();
            pixelLitSingleBlinnPhongSpecularDirectionalShader.init();
            pixelLitSingleBlinnPhongSpecularPointShader = BlinnPhongSpecularPointShader.Factory.Create();
            pixelLitSingleBlinnPhongSpecularPointShader.init();
            pixelLitSingleBlinnPhongSpecularSpotShader = BlinnPhongSpecularSpotShader.Factory.Create();
            pixelLitSingleBlinnPhongSpecularSpotShader.init();
            pixelLitSingleBlinnPhongSpecularSpotTwoConeShader = BlinnPhongSpecularSpotTwoConeShader.Factory.Create();
            pixelLitSingleBlinnPhongSpecularSpotTwoConeShader.init();
            pixelLitSingleDiffuseDirectionalShader = DiffuseDirectionalShader.Factory.Create();
            pixelLitSingleDiffuseDirectionalShader.init();
            pixelLitSingleDiffusePointShader = DiffusePointShader.Factory.Create();
            pixelLitSingleDiffusePointShader.init();
            pixelLitSingleDiffuseSpotShader = DiffuseSpotShader.Factory.Create();
            pixelLitSingleDiffuseSpotShader.init();
            pixelLitSingleDiffuseSpotTwoConeShader = DiffuseSpotTwoConeShader.Factory.Create();
            pixelLitSingleDiffuseSpotTwoConeShader.init();
            pixelLitSinglePhongSpecularDirectionalShader = PhongSpecularDirectionalShader.Factory.Create();
            pixelLitSinglePhongSpecularDirectionalShader.init();
            pixelLitSinglePhongSpecularPointShader = PhongSpecularPointShader.Factory.Create();
            pixelLitSinglePhongSpecularPointShader.init();
            pixelLitSinglePhongSpecularSpotShader = PhongSpecularSpotShader.Factory.Create();
            pixelLitSinglePhongSpecularSpotShader.init();
            pixelLitSinglePhongSpecularSpotTwoConeShader = PhongSpecularSpotTwoConeShader.Factory.Create();
            pixelLitSinglePhongSpecularSpotTwoConeShader.init();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // Set current shader
        updateCurrentShader();

        // OpenGL settings
        GL.o1.setBlendingFunctionSrcAlphaAndOneMinusSrcAlpha();
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
        memoryUsage.update();

        if (Fw.timer.isGameTick()) {
            // Do nothing
        }

        if (Fw.timer.isRenderTick()) {
            handleInput();

            // Clear screen
            GL.o1.clear();

            // Update camera
            Fw.graphics.setCamera(camera);

            // Update current light
            updateCurrentLight();

            // Update global uniform variables
            GL.uniformVariables.setGlLight(currentLight);

            // Bind shader
            Fw.graphics.switchShader(skyboxShader);

            // Render skybox
            Fw.graphics.color(1f, 1f, 1f, 1f);
            skyboxGameObject.getWorldTransform().setPosition(camera.getPosition());
            GL.o1.disableCulling();
            GL.o1.disableDepthTest();
            Fw.graphics.render(skyboxGameObject);
            GL.o1.enableDepthTest();
            GL.o1.enableCulling();

            // Unbind shader
            Fw.graphics.unbindShader();

            // Bind shader
            Fw.graphics.switchShader(currentShader);

            // Render directly
            Fw.graphics.render(gameObjectsToRender);

            // Unbind shader
            Fw.graphics.unbindShader();

            // Bind diffuse texture shader (no lighting)
            Fw.graphics.switchShader(diffuseTextureShader);

            // View frustum
            if (viewFrustumGameObject.getRenderableState().isInFrustum()) {
                Fw.graphics.renderBlend(viewFrustumGameObject);
            }

            // AABBs
            if (renderAabbs) {
                for (GameObject gameObject : gameObjectsToRender) {
                    AABB worldAabb = gameObject.getWorldAabb();
                    // Updating AABBs this way is costly
                    aabbGameObject.update(worldAabb);
                    // AABB is already in world coordinates so we don't translate
                    Fw.graphics.render(aabbGameObject.getRenderable(), 0f, 0f, 0f);
                }
            }

            // Orientation axis
            if (renderSimpleOrientationAxis) {
                GL.o1.disableDepthTest();
                for (GameObject gameObject : gameObjectsToRender) {
                    simpleOrientationAxisGameObject.setWorldTransform(gameObject.getWorldTransform());
                    Fw.graphics.render(simpleOrientationAxisGameObject);
                }
                GL.o1.enableDepthTest();
            }

            // Render 2D
            GL.o1.enableBlending();
            GL.o1.disableDepthTest();
            Fw.graphics.set2DMode();
            Fw.gui.update();
            Fw.gui.render();
            GL.o1.enableDepthTest();
            GL.o1.disableBlending();

            // Unbind shader
            Fw.graphics.unbindShader();
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
