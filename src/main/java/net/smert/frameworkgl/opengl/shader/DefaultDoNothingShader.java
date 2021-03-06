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
package net.smert.frameworkgl.opengl.shader;

import java.nio.FloatBuffer;
import net.smert.frameworkgl.gameobjects.GameObject;
import net.smert.frameworkgl.opengl.Shader;
import net.smert.frameworkgl.opengl.TextureType;
import net.smert.frameworkgl.opengl.constants.TextureUnit;
import net.smert.frameworkgl.opengl.mesh.Segment;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class DefaultDoNothingShader extends AbstractShader {

    public DefaultDoNothingShader() {
        super(new DefaultShaderUniforms(0), new Shader());
        setTextureUnit(TextureType.BUMP, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.DETAIL, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.DIFFUSE, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.DISPLACEMENT, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.ENVIRONMENT, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.GLOW, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.HEIGHT, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.NORMAL, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.PARALLAX, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.TRANSLUCENT, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.TRANSPARENCY, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.AMBIENT_OCCLUSION, TextureUnit.TEXTURE1);
        setTextureUnit(TextureType.SPECULAR, TextureUnit.TEXTURE2);
        setTextureUnit(TextureType.SPECULAR_EXPONENT, TextureUnit.TEXTURE3);

        setTextureUnit(TextureType.TEXTURE0, TextureUnit.TEXTURE0);
        setTextureUnit(TextureType.TEXTURE1, TextureUnit.TEXTURE1);
        setTextureUnit(TextureType.TEXTURE2, TextureUnit.TEXTURE2);
        setTextureUnit(TextureType.TEXTURE3, TextureUnit.TEXTURE3);
        setTextureUnit(TextureType.TEXTURE4, TextureUnit.TEXTURE4);
        setTextureUnit(TextureType.TEXTURE5, TextureUnit.TEXTURE5);
        setTextureUnit(TextureType.TEXTURE6, TextureUnit.TEXTURE6);
        setTextureUnit(TextureType.TEXTURE7, TextureUnit.TEXTURE7);
    }

    @Override
    public void bind() {
    }

    @Override
    public void sendUniformMatrices(FloatBuffer matrixFloatBuffer) {
    }

    @Override
    public void sendUniformsOncePerBind(FloatBuffer matrixFloatBuffer) {
    }

    @Override
    public void sendUniformsOncePerGameObject(FloatBuffer matrixFloatBuffer, GameObject gameObject) {
    }

    @Override
    public void sendUniformsOncePerRenderCall(FloatBuffer matrixFloatBuffer, Segment segment) {
    }

    @Override
    public void sendUniformTextureFlag(float flag) {
    }

    @Override
    public void unbind() {
    }

}
