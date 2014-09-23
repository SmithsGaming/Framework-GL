package net.smert.jreactphysics3d.framework;

import net.smert.jreactphysics3d.framework.opengl.GL;
import net.smert.jreactphysics3d.framework.opengl.mesh.Mesh;
import net.smert.jreactphysics3d.framework.opengl.renderable.AbstractRenderable;
import net.smert.jreactphysics3d.framework.opengl.renderable.factory.RenderableFactoryGL1;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class Graphics {

    public final RenderableFactoryGL1 renderableFactoryGL1;

    public Graphics() {
        renderableFactoryGL1 = new RenderableFactoryGL1();
    }

    public AbstractRenderable createDisplayListRenderable(Mesh mesh) {
        return renderableFactoryGL1.createDisplayList(mesh);
    }

    public AbstractRenderable createImmediateModeRenderable(Mesh mesh) {
        return renderableFactoryGL1.createImmediateMode(mesh);
    }

    public AbstractRenderable createVertexBufferObjectRenderable(Mesh mesh) {
        return renderableFactoryGL1.createVertexBufferObject(mesh);
    }

    public AbstractRenderable createVertexBufferObjectInterleavedRenderable(Mesh mesh) {
        return renderableFactoryGL1.createVertexBufferObjectInterleaved(mesh);
    }

    /**
     * This method should be called just before the Display is destroyed. This is automatically called in Application
     * during the normal shutdown process.
     */
    public void destroy() {
        RenderableFactoryGL1.Destroy();
        GL.vboHelper.unbind();
    }

    public void render(AbstractRenderable renderable, float x, float y, float z) {
        GL.o1.pushMatrix();
        GL.o1.translate(x, y, z);
        renderable.render();
        GL.o1.popMatrix();
    }

}
