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
package net.smert.frameworkgl.opengl.camera;

import net.smert.frameworkgl.math.MathHelper;
import net.smert.frameworkgl.math.Matrix3f;
import net.smert.frameworkgl.math.Matrix4f;
import net.smert.frameworkgl.math.Vector3f;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class Camera {

    public final static float MAX_SAFE_PITCH = 90f;
    public final static float MAX_SAFE_ROLL = 30f;

    private boolean invert;
    private float aspectRatio;
    private float fieldOfView;
    private float totalHeading;
    private float totalPitch;
    private float totalRoll;
    private float zFar;
    private float zNear;

    private AbstractFrustumCulling frustumCulling;
    private final Matrix3f movementMatrix;
    private final Matrix3f rotationMatrix;
    private final Matrix3f tempMatrix;
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Vector3f position;
    private final Vector3f viewDirection;

    public Camera() {
        invert = false;
        aspectRatio = 0f;
        fieldOfView = 0f;
        totalHeading = 0f;
        totalPitch = 0f;
        totalRoll = 0f;
        zFar = 0f;
        zNear = 0f;
        movementMatrix = new Matrix3f();
        rotationMatrix = new Matrix3f();
        tempMatrix = new Matrix3f();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        position = new Vector3f();
        viewDirection = new Vector3f();
    }

    private void updateRotationMatrix() {
        rotationMatrix.orthonormalize();
        viewDirection.setInvert(rotationMatrix.getZAxis());
        totalHeading = rotationMatrix.getHeading();
        totalPitch = rotationMatrix.getPitch();
        totalRoll = rotationMatrix.getRoll();
    }

    public float getHeading() {
        return totalHeading;
    }

    public float getPitch() {
        return totalPitch;
    }

    public float getRoll() {
        return totalRoll;
    }

    public AbstractFrustumCulling getFrustumCulling() {
        return frustumCulling;
    }

    public void setFrustumCulling(AbstractFrustumCulling frustumCulling) {
        this.frustumCulling = frustumCulling;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public void lookAt(Vector3f position, Vector3f target, Vector3f up) {
        this.position.set(position);
        rotationMatrix.orthonormalize(position, target, up);
        viewDirection.setInvert(rotationMatrix.getZAxis());
        totalHeading = rotationMatrix.getHeading();
        totalPitch = rotationMatrix.getPitch();
        totalRoll = rotationMatrix.getRoll();
    }

    public void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    public void moveForward(float dx, float dy, float dz) {
        float zdotup = viewDirection.dot(Vector3f.WORLD_Y_AXIS);

        if ((zdotup < -MathHelper.TOLERANCE_DOT_PRODUCT_PARALLEL) || (zdotup > MathHelper.TOLERANCE_DOT_PRODUCT_PARALLEL)) {
            movementMatrix.getZAxis().set(Vector3f.WORLD_Y_AXIS).cross(rotationMatrix.getXAxis()).normalize();
            movementMatrix.getXAxis().set(movementMatrix.getZAxis()).cross(Vector3f.WORLD_Y_AXIS).normalize();
            movementMatrix.getYAxis().set(movementMatrix.getXAxis()).cross(movementMatrix.getZAxis()).normalize();
        } else {
            movementMatrix.getZAxis().set(viewDirection).setY(0).normalize();
            movementMatrix.getXAxis().set(movementMatrix.getZAxis()).cross(Vector3f.WORLD_Y_AXIS).normalize();
            movementMatrix.getYAxis().set(movementMatrix.getXAxis()).cross(movementMatrix.getZAxis()).normalize();
        }

        position.addScaled(movementMatrix.getXAxis(), dx);
        position.addScaled(movementMatrix.getYAxis(), dy);
        position.addScaled(movementMatrix.getZAxis(), dz);
    }

    public void rotate(float pitch, float heading, float roll) {

        if (invert) {
            pitch = -pitch;
        }

        totalPitch += pitch;

        if (totalPitch > MAX_SAFE_PITCH) {
            pitch = MAX_SAFE_PITCH - (totalPitch - pitch);
        }
        if (totalPitch < -MAX_SAFE_PITCH) {
            pitch = -MAX_SAFE_PITCH - (totalPitch - pitch);
        }

        totalRoll += roll;

        if (totalRoll > MAX_SAFE_ROLL) {
            roll = MAX_SAFE_ROLL - (totalRoll - roll);
        }
        if (totalRoll < -MAX_SAFE_ROLL) {
            roll = -MAX_SAFE_ROLL - (totalRoll - roll);
        }

        if (heading != 0f) {
            tempMatrix.fromAxisAngle(Vector3f.WORLD_Y_AXIS, heading);
            tempMatrix.multiplyOut(rotationMatrix.getXAxis(), rotationMatrix.getXAxis());
            tempMatrix.multiplyOut(rotationMatrix.getZAxis(), rotationMatrix.getZAxis());
        }
        if (roll != 0f) {
            tempMatrix.fromAxisAngle(rotationMatrix.getZAxis(), roll);
            tempMatrix.multiplyOut(rotationMatrix.getYAxis(), rotationMatrix.getYAxis());
            tempMatrix.multiplyOut(rotationMatrix.getXAxis(), rotationMatrix.getXAxis());
        }
        if (pitch != 0f) {
            tempMatrix.fromAxisAngle(rotationMatrix.getXAxis(), pitch);
            tempMatrix.multiplyOut(rotationMatrix.getYAxis(), rotationMatrix.getYAxis());
            tempMatrix.multiplyOut(rotationMatrix.getZAxis(), rotationMatrix.getZAxis());
        }

        updateRotationMatrix();
    }

    public void resetRotation() {
        rotationMatrix.setAxes(Vector3f.WORLD_X_AXIS, Vector3f.WORLD_Y_AXIS, Vector3f.WORLD_Z_AXIS);
        updateRotationMatrix();
    }

    public void setFrustum(float left, float right, float bottom, float top, float znear, float zfar) {
        float invdeltay = 1f / (top - bottom);
        float h = 2f * znear * invdeltay;
        aspectRatio = 1f;
        fieldOfView = MathHelper.ArcTan(1f / h) / MathHelper.PI_OVER_360;
        zFar = zfar;
        zNear = znear;
        projectionMatrix.setFrustum(left, right, bottom, top, znear, zfar);
    }

    public void setOrthogonalProjection(float left, float right, float bottom, float top, float znear, float zfar) {
        float invdeltay = 1f / (top - bottom);
        float h = 2f * invdeltay;
        aspectRatio = 1f;
        fieldOfView = MathHelper.ArcTan(1f / h) / MathHelper.PI_OVER_360;
        zFar = zfar;
        zNear = znear;
        projectionMatrix.setOrthogonal(left, right, bottom, top, znear, zfar);
    }

    public void setPerspectiveProjection(float fieldofviewy, float aspectratio, float znear, float zfar) {
        aspectRatio = aspectratio;
        fieldOfView = fieldofviewy;
        zFar = zfar;
        zNear = znear;
        projectionMatrix.setPerspective(fieldofviewy, aspectratio, znear, zfar);
    }

    public void updatePlanes() {
        frustumCulling.updatePlanes(projectionMatrix, viewMatrix);
    }

    public void updateViewMatrix() {
        viewMatrix.setInverse(rotationMatrix, position);
    }

}