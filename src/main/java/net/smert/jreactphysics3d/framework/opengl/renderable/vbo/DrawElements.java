package net.smert.jreactphysics3d.framework.opengl.renderable.vbo;

import net.smert.jreactphysics3d.framework.opengl.GL;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class DrawElements extends AbstractDrawCall {

    @Override
    public void render() {
        for (int i = 0, max = renderModes.length; i < max; i++) {
            GL.vboHelper.drawElements(renderModes[i], elementCounts[i], elementType);
        }
    }

}