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
package net.smert.jreactphysics3d.framework.opengl.mesh;

import java.util.ArrayList;
import java.util.List;
import net.smert.jreactphysics3d.framework.math.AABB;
import net.smert.jreactphysics3d.framework.math.Vector2f;
import net.smert.jreactphysics3d.framework.math.Vector3f;
import net.smert.jreactphysics3d.framework.math.Vector4f;
import net.smert.jreactphysics3d.framework.opengl.GL;
import net.smert.jreactphysics3d.framework.opengl.constants.Primitives;
import net.smert.jreactphysics3d.framework.opengl.renderable.Renderable;
import net.smert.jreactphysics3d.framework.opengl.renderable.RenderableConfiguration;
import net.smert.jreactphysics3d.framework.utils.Color;
import net.smert.jreactphysics3d.framework.utils.ListUtils;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class Tessellator {

    private final static int INITIAL_SEGMENTS = 16;
    private final static int INITIAL_ELEMENTS_PER_SEGMENT = INITIAL_SEGMENTS * 1024;
    private final static int INITIAL_COMPONENT_ELEMENTS_PER_SEGMENT = INITIAL_ELEMENTS_PER_SEGMENT * 4;

    private boolean convertToTriangles; // Doesn't get reset
    private boolean enableConversionForPrimitiveMode;
    private int elementCount;
    private int maxIndex;
    private int minIndex;
    private int primitiveMode;
    private int vertexIndex;
    private final AABB aabb;
    private final Color color;
    private ConversionState conversionState;
    private final List<Float> colors;
    private final List<Float> normals;
    private final List<Float> texCoords;
    private final List<Float> vertices;
    private final List<Integer> elementCounts;
    private final List<Integer> maxIndexes;
    private final List<Integer> minIndexes;
    private final List<Integer> primitiveModes;
    private final List<Integer> vertexIndexes;
    private final List<Segment> segments;
    private RenderableConfiguration config;
    private final Vector3f normal;
    private final Vector3f normalCreation1;
    private final Vector3f normalCreation2;
    private final Vector3f texCoord;
    private final Vector4f vertex;

    public Tessellator() {
        convertToTriangles = true;
        aabb = new AABB();
        color = new Color();
        colors = new ArrayList<>(INITIAL_COMPONENT_ELEMENTS_PER_SEGMENT); // Up to four per vertex
        normals = new ArrayList<>(INITIAL_COMPONENT_ELEMENTS_PER_SEGMENT);  // Three per vertex
        texCoords = new ArrayList<>(INITIAL_COMPONENT_ELEMENTS_PER_SEGMENT);  // Up to three per vertex
        vertices = new ArrayList<>(INITIAL_COMPONENT_ELEMENTS_PER_SEGMENT);  // Up to four per vertex
        elementCounts = new ArrayList<>(INITIAL_SEGMENTS); // One per segment
        maxIndexes = new ArrayList<>(INITIAL_SEGMENTS); // One per segment
        minIndexes = new ArrayList<>(INITIAL_SEGMENTS); // One per segment
        primitiveModes = new ArrayList<>(INITIAL_SEGMENTS); // One per segment
        vertexIndexes = new ArrayList<>(INITIAL_ELEMENTS_PER_SEGMENT); // One per vertex
        segments = new ArrayList<>(INITIAL_SEGMENTS);
        normal = new Vector3f();
        normalCreation1 = new Vector3f();
        normalCreation2 = new Vector3f();
        texCoord = new Vector3f();
        vertex = new Vector4f();
        reset();
    }

    private void calculateNormal(Vector3f pos1, Vector3f pos2, Vector3f pos3) {
        // CCW order
        normalCreation1.set(pos3).subtract(pos2); // pos2 is base pointing to pos3, index finger
        normalCreation2.set(pos1).subtract(pos2); // pos2 is base pointing to pos1, middle finger
        normal.set(normalCreation1).cross(normalCreation2); // right hand rule, thumb
        normal.normalize();
    }

    private void findAABBMaxMin(Vector4f vertex) {
        aabb.setMax(vertex);
        aabb.setMin(vertex);
    }

    private void internalAddColor(Color color) {
        if (enableConversionForPrimitiveMode) {
            conversionState.addColorConversion(color);
            conversionState.convert(this);
            return;
        }
        internalAddColorToList(color);
    }

    private void internalAddColorToList(Color color) {
        colors.add(color.getR());
        colors.add(color.getG());
        colors.add(color.getB());
        if (config.getColorSize() == 4) {
            colors.add(color.getA());
        }
    }

    private void internalAddNormal(Vector3f normal) {
        if (enableConversionForPrimitiveMode) {
            conversionState.addNormalConversion(normal);
            conversionState.convert(this);
            return;
        }
        internalAddNormalToList(normal);
    }

    private void internalAddNormalToList(Vector3f normal) {
        normals.add(normal.getX());
        normals.add(normal.getY());
        normals.add(normal.getZ());
    }

    private void internalAddTexCoord(Vector3f texCoord) {
        if (enableConversionForPrimitiveMode) {
            conversionState.addTexCoordConversion(texCoord);
            conversionState.convert(this);
            return;
        }
        internalAddTexCoordToList(texCoord);
    }

    private void internalAddTexCoordToList(Vector3f texCoord) {
        texCoords.add(texCoord.getX());
        texCoords.add(texCoord.getY());
        if (config.getTexCoordSize() == 3) {
            texCoords.add(texCoord.getZ());
        }
    }

    private void internalAddVertex(Vector4f vertex) {
        findAABBMaxMin(vertex);
        if (enableConversionForPrimitiveMode) {
            conversionState.addVertexConversion(vertex);
            conversionState.convert(this);
            return;
        }
        internalAddVertexToList(vertex);
    }

    private void internalAddVertexToList(Vector4f vertex) {
        vertices.add(vertex.getX());
        vertices.add(vertex.getY());
        vertices.add(vertex.getZ());
        if (config.getVertexSize() == 4) {
            vertices.add(vertex.getW());
        }
        vertexIndexes.add(vertexIndex);
        elementCount++;
        maxIndex = vertexIndex++;
    }

    public void addColor(float r, float g, float b) {
        color.set(r, g, b, 1.0f);
        internalAddColor(color);
    }

    public void addColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
        internalAddColor(color);
    }

    public void addNormal(float x, float y, float z) {
        normal.set(x, y, z);
        internalAddNormal(normal);
    }

    public void addNormal(Vector3f normal) {
        this.normal.set(normal);
        internalAddNormal(this.normal);
    }

    public void addNormal(Vector3f pos1, Vector3f pos2, Vector3f pos3) {
        calculateNormal(pos1, pos2, pos3);
        internalAddNormal(normal);
    }

    public void addNormalAgain() {
        internalAddNormal(normal);
    }

    public void addTexCoord(float s, float t) {
        texCoord.set(s, t, 0);
        internalAddTexCoord(texCoord);
    }

    public void addTexCoord(float s, float t, float r) {
        config.setTexCoordSize(3);
        texCoord.set(s, t, r);
        internalAddTexCoord(texCoord);
    }

    public void addVertex(float x, float y) {
        vertex.set(x, y, 1.0f, 0.0f);
        internalAddVertex(vertex);
    }

    public void addVertex(float x, float y, float z) {
        vertex.set(x, y, z, 1.0f);
        internalAddVertex(vertex);
    }

    public void addVertex(float x, float y, float z, float w) {
        vertex.set(x, y, z, w);
        internalAddVertex(vertex);
    }

    public void addVertex(Vector2f vertex) {
        this.vertex.set(vertex, 1.0f, 0.0f);
        internalAddVertex(this.vertex);
    }

    public void addVertex(Vector3f vertex) {
        this.vertex.set(vertex, 1.0f);
        internalAddVertex(this.vertex);
    }

    public void addVertex(Vector4f vertex) {
        this.vertex.set(vertex);
        internalAddVertex(this.vertex);
    }

    public Segment createSegment(String name) {
        Segment segment = GL.mf.createSegment();

        segment.setElementCount(elementCount);
        segment.setMaxIndex(maxIndex);
        segment.setMinIndex(minIndex);
        segment.setName(name);
        segment.setPrimitiveMode(primitiveMode);

        if (colors.size() > 0) {
            segment.setColors(getColors());
        }
        if (normals.size() > 0) {
            segment.setNormals(getNormals());
        }
        if (texCoords.size() > 0) {
            segment.setTexCoords(getTexCoords());
        }
        if (vertices.size() > 0) {
            segment.setVertices(getVertices());
        }

        return segment;
    }

    public float[] getColors() {
        return ListUtils.ToPrimitiveFloatArray(colors);
    }

    public float[] getNormals() {
        return ListUtils.ToPrimitiveFloatArray(normals);
    }

    public float[] getTexCoords() {
        return ListUtils.ToPrimitiveFloatArray(texCoords);
    }

    public float[] getVertices() {
        return ListUtils.ToPrimitiveFloatArray(vertices);
    }

    public int getColorsCount() {
        return colors.size();
    }

    public int getElementCount() {
        return elementCount;
    }

    public int getNormalsCount() {
        return normals.size();
    }

    public int getOrAddConfigToPool() {
        RenderableConfiguration newConfig = this.config.clone();
        return Renderable.configPool.getOrAdd(newConfig);
    }

    public int getPrimitiveMode() {
        return primitiveMode;
    }

    public int getTexCoordsCount() {
        return texCoords.size();
    }

    public int getVerticesCount() {
        return vertices.size();
    }

    public int getVertexIndexesCount() {
        return vertexIndexes.size();
    }

    public int[] getElementCounts() {
        return ListUtils.ToPrimitiveIntArray(elementCounts);
    }

    public int[] getMaxIndexes() {
        return ListUtils.ToPrimitiveIntArray(maxIndexes);
    }

    public int[] getMinIndexes() {
        return ListUtils.ToPrimitiveIntArray(minIndexes);
    }

    public int[] getPrimitiveModes() {
        return ListUtils.ToPrimitiveIntArray(primitiveModes);
    }

    public int[] getVertexIndexes() {
        return ListUtils.ToPrimitiveIntArray(vertexIndexes);
    }

    public RenderableConfiguration getRenderableConfiguration() {
        return config;
    }

    public void getAABB(AABB aabb) {
        aabb.getMax().set(this.aabb.getMax());
        aabb.getMin().set(this.aabb.getMin());
    }

    public boolean isConvertToTriangles() {
        return convertToTriangles;
    }

    public void setConvertToTriangles(boolean convertToTriangles) {
        this.convertToTriangles = convertToTriangles;
    }

    public final void reset() {
        enableConversionForPrimitiveMode = false;
        elementCount = 0;
        maxIndex = -1;
        minIndex = -1;
        primitiveMode = -1;
        vertexIndex = 0;
        aabb.setMax(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        aabb.setMin(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        color.setWhite();
        conversionState = null;
        colors.clear();
        normals.clear();
        texCoords.clear();
        vertices.clear();
        elementCounts.clear();
        maxIndexes.clear();
        minIndexes.clear();
        primitiveModes.clear();
        vertexIndexes.clear();
        segments.clear();
        config = GL.mf.createRenderableConfiguration();
        normal.zero();
        normalCreation1.zero();
        normalCreation2.zero();
        texCoord.zero();
        vertex.zero();
    }

    public void setConfig(RenderableConfiguration config) {
        this.config = config;
    }

    public void start(int primitiveMode) {
        start(primitiveMode, false);
    }

    public void start(int primitiveMode, boolean forceConversionForPrimitiveMode) {
        if (convertToTriangles) {
            enableConversionForPrimitiveMode = ((primitiveMode != Primitives.LINES)
                    && (primitiveMode != Primitives.LINE_LOOP) && (primitiveMode != Primitives.LINE_STRIP)
                    && (primitiveMode != Primitives.POINTS) && (primitiveMode != Primitives.POLYGON)
                    && (primitiveMode != Primitives.TRIANGLES));
            conversionState = CreateConversionState(primitiveMode);
        }
        if (forceConversionForPrimitiveMode) {
            enableConversionForPrimitiveMode = true;
        }
        elementCount = 0;
        maxIndex = -1;
        minIndex = vertexIndex; // Save first index
        this.primitiveMode = primitiveMode;
        primitiveModes.add(this.primitiveMode);
    }

    public void stop() {
        if (enableConversionForPrimitiveMode) {
            conversionState = null;
            primitiveMode = Primitives.TRIANGLES;
            primitiveModes.set(primitiveModes.size() - 1, primitiveMode);
            enableConversionForPrimitiveMode = false;
        }
        elementCounts.add(elementCount);
        maxIndexes.add(maxIndex);
        minIndexes.add(minIndex);
    }

    public static ConversionState CreateConversionState(int primitiveMode) {
        return new ConversionState(primitiveMode);
    }

    public static class ConversionState {

        private boolean hasColorsToConvert;
        private boolean hasNormalsToConvert;
        private boolean hasTexCoordsToConvert;
        private int colorConversionCount;
        private int normalConversionCount;
        private final int primitiveMode;
        private int texCoordConversionCount;
        private int vertexConversionCount;
        private final Color color;
        private final Color colorConversion0;
        private final Color colorConversion1;
        private final Color colorConversion2;
        private final Color colorConversion3;
        private final Vector3f normal;
        private final Vector3f normalConversion0;
        private final Vector3f normalConversion1;
        private final Vector3f normalConversion2;
        private final Vector3f normalConversion3;
        private final Vector3f texCoord;
        private final Vector3f texCoordConversion0;
        private final Vector3f texCoordConversion1;
        private final Vector3f texCoordConversion2;
        private final Vector3f texCoordConversion3;
        private final Vector4f vertex;
        private final Vector4f vertexConversion0;
        private final Vector4f vertexConversion1;
        private final Vector4f vertexConversion2;
        private final Vector4f vertexConversion3;

        public ConversionState(int primitiveMode) {
            this.primitiveMode = primitiveMode;
            color = new Color();
            colorConversion0 = new Color();
            colorConversion1 = new Color();
            colorConversion2 = new Color();
            colorConversion3 = new Color();
            normal = new Vector3f();
            normalConversion0 = new Vector3f();
            normalConversion1 = new Vector3f();
            normalConversion2 = new Vector3f();
            normalConversion3 = new Vector3f();
            texCoord = new Vector3f();
            texCoordConversion0 = new Vector3f();
            texCoordConversion1 = new Vector3f();
            texCoordConversion2 = new Vector3f();
            texCoordConversion3 = new Vector3f();
            vertex = new Vector4f();
            vertexConversion0 = new Vector4f();
            vertexConversion1 = new Vector4f();
            vertexConversion2 = new Vector4f();
            vertexConversion3 = new Vector4f();
        }

        private void convertQuad(Tessellator tessellator) {
            if (hasColorsToConvert) {
                tessellator.internalAddColorToList(colorConversion0); // Top right
                tessellator.internalAddColorToList(colorConversion1); // Top left
                tessellator.internalAddColorToList(colorConversion2); // Bottom left
                tessellator.internalAddColorToList(colorConversion3); // Bottom right
            }
            if (hasNormalsToConvert) {
                tessellator.internalAddNormalToList(normalConversion0);
                tessellator.internalAddNormalToList(normalConversion1);
                tessellator.internalAddNormalToList(normalConversion2);
                tessellator.internalAddNormalToList(normalConversion3);
            }
            if (hasTexCoordsToConvert) {
                tessellator.internalAddTexCoordToList(texCoordConversion0);
                tessellator.internalAddTexCoordToList(texCoordConversion1);
                tessellator.internalAddTexCoordToList(texCoordConversion2);
                tessellator.internalAddTexCoordToList(texCoordConversion3);
            }
            tessellator.internalAddVertexToList(vertexConversion0);
            tessellator.internalAddVertexToList(vertexConversion1);
            tessellator.internalAddVertexToList(vertexConversion2);
            tessellator.internalAddVertexToList(vertexConversion3);
        }

        private void convertQuadToTriangles(Tessellator tessellator) {
            if (hasColorsToConvert) {
                tessellator.internalAddColorToList(colorConversion0); // Top right
                tessellator.internalAddColorToList(colorConversion1); // Top left
                tessellator.internalAddColorToList(colorConversion2); // Bottom left
                tessellator.internalAddColorToList(colorConversion2); // Bottom left
                tessellator.internalAddColorToList(colorConversion3); // Bottom right
                tessellator.internalAddColorToList(colorConversion0); // Top right
            }
            if (hasNormalsToConvert) {
                tessellator.internalAddNormalToList(normalConversion0);
                tessellator.internalAddNormalToList(normalConversion1);
                tessellator.internalAddNormalToList(normalConversion2);
                tessellator.internalAddNormalToList(normalConversion2);
                tessellator.internalAddNormalToList(normalConversion3);
                tessellator.internalAddNormalToList(normalConversion0);
            }
            if (hasTexCoordsToConvert) {
                tessellator.internalAddTexCoordToList(texCoordConversion0);
                tessellator.internalAddTexCoordToList(texCoordConversion1);
                tessellator.internalAddTexCoordToList(texCoordConversion2);
                tessellator.internalAddTexCoordToList(texCoordConversion2);
                tessellator.internalAddTexCoordToList(texCoordConversion3);
                tessellator.internalAddTexCoordToList(texCoordConversion0);
            }
            tessellator.internalAddVertexToList(vertexConversion0);
            tessellator.internalAddVertexToList(vertexConversion1);
            tessellator.internalAddVertexToList(vertexConversion2);
            tessellator.internalAddVertexToList(vertexConversion2);
            tessellator.internalAddVertexToList(vertexConversion3);
            tessellator.internalAddVertexToList(vertexConversion0);
        }

        private void convertStripToTriangles(Tessellator tessellator) {
            if (hasColorsToConvert) {
                tessellator.internalAddColorToList(colorConversion3); // Top right
                tessellator.internalAddColorToList(colorConversion1); // Top left
                tessellator.internalAddColorToList(colorConversion0); // Bottom left
                tessellator.internalAddColorToList(colorConversion0); // Bottom left
                tessellator.internalAddColorToList(colorConversion2); // Bottom right
                tessellator.internalAddColorToList(colorConversion3); // Top Right
            }
            if (hasNormalsToConvert) {
                tessellator.internalAddNormalToList(normalConversion3);
                tessellator.internalAddNormalToList(normalConversion1);
                tessellator.internalAddNormalToList(normalConversion0);
                tessellator.internalAddNormalToList(normalConversion0);
                tessellator.internalAddNormalToList(normalConversion2);
                tessellator.internalAddNormalToList(normalConversion3);
            }
            if (hasTexCoordsToConvert) {
                tessellator.internalAddTexCoordToList(texCoordConversion3);
                tessellator.internalAddTexCoordToList(texCoordConversion1);
                tessellator.internalAddTexCoordToList(texCoordConversion0);
                tessellator.internalAddTexCoordToList(texCoordConversion0);
                tessellator.internalAddTexCoordToList(texCoordConversion2);
                tessellator.internalAddTexCoordToList(texCoordConversion3);
            }
            tessellator.internalAddVertexToList(vertexConversion3);
            tessellator.internalAddVertexToList(vertexConversion1);
            tessellator.internalAddVertexToList(vertexConversion0);
            tessellator.internalAddVertexToList(vertexConversion0);
            tessellator.internalAddVertexToList(vertexConversion2);
            tessellator.internalAddVertexToList(vertexConversion3);
        }

        private void convertTriangle(Tessellator tessellator) {
            if (hasColorsToConvert) {
                tessellator.internalAddColorToList(colorConversion0); // Center
                tessellator.internalAddColorToList(colorConversion1); // Bottom right
                tessellator.internalAddColorToList(colorConversion2); // Bottom left
            }
            if (hasNormalsToConvert) {
                tessellator.internalAddNormalToList(normalConversion0);
                tessellator.internalAddNormalToList(normalConversion1);
                tessellator.internalAddNormalToList(normalConversion2);
            }
            if (hasTexCoordsToConvert) {
                tessellator.internalAddTexCoordToList(texCoordConversion0);
                tessellator.internalAddTexCoordToList(texCoordConversion1);
                tessellator.internalAddTexCoordToList(texCoordConversion2);
            }
            tessellator.internalAddVertexToList(vertexConversion0);
            tessellator.internalAddVertexToList(vertexConversion1);
            tessellator.internalAddVertexToList(vertexConversion2);
        }

        private boolean hasIncompatiblePrimitiveMode(Tessellator tessellator) {
            return ((tessellator.primitiveMode == Primitives.LINES)
                    || (tessellator.primitiveMode == Primitives.LINE_LOOP)
                    || (tessellator.primitiveMode == Primitives.LINE_STRIP)
                    || (tessellator.primitiveMode == Primitives.POINTS)
                    || (tessellator.primitiveMode == Primitives.POLYGON));
        }

        public void addColorConversion(Color color) {
            if (colorConversionCount == 0) {
                colorConversion0.set(color);
            } else if (colorConversionCount == 1) {
                colorConversion1.set(color);
            } else if (colorConversionCount == 2) {
                colorConversion2.set(color);
            } else if (colorConversionCount == 3) {
                colorConversion3.set(color);
            }
            hasColorsToConvert = true;
            colorConversionCount++;
        }

        public void addNormalConversion(Vector3f normal) {
            if (normalConversionCount == 0) {
                normalConversion0.set(normal);
            } else if (normalConversionCount == 1) {
                normalConversion1.set(normal);
            } else if (normalConversionCount == 2) {
                normalConversion2.set(normal);
            } else if (normalConversionCount == 3) {
                normalConversion3.set(normal);
            }
            hasNormalsToConvert = true;
            normalConversionCount++;
        }

        public void addTexCoordConversion(Vector3f texCoord) {
            if (texCoordConversionCount == 0) {
                texCoordConversion0.set(texCoord);
            } else if (texCoordConversionCount == 1) {
                texCoordConversion1.set(texCoord);
            } else if (texCoordConversionCount == 2) {
                texCoordConversion2.set(texCoord);
            } else if (texCoordConversionCount == 3) {
                texCoordConversion3.set(texCoord);
            }
            hasTexCoordsToConvert = true;
            texCoordConversionCount++;
        }

        public void addQuad(Tessellator tessellator) {
            if (hasIncompatiblePrimitiveMode(tessellator)) {
                throw new IllegalStateException("You cannot add a quad in the primitive mode: "
                        + tessellator.primitiveMode);
            }
            if ((!tessellator.enableConversionForPrimitiveMode) && (tessellator.primitiveMode != Primitives.QUADS)) {
                throw new IllegalStateException("You cannot add a quad in the primitive mode: "
                        + tessellator.primitiveMode);
            }
            if (vertexConversionCount != 4) {
                throw new IllegalStateException("You must have four vertices to add a quad");
            }
            if (tessellator.enableConversionForPrimitiveMode) {
                convertQuadToTriangles(tessellator);
            } else {
                convertQuad(tessellator);
            }
        }

        public void addTriangle(Tessellator tessellator) {
            if (hasIncompatiblePrimitiveMode(tessellator)) {
                throw new IllegalStateException("You cannot add a triangle in the primitive mode: "
                        + tessellator.primitiveMode);
            }
            if ((!tessellator.enableConversionForPrimitiveMode) && (tessellator.primitiveMode != Primitives.TRIANGLES)) {
                throw new IllegalStateException("You cannot add a triangle in the primitive mode: "
                        + tessellator.primitiveMode);
            }
            if (vertexConversionCount != 3) {
                throw new IllegalStateException("You must have three vertices to add a quad");
            }
            convertTriangle(tessellator);
        }

        public void addVertexConversion(Vector4f vertex) {
            if (vertexConversionCount == 0) {
                vertexConversion0.set(vertex);
            } else if (vertexConversionCount == 1) {
                vertexConversion1.set(vertex);
            } else if (vertexConversionCount == 2) {
                vertexConversion2.set(vertex);
            } else if (vertexConversionCount == 3) {
                vertexConversion3.set(vertex);
            }
            vertexConversionCount++;
        }

        public void convert(Tessellator tessellator) {
            if (hasIncompatiblePrimitiveMode(tessellator)) {
                throw new IllegalStateException("You cannot convert in the primitive mode: "
                        + tessellator.primitiveMode);
            }
            if ((tessellator.enableConversionForPrimitiveMode) || (tessellator.primitiveMode == Primitives.TRIANGLES)) {
                throw new IllegalStateException("You cannot convert if the Tessellator does not have it enabled");
            }
            switch (primitiveMode) {
                case Primitives.QUADS:
                    if (vertexConversionCount == 4) {
                        convertQuadToTriangles(tessellator);
                        colorConversionCount = 0;
                        normalConversionCount = 0;
                        texCoordConversionCount = 0;
                        vertexConversionCount = 0;
                    }
                    break;

                case Primitives.QUAD_STRIP:
                case Primitives.TRIANGLE_STRIP:
                    if (vertexConversionCount == 4) {
                        convertStripToTriangles(tessellator);
                        colorConversion0.set(colorConversion2);
                        colorConversion1.set(colorConversion3);
                        normalConversion0.set(normalConversion2);
                        normalConversion1.set(normalConversion3);
                        texCoordConversion0.set(texCoordConversion2);
                        texCoordConversion1.set(texCoordConversion3);
                        vertexConversion0.set(vertexConversion2);
                        vertexConversion1.set(vertexConversion3);
                        colorConversionCount = 2;
                        normalConversionCount = 2;
                        texCoordConversionCount = 2;
                        vertexConversionCount = 2;
                    }
                    break;

                case Primitives.TRIANGLE_FAN:
                    if (vertexConversionCount == 3) {
                        convertTriangle(tessellator);
                        colorConversion1.set(colorConversion2);
                        normalConversion1.set(normalConversion2);
                        texCoordConversion1.set(texCoordConversion2);
                        vertexConversion1.set(vertexConversion2);
                        colorConversionCount = 2;
                        normalConversionCount = 2;
                        texCoordConversionCount = 2;
                        vertexConversionCount = 2;
                    }
                    break;

                default:
                    throw new IllegalStateException("Unknown primitive mode: " + primitiveMode + " for conversion");
            }
        }

        public Color getColor() {
            return color;
        }

        public Vector3f getNormal() {
            return normal;
        }

        public Vector3f getTexCoord() {
            return texCoord;
        }

        public Vector4f getVertex() {
            return vertex;
        }

        public void reset() {
            hasColorsToConvert = false;
            hasNormalsToConvert = false;
            hasTexCoordsToConvert = false;
            colorConversionCount = 0;
            normalConversionCount = 0;
            texCoordConversionCount = 0;
            vertexConversionCount = 0;
            color.setWhite();
            colorConversion0.setWhite();
            colorConversion1.setWhite();
            colorConversion2.setWhite();
            colorConversion3.setWhite();
            normal.zero();
            normalConversion0.zero();
            normalConversion1.zero();
            normalConversion2.zero();
            normalConversion3.zero();
            texCoord.zero();
            texCoordConversion0.zero();
            texCoordConversion1.zero();
            texCoordConversion2.zero();
            texCoordConversion3.zero();
            vertex.zero();
            vertexConversion0.zero();
            vertexConversion1.zero();
            vertexConversion2.zero();
            vertexConversion3.zero();
        }

    }

}
