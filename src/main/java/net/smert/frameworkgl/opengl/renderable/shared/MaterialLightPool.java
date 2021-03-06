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
package net.smert.frameworkgl.opengl.renderable.shared;

import net.smert.frameworkgl.opengl.MaterialLight;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class MaterialLightPool extends ObjectPool<MaterialLight> {

    @Override
    protected ObjectDestroyer<MaterialLight> createObjectDestroyer() {
        return new ShaderDestroyer();
    }

    private static class ShaderDestroyer extends ObjectDestroyer<MaterialLight> {

        @Override
        public void destroy(MaterialLight materialLight) {
            // Data object contains to resources to destroy
        }

    }

}
