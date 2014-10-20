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

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public abstract class AbstractRenderCall implements RenderCall {

    protected TextureTypeMapping[][] textureTypeMappings;

    public TextureTypeMapping[][] getTextureTypeMappings() {
        return textureTypeMappings;
    }

    public void setTextureTypeMappings(TextureTypeMapping[][] textureTypeMappings) {
        this.textureTypeMappings = textureTypeMappings;
    }

    public static class TextureTypeMapping {

        private final float textureFlag;
        private final int textureTypeID;
        private final int uniqueTextureID;

        public TextureTypeMapping(float textureFlag, int textureTypeID, int uniqueTextureID) {
            this.textureFlag = textureFlag;
            this.textureTypeID = textureTypeID;
            this.uniqueTextureID = uniqueTextureID;
        }

        public float getTextureFlag() {
            return textureFlag;
        }

        public int getTextureTypeID() {
            return textureTypeID;
        }

        public int getUniqueTextureID() {
            return uniqueTextureID;
        }

    }

}
