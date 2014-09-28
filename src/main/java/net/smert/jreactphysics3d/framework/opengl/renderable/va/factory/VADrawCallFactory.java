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
package net.smert.jreactphysics3d.framework.opengl.renderable.va.factory;

import net.smert.jreactphysics3d.framework.opengl.renderable.va.VADrawArrays;
import net.smert.jreactphysics3d.framework.opengl.renderable.va.VADrawElements;
import org.picocontainer.MutablePicoContainer;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class VADrawCallFactory {

    private final MutablePicoContainer container;

    public VADrawCallFactory(MutablePicoContainer vaDrawCallFactoryContainer) {
        container = vaDrawCallFactoryContainer;
    }

    public VADrawArrays createDrawArrays() {
        return container.getComponent(VADrawArrays.class);
    }

    public VADrawElements createDrawElements() {
        return container.getComponent(VADrawElements.class);
    }

}