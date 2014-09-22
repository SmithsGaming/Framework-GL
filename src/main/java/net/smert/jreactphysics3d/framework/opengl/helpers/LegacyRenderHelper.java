package net.smert.jreactphysics3d.framework.opengl.helpers;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class LegacyRenderHelper {

    public void begin(int primitive) {
        GL11.glBegin(primitive);
    }

    public void color(float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
    }

    public void end() {
        GL11.glEnd();
    }

    public void normal(float x, float y, float z) {
        GL11.glNormal3f(x, y, z);
    }

    public void texCoord(float s, float t) {
        GL11.glTexCoord2f(s, t);
    }

    public void vertex(float x, float y, float z) {
        GL11.glVertex3f(x, y, z);
    }

}