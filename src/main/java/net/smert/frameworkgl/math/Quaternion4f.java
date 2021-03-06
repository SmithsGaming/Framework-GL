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
public class Quaternion4f {

    private final static Logger log = LoggerFactory.getLogger(Quaternion4f.class);

    float w;
    float x;
    float y;
    float z;

    // Constructors
    public Quaternion4f() {
        identity();
    }

    public Quaternion4f(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion4f(float w, Vector3f vector) {
        this.w = w;
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    public Quaternion4f(Matrix3f matrix) {
        fromMatrix(matrix);
    }

    public Quaternion4f(Quaternion4f quaternion) {
        w = quaternion.w;
        x = quaternion.x;
        y = quaternion.y;
        z = quaternion.z;
    }

    // Conversion Operations
    public void toFloatBuffer(FloatBuffer fbOut) {
        fbOut.put(w);
        fbOut.put(x);
        fbOut.put(y);
        fbOut.put(z);
    }

    public void toMatrix3(Matrix3f matrix) {
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float yy = y * y;
        float yz = y * z;
        float zz = z * z;
        matrix.setColumnMajor(
                1f - 2f * (yy + zz),
                2f * (xy - wz),
                2f * (xz + wy),
                2f * (xy + wz),
                1f - 2f * (xx + zz),
                2f * (yz - wx),
                2f * (xz - wy),
                2f * (yz + wx),
                1f - 2f * (xx + yy));
    }

    // Scalar Operations
    public float getAngle() {
        return MathHelper.ArcCos(w) * 2f;
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

    // Quaternion Operations
    public Quaternion4f fromAxisAngle(Vector3f vector, float degrees) {
        float halftheta = MathHelper.ToRadians(degrees) * .5f;
        float s = MathHelper.Sin(halftheta);
        w = MathHelper.Cos(halftheta);
        x = vector.x * s;
        y = vector.y * s;
        z = vector.z * s;
        return this;
    }

    public final Quaternion4f fromMatrix(Matrix3f matrix) {
        float t = matrix.xAxis.x + matrix.yAxis.y + matrix.zAxis.z;

        if (t > MathHelper.ZERO_EPSILON) {
            float s = MathHelper.Sqrt(t + 1f) * 2f;
            w = .25f * s;
            x = (matrix.zAxis.y - matrix.yAxis.z) / s;
            y = (matrix.xAxis.z - matrix.zAxis.x) / s;
            z = (matrix.yAxis.x - matrix.xAxis.y) / s;
        } else if ((matrix.xAxis.x > matrix.yAxis.y) && (matrix.xAxis.x > matrix.zAxis.z)) {
            float s = MathHelper.Sqrt(1f + matrix.xAxis.x - matrix.yAxis.y - matrix.zAxis.z) * 2f;
            w = (matrix.zAxis.y - matrix.yAxis.z) / s;
            x = .25f * s;
            y = (matrix.xAxis.y + matrix.yAxis.x) / s;
            z = (matrix.xAxis.z + matrix.zAxis.x) / s;
        } else if (matrix.yAxis.y > matrix.zAxis.z) {
            float s = MathHelper.Sqrt(1f + matrix.yAxis.y - matrix.xAxis.x - matrix.zAxis.z) * 2f;
            w = (matrix.xAxis.z - matrix.zAxis.x) / s;
            x = (matrix.xAxis.y + matrix.yAxis.x) / s;
            y = .25f * s;
            z = (matrix.yAxis.z + matrix.zAxis.y) / s;
        } else {
            float s = MathHelper.Sqrt(1f + matrix.zAxis.z - matrix.xAxis.x - matrix.yAxis.y) * 2f;
            w = (matrix.yAxis.x - matrix.xAxis.y) / s;
            x = (matrix.xAxis.z + matrix.zAxis.x) / s;
            y = (matrix.yAxis.z + matrix.zAxis.y) / s;
            z = .25f * s;
        }

        return this;
    }

    public final Quaternion4f identity() {
        w = 1f;
        x = 0f;
        y = 0f;
        z = 0f;
        return this;
    }

    public Quaternion4f multiply(float value) {
        w *= value;
        x *= value;
        y *= value;
        z *= value;
        return this;
    }

    public Quaternion4f multiply(Quaternion4f quaternion) {
        set(
                w * quaternion.w - x * quaternion.x - y * quaternion.y - z * quaternion.z,
                w * quaternion.x + x * quaternion.w + y * quaternion.z - z * quaternion.y,
                w * quaternion.y + y * quaternion.w + z * quaternion.x - x * quaternion.z,
                w * quaternion.z + z * quaternion.w + x * quaternion.y - y * quaternion.x);
        return this;
    }

    public Quaternion4f multiplyOut(Quaternion4f quaternion, Quaternion4f out) {
        out.set(
                w * quaternion.w - x * quaternion.x - y * quaternion.y - z * quaternion.z,
                w * quaternion.x + x * quaternion.w + y * quaternion.z - z * quaternion.y,
                w * quaternion.y + y * quaternion.w + z * quaternion.x - x * quaternion.z,
                w * quaternion.z + z * quaternion.w + x * quaternion.y - y * quaternion.x);
        return out;
    }

    public Quaternion4f normalize() {
        float mag = magnitude();

        if (mag < MathHelper.ZERO_EPSILON) {
            log.warn("Divide By Zero. Magnitude: {} x: {} y: {} z: {} w: {}", mag, x, y, z, w);
            return identity();
        }

        return multiply(1f / mag);
    }

    public Quaternion4f set(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Quaternion4f set(float w, Vector3f vector) {
        this.w = w;
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
        return this;
    }

    public Quaternion4f set(Quaternion4f quaternion) {
        w = quaternion.w;
        x = quaternion.x;
        y = quaternion.y;
        z = quaternion.z;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Float.floatToIntBits(this.w);
        hash = 37 * hash + Float.floatToIntBits(this.x);
        hash = 37 * hash + Float.floatToIntBits(this.y);
        hash = 37 * hash + Float.floatToIntBits(this.z);
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
        final Quaternion4f other = (Quaternion4f) obj;
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
