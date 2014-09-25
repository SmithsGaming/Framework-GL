package net.smert.jreactphysics3d.framework.opengl.renderable.va;

import java.nio.ByteBuffer;
import net.smert.jreactphysics3d.framework.opengl.VertexArray;
import net.smert.jreactphysics3d.framework.opengl.renderable.shared.MultipleBuffers;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class VertexArrays implements MultipleBuffers {

    public VertexArray color;
    public VertexArray normal;
    public VertexArray texCoord;
    public VertexArray vertex;
    public VertexArray vertexIndex;

    @Override
    public void createColor(int bufferSize) {
        color = new VertexArray();
        color.create(bufferSize);
    }

    @Override
    public void createInterleaved(int bufferSize) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void createNormal(int bufferSize) {
        normal = new VertexArray();
        normal.create(bufferSize);
    }

    @Override
    public void createTexCoord(int bufferSize) {
        texCoord = new VertexArray();
        texCoord.create(bufferSize);
    }

    @Override
    public void createVertex(int bufferSize) {
        vertex = new VertexArray();
        vertex.create(bufferSize);
    }

    @Override
    public void createVertexIndex(int bufferSize) {
        vertexIndex = new VertexArray();
        vertexIndex.create(bufferSize);
    }

    @Override
    public ByteBuffer getColor() {
        return color.getByteBuffer();
    }

    @Override
    public ByteBuffer getInterleaved() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public ByteBuffer getNormal() {
        return normal.getByteBuffer();
    }

    @Override
    public ByteBuffer getTexCoord() {
        return texCoord.getByteBuffer();
    }

    @Override
    public ByteBuffer getVertex() {
        return vertex.getByteBuffer();
    }

    @Override
    public ByteBuffer getVertexIndex() {
        return vertexIndex.getByteBuffer();
    }

    @Override
    public void setInterleavedBufferToOthers() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public VertexArray getColorVertexArray() {
        return color;
    }

    public VertexArray getNormalVertexArray() {
        return normal;
    }

    public VertexArray getTexCoordVertexArray() {
        return texCoord;
    }

    public VertexArray getVertexVertexArray() {
        return vertex;
    }

    public VertexArray getVertexIndexVertexArray() {
        return vertexIndex;
    }

}