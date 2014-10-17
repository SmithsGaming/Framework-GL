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
package net.smert.frameworkgl.opengl.renderable.factory;

import net.smert.frameworkgl.opengl.renderable.gl2.VertexArrayBindStrategyGL2;
import net.smert.frameworkgl.opengl.renderable.gl2.VertexBufferObjectBindStrategyGL2;
import net.smert.frameworkgl.opengl.renderable.shared.VertexArrayRenderable;
import net.smert.frameworkgl.opengl.renderable.shared.VertexBufferObjectDynamicInterleavedRenderable;
import net.smert.frameworkgl.opengl.renderable.shared.VertexBufferObjectDynamicNonInterleavedRenderable;
import net.smert.frameworkgl.opengl.renderable.shared.VertexBufferObjectInterleavedRenderable;
import net.smert.frameworkgl.opengl.renderable.shared.VertexBufferObjectNonInterleavedRenderable;
import org.picocontainer.MutablePicoContainer;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class RenderableFactoryGL2 implements RenderableFactory {

    private final MutablePicoContainer container;
    private final VertexArrayBindStrategyGL2 vertexArrayBindStrategy;
    private final VertexBufferObjectBindStrategyGL2 vertexBufferObjectBindStrategy;

    public RenderableFactoryGL2(MutablePicoContainer renderableFactoryGL2Container,
            VertexArrayBindStrategyGL2 vertexArrayBindStrategy,
            VertexBufferObjectBindStrategyGL2 vertexBufferObjectBindStrategy) {
        container = renderableFactoryGL2Container;
        this.vertexArrayBindStrategy = vertexArrayBindStrategy;
        this.vertexBufferObjectBindStrategy = vertexBufferObjectBindStrategy;
    }

    @Override
    public VertexArrayRenderable createArrayRenderable() {
        VertexArrayRenderable vertexArrayRenderable = container.getComponent(VertexArrayRenderable.class);
        vertexArrayRenderable.setBind(vertexArrayBindStrategy);
        return vertexArrayRenderable;
    }

    @Override
    public VertexBufferObjectDynamicInterleavedRenderable createDynamicInterleavedRenderable() {
        VertexBufferObjectDynamicInterleavedRenderable vboDynamicInterleavedRenderable
                = container.getComponent(VertexBufferObjectDynamicInterleavedRenderable.class);
        vboDynamicInterleavedRenderable.setBind(vertexBufferObjectBindStrategy);
        return vboDynamicInterleavedRenderable;
    }

    @Override
    public VertexBufferObjectDynamicNonInterleavedRenderable createDynamicNonInterleavedRenderable() {
        VertexBufferObjectDynamicNonInterleavedRenderable vboDynamicNonInterleavedRenderable
                = container.getComponent(VertexBufferObjectDynamicNonInterleavedRenderable.class);
        vboDynamicNonInterleavedRenderable.setBind(vertexBufferObjectBindStrategy);
        return vboDynamicNonInterleavedRenderable;
    }

    @Override
    public VertexBufferObjectInterleavedRenderable createInterleavedRenderable() {
        VertexBufferObjectInterleavedRenderable vboInterleavedRenderable
                = container.getComponent(VertexBufferObjectInterleavedRenderable.class);
        vboInterleavedRenderable.setBind(vertexBufferObjectBindStrategy);
        return vboInterleavedRenderable;
    }

    @Override
    public VertexBufferObjectNonInterleavedRenderable createNonInterleavedRenderable() {
        VertexBufferObjectNonInterleavedRenderable vboNonInterleavedRenderable
                = container.getComponent(VertexBufferObjectNonInterleavedRenderable.class);
        vboNonInterleavedRenderable.setBind(vertexBufferObjectBindStrategy);
        return vboNonInterleavedRenderable;
    }

}