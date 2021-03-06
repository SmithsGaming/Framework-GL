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
package net.smert.frameworkgl.opengl.camera.factory;

import net.smert.frameworkgl.opengl.camera.Camera;
import net.smert.frameworkgl.opengl.camera.CameraController;
import net.smert.frameworkgl.opengl.camera.FrustumCullingClipSpace;
import net.smert.frameworkgl.opengl.camera.FrustumCullingClipSpaceSymmetrical;
import net.smert.frameworkgl.opengl.camera.LegacyCamera;
import net.smert.frameworkgl.opengl.camera.LegacyCameraController;
import org.picocontainer.MutablePicoContainer;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class CameraFactory {

    private final MutablePicoContainer container;

    public CameraFactory(MutablePicoContainer cameraFactoryContainer) {
        container = cameraFactoryContainer;
    }

    public Camera createCamera() {
        return container.getComponent(Camera.class);
    }

    public CameraController createCameraController() {
        return container.getComponent(CameraController.class);
    }

    public FrustumCullingClipSpace createFrustumCullingClipSpace() {
        return container.getComponent(FrustumCullingClipSpace.class);
    }

    public FrustumCullingClipSpaceSymmetrical createFrustumCullingClipSpaceSymmetrical() {
        return container.getComponent(FrustumCullingClipSpaceSymmetrical.class);
    }

    public LegacyCamera createLegacyCamera() {
        return container.getComponent(LegacyCamera.class);
    }

    public LegacyCameraController createLegacyCameraController() {
        return container.getComponent(LegacyCameraController.class);
    }

}
