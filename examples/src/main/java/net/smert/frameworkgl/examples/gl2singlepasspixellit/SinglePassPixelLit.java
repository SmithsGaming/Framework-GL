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
package net.smert.frameworkgl.examples.gl2singlepasspixellit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.smert.frameworkgl.Fw;
import net.smert.frameworkgl.Screen;
import net.smert.frameworkgl.examples.common.DynamicMeshWorld;
import net.smert.frameworkgl.examples.common.MultipleLightsOfTheSameType;
import net.smert.frameworkgl.gameobjects.AABBGameObject;
import net.smert.frameworkgl.gameobjects.GameObject;
import net.smert.frameworkgl.gameobjects.RenderStatisticsGameObject;
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
import net.smert.frameworkgl.opengl.shader.pixellit.multi.BlinnPhongSpecularDirectionalShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.BlinnPhongSpecularPointShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.BlinnPhongSpecularSpotShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.BlinnPhongSpecularSpotTwoConeShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.DiffuseDirectionalShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.DiffusePointShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.DiffuseSpotShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.DiffuseSpotTwoConeShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.PhongSpecularDirectionalShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.PhongSpecularPointShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.PhongSpecularSpotShader;
import net.smert.frameworkgl.opengl.shader.pixellit.multi.PhongSpecularSpotTwoConeShader;
import net.smert.frameworkgl.utils.FpsTimer;
import net.smert.frameworkgl.utils.MemoryUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class SinglePassPixelLit extends Screen {

    private final static Logger log = LoggerFactory.getLogger(SinglePassPixelLit.class);

    private boolean renderAabbs;
    private boolean renderSimpleOrientationAxis;
    private boolean wireframe;
    private float spotInnerCutoff;
    private float spotOuterCutoff;
    private int shaderIndex;
    private AbstractShader currentShader;
    private AABBGameObject aabbGameObject;
    private BlinnPhongSpecularDirectionalShader pixelLitMultiBlinnPhongSpecularDirectionalShader;
    private BlinnPhongSpecularPointShader pixelLitMultiBlinnPhongSpecularPointShader;
    private BlinnPhongSpecularSpotShader pixelLitMultiBlinnPhongSpecularSpotShader;
    private BlinnPhongSpecularSpotTwoConeShader pixelLitMultiBlinnPhongSpecularSpotTwoConeShader;
    private Camera camera;
    private CameraController cameraController;
    private DiffuseDirectionalShader pixelLitMultiDiffuseDirectionalShader;
    private DiffusePointShader pixelLitMultiDiffusePointShader;
    private DiffuseSpotShader pixelLitMultiDiffuseSpotShader;
    private DiffuseSpotTwoConeShader pixelLitMultiDiffuseSpotTwoConeShader;
    private DiffuseTextureShader diffuseTextureShader;
    private DynamicMeshWorld dynamicMeshesWorld;
    private FpsTimer fpsTimer;
    private final List<GameObject> gameObjectsToRender;
    private List<GLLight> currentLights;
    private MemoryUsage memoryUsage;
    private MultipleLightsOfTheSameType multipleLightsOfTheSameType;
    private PhongSpecularDirectionalShader pixelLitMultiPhongSpecularDirectionalShader;
    private PhongSpecularPointShader pixelLitMultiPhongSpecularPointShader;
    private PhongSpecularSpotShader pixelLitMultiPhongSpecularSpotShader;
    private PhongSpecularSpotTwoConeShader pixelLitMultiPhongSpecularSpotTwoConeShader;
    private RenderStatisticsGameObject renderStatisticsGameObject;
    private SimpleOrientationAxisGameObject simpleOrientationAxisGameObject;
    private SkyboxGameObject skyboxGameObject;
    private SkyboxShader skyboxShader;
    private ViewFrustumGameObject viewFrustumGameObject;

    public SinglePassPixelLit(String[] args) {
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
                GL.o1.setPolygonModeFrontLine();
            } else {
                GL.o1.setPolygonModeFrontFill();
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
            for (GLLight light : multipleLightsOfTheSameType.getSpotLights()) {
                light.setSpotOuterCutoff(spotOuterCutoff);
            }
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
            for (GLLight light : multipleLightsOfTheSameType.getSpotLights()) {
                light.setSpotInnerCutoff(spotInnerCutoff);
            }
        }
        if (Fw.input.isKeyDown(Keyboard.LBRACKET) && !Fw.input.wasKeyDown(Keyboard.LBRACKET)) {
            if (--shaderIndex < 0) {
                shaderIndex += 12;
            }
            updateCurrentShader();
        }
        if (Fw.input.isKeyDown(Keyboard.RBRACKET) && !Fw.input.wasKeyDown(Keyboard.RBRACKET)) {
            shaderIndex = ++shaderIndex % 12;
            updateCurrentShader();
        }
        cameraController.update();
    }

    private void renderCurrentShaderName() {
        Fw.graphics.textNewLine();
        Fw.graphics.setTextColor("yellow");
        String shaderName = "";

        switch (shaderIndex) {
            case 0:
                shaderName = "BlinnPhongSpecularDirectionalShader";
                break;
            case 1:
                shaderName = "BlinnPhongSpecularPointShader";
                break;
            case 2:
                shaderName = "BlinnPhongSpecularSpotShader";
                break;
            case 3:
                shaderName = "BlinnPhongSpecularSpotTwoConeShader";
                break;
            case 4:
                shaderName = "DiffuseDirectionalShader";
                break;
            case 5:
                shaderName = "DiffusePointShader";
                break;
            case 6:
                shaderName = "DiffuseSpotShader";
                break;
            case 7:
                shaderName = "DiffuseSpotTwoConeShader";
                break;
            case 8:
                shaderName = "PhongSpecularDirectionalShader";
                break;
            case 9:
                shaderName = "PhongSpecularPointShader";
                break;
            case 10:
                shaderName = "PhongSpecularSpotShader";
                break;
            case 11:
                shaderName = "PhongSpecularSpotTwoConeShader";
                break;
        }

        Fw.graphics.drawString(shaderName);
        if ((shaderIndex == 2) || (shaderIndex == 6) || (shaderIndex == 10)) {
            Fw.graphics.textNewLine();
            Fw.graphics.setTextColor("lime");
            Fw.graphics.drawString("Spot cutoff: " + spotOuterCutoff);
        }
        if ((shaderIndex == 3) || (shaderIndex == 7) || (shaderIndex == 11)) {
            Fw.graphics.textNewLine();
            Fw.graphics.setTextColor("lime");
            Fw.graphics.drawString("Spot outer cutoff: " + spotOuterCutoff);
            Fw.graphics.textNewLine();
            Fw.graphics.drawString("Spot inner cutoff: " + spotInnerCutoff);
        }
    }

    private void updateCurrentLights() {
        switch (shaderIndex) {
            case 0:
            case 4:
            case 8:
                currentLights = multipleLightsOfTheSameType.getDirectionalLights();
                break;
            case 1:
            case 5:
            case 9:
                currentLights = multipleLightsOfTheSameType.getPointLights();
                break;
            case 2:
            case 3:
            case 6:
            case 7:
            case 10:
            case 11:
                currentLights = multipleLightsOfTheSameType.getSpotLights();
                break;
        }
    }

    private void updateCurrentShader() {
        switch (shaderIndex) {
            case 0:
                currentShader = pixelLitMultiBlinnPhongSpecularDirectionalShader;
                break;
            case 1:
                currentShader = pixelLitMultiBlinnPhongSpecularPointShader;
                break;
            case 2:
                currentShader = pixelLitMultiBlinnPhongSpecularSpotShader;
                break;
            case 3:
                currentShader = pixelLitMultiBlinnPhongSpecularSpotTwoConeShader;
                break;
            case 4:
                currentShader = pixelLitMultiDiffuseDirectionalShader;
                break;
            case 5:
                currentShader = pixelLitMultiDiffusePointShader;
                break;
            case 6:
                currentShader = pixelLitMultiDiffuseSpotShader;
                break;
            case 7:
                currentShader = pixelLitMultiDiffuseSpotTwoConeShader;
                break;
            case 8:
                currentShader = pixelLitMultiPhongSpecularDirectionalShader;
                break;
            case 9:
                currentShader = pixelLitMultiPhongSpecularPointShader;
                break;
            case 10:
                currentShader = pixelLitMultiPhongSpecularSpotShader;
                break;
            case 11:
                currentShader = pixelLitMultiPhongSpecularSpotTwoConeShader;
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

        // Switch renderer and factory to OpenGL 2
        Fw.graphics.switchRenderableFactoryAndRenderer(2);

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
        multipleLightsOfTheSameType = new MultipleLightsOfTheSameType();
        multipleLightsOfTheSameType.init();
        spotInnerCutoff = 180f;
        spotOuterCutoff = 180f;
        GL.uniformVariables.getDefaultMaterialLight().setShininess(16);
        // Effectively disables diffuse and per vertex color will be used
        GL.uniformVariables.getDefaultMaterialLight().setDiffuse(new Vector4f(1f, 1f, 1f, 1f));
        GL.uniformVariables.getDefaultMaterialLight().setSpecular(new Vector4f(.3f, .3f, .3f, 1f));

        // Load textures
        try {
            // Texture must be loaded before the renderable is created
            // Parameters ("skybox/miramar", "png") will create a texture named "skybox/miramar/cubemap.png"
            // from "skybox/miramar/xpos.png", "skybox/miramar/xneg.png", "skybox/miramar/ypos.png"
            // "skybox/miramar/yneg.png", "skybox/miramar/zpos.png" and "skybox/miramar/zneg.png"
            Fw.graphics.loadCubeMapTexture("skybox/miramar", "png");
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

        // Render statistics game object
        renderStatisticsGameObject = new RenderStatisticsGameObject();
        renderStatisticsGameObject.init(Fw.graphics);

        // Simple axis game object
        simpleOrientationAxisGameObject = new SimpleOrientationAxisGameObject();
        simpleOrientationAxisGameObject.getColor0().set("red");
        simpleOrientationAxisGameObject.getColor1().set("green");
        simpleOrientationAxisGameObject.getColor2().set("blue");
        simpleOrientationAxisGameObject.init();

        // Skybox game object
        skyboxGameObject = new SkyboxGameObject();
        skyboxGameObject.init("skybox/miramar/cubemap.png");

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

        // Build shaders
        try {
            diffuseTextureShader = DiffuseTextureShader.Factory.Create();
            diffuseTextureShader.init();
            skyboxShader = SkyboxShader.Factory.Create();
            skyboxShader.init();
            pixelLitMultiBlinnPhongSpecularDirectionalShader = BlinnPhongSpecularDirectionalShader.Factory.Create();
            pixelLitMultiBlinnPhongSpecularDirectionalShader.init();
            pixelLitMultiBlinnPhongSpecularPointShader = BlinnPhongSpecularPointShader.Factory.Create();
            pixelLitMultiBlinnPhongSpecularPointShader.init();
            pixelLitMultiBlinnPhongSpecularSpotShader = BlinnPhongSpecularSpotShader.Factory.Create();
            pixelLitMultiBlinnPhongSpecularSpotShader.init();
            pixelLitMultiBlinnPhongSpecularSpotTwoConeShader = BlinnPhongSpecularSpotTwoConeShader.Factory.Create();
            pixelLitMultiBlinnPhongSpecularSpotTwoConeShader.init();
            pixelLitMultiDiffuseDirectionalShader = DiffuseDirectionalShader.Factory.Create();
            pixelLitMultiDiffuseDirectionalShader.init();
            pixelLitMultiDiffusePointShader = DiffusePointShader.Factory.Create();
            pixelLitMultiDiffusePointShader.init();
            pixelLitMultiDiffuseSpotShader = DiffuseSpotShader.Factory.Create();
            pixelLitMultiDiffuseSpotShader.init();
            pixelLitMultiDiffuseSpotTwoConeShader = DiffuseSpotTwoConeShader.Factory.Create();
            pixelLitMultiDiffuseSpotTwoConeShader.init();
            pixelLitMultiPhongSpecularDirectionalShader = PhongSpecularDirectionalShader.Factory.Create();
            pixelLitMultiPhongSpecularDirectionalShader.init();
            pixelLitMultiPhongSpecularPointShader = PhongSpecularPointShader.Factory.Create();
            pixelLitMultiPhongSpecularPointShader.init();
            pixelLitMultiPhongSpecularSpotShader = PhongSpecularSpotShader.Factory.Create();
            pixelLitMultiPhongSpecularSpotShader.init();
            pixelLitMultiPhongSpecularSpotTwoConeShader = PhongSpecularSpotTwoConeShader.Factory.Create();
            pixelLitMultiPhongSpecularSpotTwoConeShader.init();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // Set current shader
        shaderIndex = 5;
        updateCurrentShader();

        // OpenGL settings
        GL.o1.setBlendingFunctionSrcAlphaAndOneMinusSrcAlpha();
        GL.o1.enableCulling();
        GL.o1.cullBackFaces();
        GL.o1.enableDepthTest();
        GL.o1.setDepthFuncLess();
        GL.o1.enableDepthMask();
        GL.o1.setClearDepth(1f);
        GL.o1.enableTexture2D();
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
        renderStatisticsGameObject.update();

        if (Fw.timer.isGameTick()) {
            // Do nothing
        }

        if (Fw.timer.isRenderTick()) {
            handleInput();

            // Clear screen
            GL.o1.clear();

            // Update camera
            Fw.graphics.setCamera(camera);

            // Update current lights
            updateCurrentLights();

            // Update global uniform variables
            GL.uniformVariables.setGlLights(currentLights);

            // Bind shader
            Fw.graphics.switchShader(skyboxShader);

            // Render skybox
            Fw.graphics.color(1f, 1f, 1f, 1f);
            skyboxGameObject.getWorldTransform().setPosition(camera.getPosition());
            GL.o1.enableTextureCubeMap();
            GL.o1.disableCulling();
            GL.o1.disableDepthTest();
            Fw.graphics.render(skyboxGameObject);
            GL.o1.enableDepthTest();
            GL.o1.enableCulling();
            GL.o1.disableTextureCubeMap();

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
            Fw.graphics.resetTextRendering();
            Fw.graphics.textNewHalfLine();
            renderStatisticsGameObject.render(); // Game object has no renderable
            renderCurrentShaderName();
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
