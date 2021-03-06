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
package net.smert.frameworkgl.opengl.shader.basic;

import java.io.IOException;
import net.smert.frameworkgl.Fw;
import net.smert.frameworkgl.opengl.Shader;
import net.smert.frameworkgl.opengl.shader.AbstractShader;
import net.smert.frameworkgl.opengl.shader.DefaultShaderUniforms;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class ColorShader extends AbstractShader {

    private final Uniforms uniforms;

    public ColorShader(Uniforms uniforms, Shader shader) {
        super(uniforms, shader);
        this.uniforms = uniforms;
    }

    public Uniforms getUniforms() {
        return uniforms;
    }

    public static class Factory {

        public static ColorShader Create() throws IOException {
            Shader shader = Fw.graphics.buildShader("basic/color.fsh", "basic/color.vsh", "color");
            return new ColorShader(new Uniforms(shader.getProgramID()), shader);
        }

    }

    public static class Uniforms extends DefaultShaderUniforms {

        public Uniforms(int programID) {
            super(programID);
        }

    }

}
