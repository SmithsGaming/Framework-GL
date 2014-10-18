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
package net.smert.frameworkgl.opengl.shader.vertexlit.single;

import java.io.IOException;
import net.smert.frameworkgl.Fw;
import net.smert.frameworkgl.opengl.Shader;
import net.smert.frameworkgl.opengl.shader.AbstractShader;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class BlinnPhongSpecularDirectional extends AbstractShader {

    private final BlinnPhongSpecularUniforms uniforms;

    public BlinnPhongSpecularDirectional(BlinnPhongSpecularUniforms uniforms, Shader shader) {
        super(uniforms, shader);
        this.uniforms = uniforms;
    }

    public BlinnPhongSpecularUniforms getUniforms() {
        return uniforms;
    }

    public static class Factory {

        public static BlinnPhongSpecularDirectional Create() throws IOException {
            Shader shader = Fw.graphics.buildShader(
                    "vertexlit/single/blinn_phong_specular_directional.fsh",
                    "vertexlit/single/blinn_phong_specular_directional.vsh",
                    "vertexLitSingleBlinnPhongSpecularDirectional");
            return new BlinnPhongSpecularDirectional(new BlinnPhongSpecularUniforms(shader.getProgramID()), shader);
        }

    }

}
