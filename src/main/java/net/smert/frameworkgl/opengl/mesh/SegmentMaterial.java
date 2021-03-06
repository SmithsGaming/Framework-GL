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
package net.smert.frameworkgl.opengl.mesh;

import java.util.HashMap;
import java.util.Map;
import net.smert.frameworkgl.opengl.TextureType;
import net.smert.frameworkgl.utils.Color;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class SegmentMaterial {

    private Color color;
    private final Map<TextureType, String> textureTypeToFilename;
    private String materialLightName;

    public SegmentMaterial() {
        textureTypeToFilename = new HashMap<>();
        materialLightName = "default";
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Map<TextureType, String> getTextures() {
        return textureTypeToFilename;
    }

    public String getMaterialLightName() {
        return materialLightName;
    }

    public void setMaterialLightName(String materialLightName) {
        this.materialLightName = materialLightName;
    }

    public String getTexture(TextureType textureType) {
        return textureTypeToFilename.get(textureType);
    }

    public String setTexture(TextureType textureType, String filename) {
        return textureTypeToFilename.put(textureType, filename);
    }

}
