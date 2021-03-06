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
package net.smert.frameworkgl.opengl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class FrameBufferObject {

    private final static Logger log = LoggerFactory.getLogger(FrameBufferObject.class);

    private int fboID;

    public FrameBufferObject() {
        fboID = 0;
    }

    public void create() {
        destroy();
        fboID = GL.fboHelper.create();
        log.debug("Created a new FBO with ID: {}", fboID);
    }

    public void destroy() {
        if (fboID == 0) {
            return;
        }
        GL.fboHelper.delete(fboID);
        log.debug("Deleted a FBO with ID: {}", fboID);
        fboID = 0;
    }

    public int getFboID() {
        return fboID;
    }

}
