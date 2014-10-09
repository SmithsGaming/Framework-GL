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
package net.smert.frameworkgl.opengl.mesh.dynamic;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public final class Quality {

    private int x;
    private int y;
    private int z;

    public Quality() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Quality(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getZ() {
        return z;
    }

    public final void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final void setX(int x) {
        this.x = x;
    }

    public final void setY(int y) {
        this.y = y;
    }

    public final void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "(x: " + x + " y: " + y + " z: " + z + ")";
    }

}
