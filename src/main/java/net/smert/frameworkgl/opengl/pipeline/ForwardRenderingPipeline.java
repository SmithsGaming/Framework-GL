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
package net.smert.frameworkgl.opengl.pipeline;

import java.io.IOException;
import net.smert.frameworkgl.Fw;
import net.smert.frameworkgl.gameobjects.AABBGameObject;
import net.smert.frameworkgl.gameobjects.SimpleOrientationAxisGameObject;
import net.smert.frameworkgl.gameobjects.SkyboxGameObject;
import net.smert.frameworkgl.gameobjects.ViewFrustumGameObject;
import net.smert.frameworkgl.math.AABB;
import net.smert.frameworkgl.opengl.GL;
import net.smert.frameworkgl.opengl.renderer.GuiRenderer;
import net.smert.frameworkgl.opengl.shader.AbstractShader;
import net.smert.frameworkgl.opengl.shader.basic.DiffuseTextureShader;
import net.smert.frameworkgl.opengl.shader.basic.SkyboxShader;
import net.smert.frameworkgl.utils.Color;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class ForwardRenderingPipeline extends AbstractRenderingPipeline implements AdvancedRenderingPipeline {

    private boolean renderAabbs;
    private boolean renderDebug;
    private boolean renderSimpleOrientationAxis;
    private boolean renderViewFrustum;
    private boolean updateAabbs;
    private AABBGameObject aabbGameObject;
    private AbstractShader currentDefaultShader;
    private AbstractShader defaultShader;
    private AbstractShader defaultShaderWithShadows;
    private final Color skyboxColor;
    private final Config config;
    private DiffuseTextureShader diffuseTextureShader;
    private GuiRenderer guiRenderer;
    private PipelineRenderCallback pipelineRenderCallback;
    private SimpleOrientationAxisGameObject simpleOrientationAxisGameObject;
    private SkyboxGameObject skyboxGameObject;
    private SkyboxShader skyboxShader;
    private ViewFrustumGameObject viewFrustumGameObject;

    public ForwardRenderingPipeline() {
        skyboxColor = new Color();
        config = new Config();
        reset();
    }

    @Override
    public void addAllGameObjectsToRender() {
        pipelineRenderCallback.addAllGameObjectsToRender();
    }

    @Override
    public AdvancedPipelineConfig getPipelineConfig() {
        return config;
    }

    @Override
    public void performFrustumCulling() {
        camera.updatePlanes();
        pipelineRenderCallback.performFrustumCulling(camera);
    }

    @Override
    public void updateAabbs() {
        pipelineRenderCallback.updateAabbs();
    }

    @Override
    public void updateCurrentShader() {
        if (shadowsEnabled) {
            currentDefaultShader = defaultShaderWithShadows;
        } else {
            currentDefaultShader = defaultShader;
        }
    }

    @Override
    public void updateViewFrustumGameObjectWithCamera() {
        viewFrustumGameObject.getWorldTransform().getRotation().set(camera.getRotationMatrix());
        viewFrustumGameObject.setWorldPosition(camera.getPosition());
        viewFrustumGameObject.update(camera.getAspectRatio(), camera.getFieldOfView(), camera.getZNear(),
                camera.getZFar());
        Fw.graphics.updateAabb(viewFrustumGameObject);
    }

    @Override
    public void destroy() {
        aabbGameObject.destroy();
        simpleOrientationAxisGameObject.destroy();
        viewFrustumGameObject.destroy();
        diffuseTextureShader.destroy();
        skyboxShader.destroy();
    }

    @Override
    public void init() throws IOException {

        // AABB game object
        aabbGameObject = new AABBGameObject();
        aabbGameObject.getColor0().set("yellow");
        aabbGameObject.init(new AABB()); // Empty AABB

        // Simple axis game object
        simpleOrientationAxisGameObject = new SimpleOrientationAxisGameObject();
        simpleOrientationAxisGameObject.getColor0().set("red");
        simpleOrientationAxisGameObject.getColor1().set("green");
        simpleOrientationAxisGameObject.getColor2().set("blue");
        simpleOrientationAxisGameObject.init();

        // View frustum game object
        viewFrustumGameObject = new ViewFrustumGameObject();
        viewFrustumGameObject.getColor0().set("black");
        viewFrustumGameObject.getColor1().set("yellow");
        viewFrustumGameObject.getColor2().set("yellow");
        viewFrustumGameObject.getColor3().set("white");
        viewFrustumGameObject.getColor3().setA(.4f);
        viewFrustumGameObject.init(camera.getAspectRatio(), camera.getFieldOfView(), camera.getZNear(),
                camera.getZFar());

        // Build shaders
        diffuseTextureShader = DiffuseTextureShader.Factory.Create();
        diffuseTextureShader.init();
        skyboxShader = SkyboxShader.Factory.Create();
        skyboxShader.init();

        updateCurrentShader();
    }

    @Override
    public void render() {

        // Reset state and clear
        GL.o1.disableBlending();
        GL.o1.enableColorMask();
        GL.o1.enableCulling();
        GL.o1.enableDepthMask();
        GL.o1.enableDepthTest();
        GL.o1.setDepthFuncLess();
        GL.o1.setPolygonModeFrontAndBackFill();
        GL.o1.clear();

        // Update camera
        Fw.graphics.setCamera(camera);

        // Update AABBs
        if (updateAabbs) {
            updateAabbs();
        }

        // Frustum culling
        if (frustumCulling) {
            performFrustumCulling();
        }

        // Render 3D
        Fw.graphics.switchShader(skyboxShader);
        Fw.graphics.color(skyboxColor.getR(), skyboxColor.getG(), skyboxColor.getB(), skyboxColor.getA());
        skyboxGameObject.getWorldTransform().setPosition(camera.getPosition());
        GL.o1.disableCulling();
        GL.o1.disableDepthTest();
        Fw.graphics.render(skyboxGameObject);
        GL.o1.enableDepthTest();
        GL.o1.enableCulling();
        Fw.graphics.unbindShader();

        if (shadowsEnabled) {
        }

        switchPolygonFillMode();
        Fw.graphics.switchShader(currentDefaultShader);
        pipelineRenderCallback.render();
        Fw.graphics.unbindShader();

        if (debug) {
            Fw.graphics.switchShader(diffuseTextureShader); // No lighting

            // View frustum
            if (renderViewFrustum) {
                Fw.graphics.renderBlend(viewFrustumGameObject);
            }

            // AABBs
            if (renderAabbs) {
                pipelineRenderCallback.renderAabbs(aabbGameObject);
            }

            // Orientation axis
            if (renderSimpleOrientationAxis) {
                GL.o1.disableDepthTest();
                pipelineRenderCallback.renderSimpleOrientationAxis(simpleOrientationAxisGameObject);
                GL.o1.enableDepthTest();
            }

            // Debug
            if (renderDebug) {
                pipelineRenderCallback.renderDebug();
            }

            Fw.graphics.unbindShader();
        }

        // Render 2D
        GL.o1.setBlendingFunctionSrcAlphaAndOneMinusSrcAlpha();
        GL.o1.enableBlending();
        GL.o1.disableDepthTest();
        GL.o1.setPolygonModeFrontAndBackFill();
        Fw.graphics.switchShader(diffuseTextureShader);
        Fw.graphics.set2DMode();
        guiRenderer.render();
        Fw.graphics.unbindShader();
        GL.o1.enableDepthTest();
        GL.o1.disableBlending();
    }

    @Override
    public final void reset() {
        super.reset();
        renderAabbs = false;
        renderDebug = false;
        renderSimpleOrientationAxis = false;
        renderViewFrustum = false;
        updateAabbs = true;
        skyboxColor.setWhite();
    }

    public class Config extends AbstractRenderingPipeline.Config implements AdvancedPipelineConfig {

        @Override
        public AABBGameObject getAabbGameObject() {
            return aabbGameObject;
        }

        @Override
        public void setAabbGameObject(AABBGameObject aabbGameObject) {
            ForwardRenderingPipeline.this.aabbGameObject = aabbGameObject;
        }

        @Override
        public AbstractShader getDefaultShader() {
            return defaultShader;
        }

        @Override
        public void setDefaultShader(AbstractShader defaultShader) {
            ForwardRenderingPipeline.this.defaultShader = defaultShader;
            updateCurrentShader();
        }

        @Override
        public AbstractShader getDefaultShaderWithShadows() {
            return defaultShaderWithShadows;
        }

        @Override
        public void setDefaultShaderWithShadows(AbstractShader defaultShaderWithShadows) {
            ForwardRenderingPipeline.this.defaultShaderWithShadows = defaultShaderWithShadows;
            updateCurrentShader();
        }

        @Override
        public Color getSkyboxColor() {
            return skyboxColor;
        }

        @Override
        public GuiRenderer getGuiRenderer() {
            return guiRenderer;
        }

        @Override
        public void setGuiRenderer(GuiRenderer guiRenderer) {
            ForwardRenderingPipeline.this.guiRenderer = guiRenderer;
        }

        @Override
        public PipelineRenderCallback getPipelineRenderCallback() {
            return pipelineRenderCallback;
        }

        @Override
        public void setPipelineRenderCallback(PipelineRenderCallback renderCallback) {
            ForwardRenderingPipeline.this.pipelineRenderCallback = renderCallback;
        }

        @Override
        public SimpleOrientationAxisGameObject getSimpleOrientationAxisGameObject() {
            return simpleOrientationAxisGameObject;
        }

        @Override
        public void setSimpleOrientationAxisGameObject(SimpleOrientationAxisGameObject simpleOrientationAxisGameObject) {
            ForwardRenderingPipeline.this.simpleOrientationAxisGameObject = simpleOrientationAxisGameObject;
        }

        @Override
        public SkyboxGameObject getSkyboxGameObject() {
            return skyboxGameObject;
        }

        @Override
        public void setSkyboxGameObject(SkyboxGameObject skyboxGameObject) {
            ForwardRenderingPipeline.this.skyboxGameObject = skyboxGameObject;
        }

        @Override
        public ViewFrustumGameObject getViewFrustumGameObject() {
            return viewFrustumGameObject;
        }

        @Override
        public void setViewFrustumGameObject(ViewFrustumGameObject viewFrustumGameObject) {
            ForwardRenderingPipeline.this.viewFrustumGameObject = viewFrustumGameObject;
        }

        @Override
        public boolean isRenderAabbs() {
            return renderAabbs;
        }

        @Override
        public void setRenderAabbs(boolean renderAabbs) {
            ForwardRenderingPipeline.this.renderAabbs = renderAabbs;
        }

        @Override
        public boolean isRenderDebug() {
            return renderDebug;
        }

        @Override
        public void setRenderDebug(boolean renderDebug) {
            ForwardRenderingPipeline.this.renderDebug = renderDebug;
        }

        @Override
        public boolean isRenderSimpleOrientationAxis() {
            return renderSimpleOrientationAxis;
        }

        @Override
        public void setRenderSimpleOrientationAxis(boolean renderSimpleOrientationAxis) {
            ForwardRenderingPipeline.this.renderSimpleOrientationAxis = renderSimpleOrientationAxis;
        }

        @Override
        public boolean isRenderViewFrustum() {
            return renderViewFrustum;
        }

        @Override
        public void setRenderViewFrustum(boolean renderViewFrustum) {
            ForwardRenderingPipeline.this.renderViewFrustum = renderViewFrustum;
        }

        @Override
        public boolean isUpdateAabbs() {
            return updateAabbs;
        }

        @Override
        public void setUpdateAabbs(boolean updateAabbs) {
            ForwardRenderingPipeline.this.updateAabbs = updateAabbs;
        }

        @Override
        public void setShadowsEnabled(boolean shadowsEnabled) {
            super.setShadowsEnabled(shadowsEnabled);
            updateCurrentShader();
        }

    }

}
