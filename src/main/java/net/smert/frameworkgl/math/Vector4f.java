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
package net.smert.frameworkgl.math;

import java.nio.FloatBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class Vector4f {

    private static Logger log = LoggerFactory.getLogger(Vector4f.class);

    float w;
    float x;
    float y;
    float z;

    // Constructors
    public Vector4f() {
        zero();
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector4f v) {
        x = v.x;
        y = v.y;
        z = v.z;
        w = v.w;
    }

    // Boolean Results
    public boolean planeAABBEquation(Vector3f aabbMin, Vector3f aabbMax, float threshold) {

        // This will not work correctly for objects bigger than the frustum
        float xMinX = x * aabbMin.x;
        float yMinY = y * aabbMin.y;
        float zMinZ = z * aabbMin.z;
        if ((xMinX + yMinY + zMinZ + w) < threshold) {
            return false;
        }
        float zMaxZ = z * aabbMax.z;
        if ((xMinX + yMinY + zMaxZ + w) < threshold) {
            return false;
        }
        float yMaxY = y * aabbMax.y;
        if ((xMinX + yMaxY + zMinZ + w) < threshold) {
            return false;
        }
        float xMaxX = x * aabbMax.x;
        return ((xMinX + yMaxY + zMaxZ + w) < threshold)
                || ((xMaxX + yMinY + zMinZ + w) < threshold)
                || ((xMaxX + yMinY + zMaxZ + w) < threshold)
                || ((xMaxX + yMaxY + zMinZ + w) < threshold)
                || ((xMaxX + yMaxY + zMaxZ + w) < threshold);
    }

    public boolean planePointEquation(float x, float y, float z, float threshold) {
        return ((this.x * x + this.y * y + this.z * z + w) > -threshold);
    }

    public boolean planePointEquation(Vector3f v, float threshold) {
        return planePointEquation(v.x, v.y, v.z, threshold);
    }

    // Conversion Operations
    public void toFloatBuffer(FloatBuffer fbOut) {
        fbOut.put(x);
        fbOut.put(y);
        fbOut.put(z);
        fbOut.put(w);
    }

    // Scalar Results
    public float distance(Vector4f vector) {
        float d0 = w - vector.w;
        float d1 = x - vector.x;
        float d2 = y - vector.y;
        float d3 = z - vector.z;
        return MathHelper.Sqrt(d0 * d0 + d1 * d1 + d2 * d2 + d3 * d3);
    }

    public float distanceSquared(Vector4f vector) {
        float d0 = w - vector.w;
        float d1 = x - vector.x;
        float d2 = y - vector.y;
        float d3 = z - vector.z;
        return d0 * d0 + d1 * d1 + d2 * d2 + d3 * d3;
    }

    public float dot(Vector4f vector) {
        return w * vector.w + x * vector.x + y * vector.y + z * vector.z;
    }

    public float dot3(Vector3f vector) {
        return x * vector.x + y * vector.y + z * vector.z;
    }

    public float dot3(Vector4f vector) {
        return x * vector.x + y * vector.y + z * vector.z;
    }

    public float getW() {
        return w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float magnitude() {
        return MathHelper.Sqrt(w * w + x * x + y * y + z * z);
    }

    public float magnitudeSquared() {
        return w * w + x * x + y * y + z * z;
    }

    // Vector Results
    public Vector4f abs() {
        w = Math.abs(w);
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }

    public void add(float x, float y, float z, float w) {
        this.w += w;
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void add(Vector4f vector) {
        this.w += vector.w;
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
    }

    public void addW(float w) {
        this.w += w;
    }

    public void addX(float x) {
        this.x += x;
    }

    public void addY(float y) {
        this.y += y;
    }

    public void addZ(float z) {
        this.z += z;
    }

    public Vector4f absolute() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        w = Math.abs(w);
        return this;
    }

    public Vector4f multiply(float value) {
        w *= value;
        x *= value;
        y *= value;
        z *= value;
        return this;
    }

    public Vector4f normalize() {
        float mag = magnitude();

        if (mag < MathHelper.ZERO_EPSILON) {
            log.warn("Divide By Zero. Magnitude: {} x: {} y: {} z: {} w: {}", mag, x, y, z, w);

            w = 0f;
            x = 0f;
            y = 0f;
            z = -1f;

            return this;
        }

        return multiply(1f / mag);
    }

    public void set(float x, float y, float z, float w) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector2f vector, float z, float w) {
        this.w = w;
        x = vector.x;
        y = vector.y;
        this.z = z;
    }

    public void set(Vector3f vector, float w) {
        this.w = w;
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    public void set(Vector4f vector) {
        w = vector.w;
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public final void zero() {
        w = 0f;
        x = 0f;
        y = 0f;
        z = 0f;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Float.floatToIntBits(this.w);
        hash = 89 * hash + Float.floatToIntBits(this.x);
        hash = 89 * hash + Float.floatToIntBits(this.y);
        hash = 89 * hash + Float.floatToIntBits(this.z);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector4f other = (Vector4f) obj;
        if (Float.floatToIntBits(this.w) != Float.floatToIntBits(other.w)) {
            return false;
        }
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        return Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
    }

    @Override
    public String toString() {
        return "(x: " + x + " y: " + y + " z: " + z + " w: " + w + ")";
    }

}
