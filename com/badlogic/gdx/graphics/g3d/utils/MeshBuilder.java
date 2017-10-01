/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ShortArray;

public class MeshBuilder
implements MeshPartBuilder {
    private static final ShortArray tmpIndices = new ShortArray();
    private static final FloatArray tmpVertices = new FloatArray();
    private final MeshPartBuilder.VertexInfo vertTmp1 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp2 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp3 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp4 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp5 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp6 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp7 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp8 = new MeshPartBuilder.VertexInfo();
    private final Matrix4 matTmp1 = new Matrix4();
    private final Vector3 tempV1 = new Vector3();
    private final Vector3 tempV2 = new Vector3();
    private final Vector3 tempV3 = new Vector3();
    private final Vector3 tempV4 = new Vector3();
    private final Vector3 tempV5 = new Vector3();
    private final Vector3 tempV6 = new Vector3();
    private final Vector3 tempV7 = new Vector3();
    private final Vector3 tempV8 = new Vector3();
    private final Color tempC1 = new Color();
    private VertexAttributes attributes;
    private FloatArray vertices = new FloatArray();
    private ShortArray indices = new ShortArray();
    private int stride;
    private short vindex;
    private int istart;
    private int posOffset;
    private int posSize;
    private int norOffset;
    private int colOffset;
    private int colSize;
    private int cpOffset;
    private int uvOffset;
    private MeshPart part;
    private Array<MeshPart> parts = new Array();
    private final Color color = new Color(Color.WHITE);
    private boolean hasColor = false;
    private int primitiveType;
    private float uOffset = 0.0f;
    private float uScale = 1.0f;
    private float vOffset = 0.0f;
    private float vScale = 1.0f;
    private boolean hasUVTransform = false;
    private float[] vertex;
    private boolean vertexTransformationEnabled = false;
    private final Matrix4 positionTransform = new Matrix4();
    private final Matrix3 normalTransform = new Matrix3();
    private static final Pool<Vector3> vectorPool = new Pool<Vector3>(){

        @Override
        protected Vector3 newObject() {
            return new Vector3();
        }
    };
    private static final Array<Vector3> vectorArray = new Array();
    private static final Pool<Matrix4> matrices4Pool = new Pool<Matrix4>(){

        @Override
        protected Matrix4 newObject() {
            return new Matrix4();
        }
    };
    private static final Array<Matrix4> matrices4Array = new Array();
    private short lastIndex = -1;
    private static final Vector3 vTmp = new Vector3();
    private final Vector3 tmpNormal = new Vector3();
    private static IntIntMap indicesMap = null;

    public static VertexAttributes createAttributes(long usage) {
        Array<VertexAttribute> attrs = new Array<VertexAttribute>();
        if ((usage & 1) == 1) {
            attrs.add(new VertexAttribute(1, 3, "a_position"));
        }
        if ((usage & 2) == 2) {
            attrs.add(new VertexAttribute(2, 4, "a_color"));
        }
        if ((usage & 4) == 4) {
            attrs.add(new VertexAttribute(4, 4, "a_color"));
        }
        if ((usage & 8) == 8) {
            attrs.add(new VertexAttribute(8, 3, "a_normal"));
        }
        if ((usage & 16) == 16) {
            attrs.add(new VertexAttribute(16, 2, "a_texCoord0"));
        }
        VertexAttribute[] attributes = new VertexAttribute[attrs.size];
        for (int i = 0; i < attributes.length; ++i) {
            attributes[i] = (VertexAttribute)attrs.get(i);
        }
        return new VertexAttributes(attributes);
    }

    public void begin(long attributes) {
        this.begin(MeshBuilder.createAttributes(attributes), -1);
    }

    public void begin(VertexAttributes attributes) {
        this.begin(attributes, -1);
    }

    public void begin(long attributes, int primitiveType) {
        this.begin(MeshBuilder.createAttributes(attributes), primitiveType);
    }

    public void begin(VertexAttributes attributes, int primitiveType) {
        if (this.attributes != null) {
            throw new RuntimeException("Call end() first");
        }
        this.attributes = attributes;
        this.vertices.clear();
        this.indices.clear();
        this.parts.clear();
        this.vindex = 0;
        this.istart = 0;
        this.part = null;
        this.stride = attributes.vertexSize / 4;
        this.vertex = new float[this.stride];
        VertexAttribute a = attributes.findByUsage(1);
        if (a == null) {
            throw new GdxRuntimeException("Cannot build mesh without position attribute");
        }
        this.posOffset = a.offset / 4;
        this.posSize = a.numComponents;
        a = attributes.findByUsage(8);
        this.norOffset = a == null ? -1 : a.offset / 4;
        a = attributes.findByUsage(2);
        this.colOffset = a == null ? -1 : a.offset / 4;
        this.colSize = a == null ? 0 : a.numComponents;
        a = attributes.findByUsage(4);
        this.cpOffset = a == null ? -1 : a.offset / 4;
        a = attributes.findByUsage(16);
        this.uvOffset = a == null ? -1 : a.offset / 4;
        this.setColor(null);
        this.setVertexTransform(null);
        this.setUVRange(null);
        this.primitiveType = primitiveType;
    }

    private void endpart() {
        if (this.part != null) {
            this.part.indexOffset = this.istart;
            this.part.numVertices = this.indices.size - this.istart;
            this.istart = this.indices.size;
            this.part = null;
        }
    }

    public MeshPart part(String id, int primitiveType) {
        if (this.attributes == null) {
            throw new RuntimeException("Call begin() first");
        }
        this.endpart();
        this.part = new MeshPart();
        this.part.id = id;
        this.primitiveType = this.part.primitiveType = primitiveType;
        this.parts.add(this.part);
        this.setColor(null);
        this.setVertexTransform(null);
        this.setUVRange(null);
        return this.part;
    }

    public Mesh end(Mesh mesh) {
        this.endpart();
        if (this.attributes == null) {
            throw new GdxRuntimeException("Call begin() first");
        }
        if (!this.attributes.equals(mesh.getVertexAttributes())) {
            throw new GdxRuntimeException("Mesh attributes don't match");
        }
        if (mesh.getMaxVertices() * this.stride < this.vertices.size) {
            throw new GdxRuntimeException("Mesh can't hold enough vertices: " + mesh.getMaxVertices() + " * " + this.stride + " < " + this.vertices.size);
        }
        if (mesh.getMaxIndices() < this.indices.size) {
            throw new GdxRuntimeException("Mesh can't hold enough indices: " + mesh.getMaxIndices() + " < " + this.indices.size);
        }
        mesh.setVertices(this.vertices.items, 0, this.vertices.size);
        mesh.setIndices(this.indices.items, 0, this.indices.size);
        for (MeshPart p : this.parts) {
            p.mesh = mesh;
        }
        this.parts.clear();
        this.attributes = null;
        this.vertices.clear();
        this.indices.clear();
        return mesh;
    }

    public Mesh end() {
        return this.end(new Mesh(true, this.vertices.size / this.stride, this.indices.size, this.attributes));
    }

    public int getNumVertices() {
        return this.vertices.size / this.stride;
    }

    public int getNumIndices() {
        return this.indices.size;
    }

    @Override
    public VertexAttributes getAttributes() {
        return this.attributes;
    }

    @Override
    public MeshPart getMeshPart() {
        return this.part;
    }

    private Vector3 tmp(float x, float y, float z) {
        Vector3 result = vectorPool.obtain().set(x, y, z);
        vectorArray.add(result);
        return result;
    }

    private Vector3 tmp(Vector3 copyFrom) {
        return this.tmp(copyFrom.x, copyFrom.y, copyFrom.z);
    }

    private Matrix4 tmp() {
        Matrix4 result = matrices4Pool.obtain().idt();
        matrices4Array.add(result);
        return result;
    }

    private Matrix4 tmp(Matrix4 copyFrom) {
        return this.tmp().set(copyFrom);
    }

    private void cleanup() {
        vectorPool.freeAll(vectorArray);
        vectorArray.clear();
        matrices4Pool.freeAll(matrices4Array);
        matrices4Array.clear();
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
        this.hasColor = !this.color.equals(Color.WHITE);
    }

    @Override
    public void setColor(Color color) {
        this.hasColor = color != null;
        this.color.set(!this.hasColor ? Color.WHITE : color);
    }

    @Override
    public void setUVRange(float u1, float v1, float u2, float v2) {
        this.uOffset = u1;
        this.vOffset = v1;
        this.uScale = u2 - u1;
        this.vScale = v2 - v1;
        this.hasUVTransform = !MathUtils.isZero(u1) || !MathUtils.isZero(v1) || !MathUtils.isEqual(u2, 1.0f) || !MathUtils.isEqual(v2, 1.0f);
    }

    @Override
    public void setUVRange(TextureRegion region) {
        this.hasUVTransform = region != null;
        if (!this.hasUVTransform) {
            this.vOffset = 0.0f;
            this.uOffset = 0.0f;
            this.vScale = 1.0f;
            this.uScale = 1.0f;
        } else {
            this.setUVRange(region.getU(), region.getV(), region.getU2(), region.getV2());
        }
    }

    @Override
    public Matrix4 getVertexTransform(Matrix4 out) {
        return out.set(this.positionTransform);
    }

    @Override
    public void setVertexTransform(Matrix4 transform) {
        this.vertexTransformationEnabled = transform != null;
        if (this.vertexTransformationEnabled) {
            this.positionTransform.set(transform);
            this.normalTransform.set(transform).inv().transpose();
        } else {
            this.positionTransform.idt();
            this.normalTransform.idt();
        }
    }

    @Override
    public boolean isVertexTransformationEnabled() {
        return this.vertexTransformationEnabled;
    }

    @Override
    public void setVertexTransformationEnabled(boolean enabled) {
        this.vertexTransformationEnabled = enabled;
    }

    public void ensureVertices(int numVertices) {
        this.vertices.ensureCapacity(this.vertex.length * numVertices);
    }

    public void ensureIndices(int numIndices) {
        this.indices.ensureCapacity(numIndices);
    }

    public void ensureCapacity(int numVertices, int numIndices) {
        this.ensureVertices(numVertices);
        this.ensureIndices(numIndices);
    }

    public void ensureTriangleIndices(int numTriangles) {
        if (this.primitiveType == 1) {
            this.ensureIndices(6 * numTriangles);
        } else if (this.primitiveType == 4 || this.primitiveType == 0) {
            this.ensureIndices(3 * numTriangles);
        } else {
            throw new GdxRuntimeException("Incorrect primtive type");
        }
    }

    public void ensureTriangles(int numVertices, int numTriangles) {
        this.ensureVertices(numVertices);
        this.ensureTriangleIndices(numTriangles);
    }

    public void ensureTriangles(int numTriangles) {
        this.ensureTriangles(3 * numTriangles, numTriangles);
    }

    public void ensureRectangleIndices(int numRectangles) {
        if (this.primitiveType == 0) {
            this.ensureIndices(4 * numRectangles);
        } else if (this.primitiveType == 1) {
            this.ensureIndices(8 * numRectangles);
        } else {
            this.ensureIndices(6 * numRectangles);
        }
    }

    public void ensureRectangles(int numVertices, int numRectangles) {
        this.ensureVertices(numVertices);
        this.ensureRectangleIndices(numRectangles);
    }

    public void ensureRectangles(int numRectangles) {
        this.ensureRectangles(4 * numRectangles, numRectangles);
    }

    @Override
    public short lastIndex() {
        return this.lastIndex;
    }

    private static final void transformPosition(float[] values, int offset, int size, Matrix4 transform) {
        if (size > 2) {
            vTmp.set(values[offset], values[offset + 1], values[offset + 2]).mul(transform);
            values[offset] = MeshBuilder.vTmp.x;
            values[offset + 1] = MeshBuilder.vTmp.y;
            values[offset + 2] = MeshBuilder.vTmp.z;
        } else if (size > 1) {
            vTmp.set(values[offset], values[offset + 1], 0.0f).mul(transform);
            values[offset] = MeshBuilder.vTmp.x;
            values[offset + 1] = MeshBuilder.vTmp.y;
        } else {
            values[offset] = MeshBuilder.vTmp.set((float)values[offset], (float)0.0f, (float)0.0f).mul((Matrix4)transform).x;
        }
    }

    private static final void transformNormal(float[] values, int offset, int size, Matrix3 transform) {
        if (size > 2) {
            vTmp.set(values[offset], values[offset + 1], values[offset + 2]).mul(transform).nor();
            values[offset] = MeshBuilder.vTmp.x;
            values[offset + 1] = MeshBuilder.vTmp.y;
            values[offset + 2] = MeshBuilder.vTmp.z;
        } else if (size > 1) {
            vTmp.set(values[offset], values[offset + 1], 0.0f).mul(transform).nor();
            values[offset] = MeshBuilder.vTmp.x;
            values[offset + 1] = MeshBuilder.vTmp.y;
        } else {
            values[offset] = MeshBuilder.vTmp.set((float)values[offset], (float)0.0f, (float)0.0f).mul((Matrix3)transform).nor().x;
        }
    }

    private final void addVertex(float[] values, int offset) {
        int o = this.vertices.size;
        this.vertices.addAll(values, offset, this.stride);
        short s = this.vindex;
        this.vindex = (short)(s + 1);
        this.lastIndex = s;
        if (this.vertexTransformationEnabled) {
            MeshBuilder.transformPosition(this.vertices.items, o + this.posOffset, this.posSize, this.positionTransform);
            if (this.norOffset >= 0) {
                MeshBuilder.transformNormal(this.vertices.items, o + this.norOffset, 3, this.normalTransform);
            }
        }
        if (this.hasColor) {
            if (this.colOffset >= 0) {
                float[] arrf = this.vertices.items;
                int n = o + this.colOffset;
                arrf[n] = arrf[n] * this.color.r;
                float[] arrf2 = this.vertices.items;
                int n2 = o + this.colOffset + 1;
                arrf2[n2] = arrf2[n2] * this.color.g;
                float[] arrf3 = this.vertices.items;
                int n3 = o + this.colOffset + 2;
                arrf3[n3] = arrf3[n3] * this.color.b;
                if (this.colSize > 3) {
                    float[] arrf4 = this.vertices.items;
                    int n4 = o + this.colOffset + 3;
                    arrf4[n4] = arrf4[n4] * this.color.a;
                }
            } else if (this.cpOffset >= 0) {
                this.vertices.items[o + this.cpOffset] = this.tempC1.set(NumberUtils.floatToIntColor(this.vertices.items[o + this.cpOffset])).mul(this.color).toFloatBits();
            }
        }
        if (this.hasUVTransform && this.uvOffset >= 0) {
            this.vertices.items[o + this.uvOffset] = this.uOffset + this.uScale * this.vertices.items[o + this.uvOffset];
            this.vertices.items[o + this.uvOffset + 1] = this.vOffset + this.vScale * this.vertices.items[o + this.uvOffset + 1];
        }
    }

    @Override
    public short vertex(Vector3 pos, Vector3 nor, Color col, Vector2 uv) {
        if (this.vindex >= 32767) {
            throw new GdxRuntimeException("Too many vertices used");
        }
        this.vertex[this.posOffset] = pos.x;
        if (this.posSize > 1) {
            this.vertex[this.posOffset + 1] = pos.y;
        }
        if (this.posSize > 2) {
            this.vertex[this.posOffset + 2] = pos.z;
        }
        if (this.norOffset >= 0) {
            if (nor == null) {
                nor = this.tmpNormal.set(pos).nor();
            }
            this.vertex[this.norOffset] = nor.x;
            this.vertex[this.norOffset + 1] = nor.y;
            this.vertex[this.norOffset + 2] = nor.z;
        }
        if (this.colOffset >= 0) {
            if (col == null) {
                col = Color.WHITE;
            }
            this.vertex[this.colOffset] = col.r;
            this.vertex[this.colOffset + 1] = col.g;
            this.vertex[this.colOffset + 2] = col.b;
            if (this.colSize > 3) {
                this.vertex[this.colOffset + 3] = col.a;
            }
        } else if (this.cpOffset > 0) {
            if (col == null) {
                col = Color.WHITE;
            }
            this.vertex[this.cpOffset] = col.toFloatBits();
        }
        if (uv != null && this.uvOffset >= 0) {
            this.vertex[this.uvOffset] = uv.x;
            this.vertex[this.uvOffset + 1] = uv.y;
        }
        this.addVertex(this.vertex, 0);
        return this.lastIndex;
    }

    @Override
    public /* varargs */ short vertex(float ... values) {
        int n = values.length - this.stride;
        for (int i = 0; i <= n; i += this.stride) {
            this.addVertex(values, i);
        }
        return this.lastIndex;
    }

    @Override
    public short vertex(MeshPartBuilder.VertexInfo info) {
        return this.vertex(info.hasPosition ? info.position : null, info.hasNormal ? info.normal : null, info.hasColor ? info.color : null, info.hasUV ? info.uv : null);
    }

    @Override
    public void index(short value) {
        this.indices.add(value);
    }

    @Override
    public void index(short value1, short value2) {
        this.ensureIndices(2);
        this.indices.add(value1);
        this.indices.add(value2);
    }

    @Override
    public void index(short value1, short value2, short value3) {
        this.ensureIndices(3);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
    }

    @Override
    public void index(short value1, short value2, short value3, short value4) {
        this.ensureIndices(4);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
    }

    @Override
    public void index(short value1, short value2, short value3, short value4, short value5, short value6) {
        this.ensureIndices(6);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
        this.indices.add(value5);
        this.indices.add(value6);
    }

    @Override
    public void index(short value1, short value2, short value3, short value4, short value5, short value6, short value7, short value8) {
        this.ensureIndices(8);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
        this.indices.add(value5);
        this.indices.add(value6);
        this.indices.add(value7);
        this.indices.add(value8);
    }

    @Override
    public void line(short index1, short index2) {
        if (this.primitiveType != 1) {
            throw new GdxRuntimeException("Incorrect primitive type");
        }
        this.index(index1, index2);
    }

    @Override
    public void line(MeshPartBuilder.VertexInfo p1, MeshPartBuilder.VertexInfo p2) {
        this.ensureVertices(2);
        this.line(this.vertex(p1), this.vertex(p2));
    }

    @Override
    public void line(Vector3 p1, Vector3 p2) {
        this.line(this.vertTmp1.set(p1, null, null, null), this.vertTmp2.set(p2, null, null, null));
    }

    @Override
    public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.line(this.vertTmp1.set(null, null, null, null).setPos(x1, y1, z1), this.vertTmp2.set(null, null, null, null).setPos(x2, y2, z2));
    }

    @Override
    public void line(Vector3 p1, Color c1, Vector3 p2, Color c2) {
        this.line(this.vertTmp1.set(p1, null, c1, null), this.vertTmp2.set(p2, null, c2, null));
    }

    @Override
    public void triangle(short index1, short index2, short index3) {
        if (this.primitiveType == 4 || this.primitiveType == 0) {
            this.index(index1, index2, index3);
        } else if (this.primitiveType == 1) {
            this.index(index1, index2, index2, index3, index3, index1);
        } else {
            throw new GdxRuntimeException("Incorrect primitive type");
        }
    }

    @Override
    public void triangle(MeshPartBuilder.VertexInfo p1, MeshPartBuilder.VertexInfo p2, MeshPartBuilder.VertexInfo p3) {
        this.ensureVertices(3);
        this.triangle(this.vertex(p1), this.vertex(p2), this.vertex(p3));
    }

    @Override
    public void triangle(Vector3 p1, Vector3 p2, Vector3 p3) {
        this.triangle(this.vertTmp1.set(p1, null, null, null), this.vertTmp2.set(p2, null, null, null), this.vertTmp3.set(p3, null, null, null));
    }

    @Override
    public void triangle(Vector3 p1, Color c1, Vector3 p2, Color c2, Vector3 p3, Color c3) {
        this.triangle(this.vertTmp1.set(p1, null, c1, null), this.vertTmp2.set(p2, null, c2, null), this.vertTmp3.set(p3, null, c3, null));
    }

    @Override
    public void rect(short corner00, short corner10, short corner11, short corner01) {
        if (this.primitiveType == 4) {
            this.index(corner00, corner10, corner11, corner11, corner01, corner00);
        } else if (this.primitiveType == 1) {
            this.index(corner00, corner10, corner10, corner11, corner11, corner01, corner01, corner00);
        } else if (this.primitiveType == 0) {
            this.index(corner00, corner10, corner11, corner01);
        } else {
            throw new GdxRuntimeException("Incorrect primitive type");
        }
    }

    @Override
    public void rect(MeshPartBuilder.VertexInfo corner00, MeshPartBuilder.VertexInfo corner10, MeshPartBuilder.VertexInfo corner11, MeshPartBuilder.VertexInfo corner01) {
        this.ensureVertices(4);
        this.rect(this.vertex(corner00), this.vertex(corner10), this.vertex(corner11), this.vertex(corner01));
    }

    @Override
    public void rect(Vector3 corner00, Vector3 corner10, Vector3 corner11, Vector3 corner01, Vector3 normal) {
        this.rect(this.vertTmp1.set(corner00, normal, null, null).setUV(0.0f, 1.0f), this.vertTmp2.set(corner10, normal, null, null).setUV(1.0f, 1.0f), this.vertTmp3.set(corner11, normal, null, null).setUV(1.0f, 0.0f), this.vertTmp4.set(corner01, normal, null, null).setUV(0.0f, 0.0f));
    }

    @Override
    public void rect(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ) {
        this.rect(this.vertTmp1.set(null, null, null, null).setPos(x00, y00, z00).setNor(normalX, normalY, normalZ).setUV(0.0f, 1.0f), this.vertTmp2.set(null, null, null, null).setPos(x10, y10, z10).setNor(normalX, normalY, normalZ).setUV(1.0f, 1.0f), this.vertTmp3.set(null, null, null, null).setPos(x11, y11, z11).setNor(normalX, normalY, normalZ).setUV(1.0f, 0.0f), this.vertTmp4.set(null, null, null, null).setPos(x01, y01, z01).setNor(normalX, normalY, normalZ).setUV(0.0f, 0.0f));
    }

    @Override
    public void patch(MeshPartBuilder.VertexInfo corner00, MeshPartBuilder.VertexInfo corner10, MeshPartBuilder.VertexInfo corner11, MeshPartBuilder.VertexInfo corner01, int divisionsU, int divisionsV) {
        if (divisionsU < 1 || divisionsV < 1) {
            throw new GdxRuntimeException("divisionsU and divisionV must be > 0, u,v: " + divisionsU + ", " + divisionsV);
        }
        this.ensureRectangles((divisionsV + 1) * (divisionsU + 1), divisionsV * divisionsU);
        for (int u = 0; u <= divisionsU; ++u) {
            float alphaU = (float)u / (float)divisionsU;
            this.vertTmp5.set(corner00).lerp(corner10, alphaU);
            this.vertTmp6.set(corner01).lerp(corner11, alphaU);
            for (int v = 0; v <= divisionsV; ++v) {
                short idx = this.vertex(this.vertTmp7.set(this.vertTmp5).lerp(this.vertTmp6, (float)v / (float)divisionsV));
                if (u <= 0 || v <= 0) continue;
                this.rect((short)(idx - divisionsV - 2), (short)(idx - 1), idx, (short)(idx - divisionsV - 1));
            }
        }
    }

    @Override
    public void patch(Vector3 corner00, Vector3 corner10, Vector3 corner11, Vector3 corner01, Vector3 normal, int divisionsU, int divisionsV) {
        this.patch(this.vertTmp1.set(corner00, normal, null, null).setUV(0.0f, 1.0f), this.vertTmp2.set(corner10, normal, null, null).setUV(1.0f, 1.0f), this.vertTmp3.set(corner11, normal, null, null).setUV(1.0f, 0.0f), this.vertTmp4.set(corner01, normal, null, null).setUV(0.0f, 0.0f), divisionsU, divisionsV);
    }

    @Override
    public void patch(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ, int divisionsU, int divisionsV) {
        this.patch(this.vertTmp1.set(null).setPos(x00, y00, z00).setNor(normalX, normalY, normalZ).setUV(0.0f, 1.0f), this.vertTmp2.set(null).setPos(x10, y10, z10).setNor(normalX, normalY, normalZ).setUV(1.0f, 1.0f), this.vertTmp3.set(null).setPos(x11, y11, z11).setNor(normalX, normalY, normalZ).setUV(1.0f, 0.0f), this.vertTmp4.set(null).setPos(x01, y01, z01).setNor(normalX, normalY, normalZ).setUV(0.0f, 0.0f), divisionsU, divisionsV);
    }

    @Override
    public void box(MeshPartBuilder.VertexInfo corner000, MeshPartBuilder.VertexInfo corner010, MeshPartBuilder.VertexInfo corner100, MeshPartBuilder.VertexInfo corner110, MeshPartBuilder.VertexInfo corner001, MeshPartBuilder.VertexInfo corner011, MeshPartBuilder.VertexInfo corner101, MeshPartBuilder.VertexInfo corner111) {
        this.ensureVertices(8);
        short i000 = this.vertex(corner000);
        short i100 = this.vertex(corner100);
        short i110 = this.vertex(corner110);
        short i010 = this.vertex(corner010);
        short i001 = this.vertex(corner001);
        short i101 = this.vertex(corner101);
        short i111 = this.vertex(corner111);
        short i011 = this.vertex(corner011);
        if (this.primitiveType == 1) {
            this.ensureIndices(24);
            this.rect(i000, i100, i110, i010);
            this.rect(i101, i001, i011, i111);
            this.index(i000, i001, i010, i011, i110, i111, i100, i101);
        } else if (this.primitiveType == 0) {
            this.ensureRectangleIndices(2);
            this.rect(i000, i100, i110, i010);
            this.rect(i101, i001, i011, i111);
        } else {
            this.ensureRectangleIndices(6);
            this.rect(i000, i100, i110, i010);
            this.rect(i101, i001, i011, i111);
            this.rect(i000, i010, i011, i001);
            this.rect(i101, i111, i110, i100);
            this.rect(i101, i100, i000, i001);
            this.rect(i110, i111, i011, i010);
        }
    }

    @Override
    public void box(Vector3 corner000, Vector3 corner010, Vector3 corner100, Vector3 corner110, Vector3 corner001, Vector3 corner011, Vector3 corner101, Vector3 corner111) {
        if (this.norOffset < 0 && this.uvOffset < 0) {
            this.box(this.vertTmp1.set(corner000, null, null, null), this.vertTmp2.set(corner010, null, null, null), this.vertTmp3.set(corner100, null, null, null), this.vertTmp4.set(corner110, null, null, null), this.vertTmp5.set(corner001, null, null, null), this.vertTmp6.set(corner011, null, null, null), this.vertTmp7.set(corner101, null, null, null), this.vertTmp8.set(corner111, null, null, null));
        } else {
            this.ensureRectangles(6);
            Vector3 nor = this.tempV1.set(corner000).lerp(corner110, 0.5f).sub(this.tempV2.set(corner001).lerp(corner111, 0.5f)).nor();
            this.rect(corner000, corner010, corner110, corner100, nor);
            this.rect(corner011, corner001, corner101, corner111, nor.scl(-1.0f));
            nor = this.tempV1.set(corner000).lerp(corner101, 0.5f).sub(this.tempV2.set(corner010).lerp(corner111, 0.5f)).nor();
            this.rect(corner001, corner000, corner100, corner101, nor);
            this.rect(corner010, corner011, corner111, corner110, nor.scl(-1.0f));
            nor = this.tempV1.set(corner000).lerp(corner011, 0.5f).sub(this.tempV2.set(corner100).lerp(corner111, 0.5f)).nor();
            this.rect(corner001, corner011, corner010, corner000, nor);
            this.rect(corner100, corner110, corner111, corner101, nor.scl(-1.0f));
        }
    }

    @Override
    public void box(Matrix4 transform) {
        this.box(this.tmp(-0.5f, -0.5f, -0.5f).mul(transform), this.tmp(-0.5f, 0.5f, -0.5f).mul(transform), this.tmp(0.5f, -0.5f, -0.5f).mul(transform), this.tmp(0.5f, 0.5f, -0.5f).mul(transform), this.tmp(-0.5f, -0.5f, 0.5f).mul(transform), this.tmp(-0.5f, 0.5f, 0.5f).mul(transform), this.tmp(0.5f, -0.5f, 0.5f).mul(transform), this.tmp(0.5f, 0.5f, 0.5f).mul(transform));
        this.cleanup();
    }

    @Override
    public void box(float width, float height, float depth) {
        this.box(this.matTmp1.setToScaling(width, height, depth));
    }

    @Override
    public void box(float x, float y, float z, float width, float height, float depth) {
        this.box(this.matTmp1.setToScaling(width, height, depth).trn(x, y, z));
    }

    @Override
    public void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        this.circle(radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }

    @Override
    public void circle(float radius, int divisions, Vector3 center, Vector3 normal) {
        this.circle(radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z);
    }

    @Override
    public void circle(float radius, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal) {
        this.circle(radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z);
    }

    @Override
    public void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ) {
        this.circle(radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, 0.0f, 360.0f);
    }

    @Override
    public void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        this.ellipse(radius * 2.0f, radius * 2.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }

    @Override
    public void circle(float radius, int divisions, Vector3 center, Vector3 normal, float angleFrom, float angleTo) {
        this.circle(radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, angleFrom, angleTo);
    }

    @Override
    public void circle(float radius, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal, float angleFrom, float angleTo) {
        this.circle(radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z, angleFrom, angleTo);
    }

    @Override
    public void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        this.ellipse(radius * 2.0f, radius * 2.0f, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }

    @Override
    public void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        this.ellipse(width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }

    @Override
    public void ellipse(float width, float height, int divisions, Vector3 center, Vector3 normal) {
        this.ellipse(width, height, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z);
    }

    @Override
    public void ellipse(float width, float height, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal) {
        this.ellipse(width, height, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z);
    }

    @Override
    public void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ) {
        this.ellipse(width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, 0.0f, 360.0f);
    }

    @Override
    public void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        this.ellipse(width, height, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }

    @Override
    public void ellipse(float width, float height, int divisions, Vector3 center, Vector3 normal, float angleFrom, float angleTo) {
        this.ellipse(width, height, 0.0f, 0.0f, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, angleFrom, angleTo);
    }

    @Override
    public void ellipse(float width, float height, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal, float angleFrom, float angleTo) {
        this.ellipse(width, height, 0.0f, 0.0f, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z, angleFrom, angleTo);
    }

    @Override
    public void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        this.ellipse(width, height, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }

    @Override
    public void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, Vector3 center, Vector3 normal) {
        this.ellipse(width, height, innerWidth, innerHeight, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, 0.0f, 360.0f);
    }

    @Override
    public void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        this.ellipse(width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }

    @Override
    public void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        this.tempV1.set(normalX, normalY, normalZ).crs(0.0f, 0.0f, 1.0f);
        this.tempV2.set(normalX, normalY, normalZ).crs(0.0f, 1.0f, 0.0f);
        if (this.tempV2.len2() > this.tempV1.len2()) {
            this.tempV1.set(this.tempV2);
        }
        this.tempV2.set(this.tempV1.nor()).crs(normalX, normalY, normalZ).nor();
        this.ellipse(width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, this.tempV1.x, this.tempV1.y, this.tempV1.z, this.tempV2.x, this.tempV2.y, this.tempV2.z, angleFrom, angleTo);
    }

    @Override
    public void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        if (innerWidth <= 0.0f || innerHeight <= 0.0f) {
            this.ensureTriangles(divisions + 2, divisions);
        } else if (innerWidth == width && innerHeight == height) {
            this.ensureVertices(divisions + 1);
            this.ensureIndices(divisions + 1);
            if (this.primitiveType != 1) {
                throw new GdxRuntimeException("Incorrect primitive type : expect GL_LINES because innerWidth == width && innerHeight == height");
            }
        } else {
            this.ensureRectangles((divisions + 1) * 2, divisions + 1);
        }
        float ao = 0.017453292f * angleFrom;
        float step = 0.017453292f * (angleTo - angleFrom) / (float)divisions;
        Vector3 sxEx = this.tempV1.set(tangentX, tangentY, tangentZ).scl(width * 0.5f);
        Vector3 syEx = this.tempV2.set(binormalX, binormalY, binormalZ).scl(height * 0.5f);
        Vector3 sxIn = this.tempV3.set(tangentX, tangentY, tangentZ).scl(innerWidth * 0.5f);
        Vector3 syIn = this.tempV4.set(binormalX, binormalY, binormalZ).scl(innerHeight * 0.5f);
        MeshPartBuilder.VertexInfo currIn = this.vertTmp3.set(null, null, null, null);
        currIn.hasNormal = true;
        currIn.hasPosition = true;
        currIn.hasUV = true;
        currIn.uv.set(0.5f, 0.5f);
        currIn.position.set(centerX, centerY, centerZ);
        currIn.normal.set(normalX, normalY, normalZ);
        MeshPartBuilder.VertexInfo currEx = this.vertTmp4.set(null, null, null, null);
        currEx.hasNormal = true;
        currEx.hasPosition = true;
        currEx.hasUV = true;
        currEx.uv.set(0.5f, 0.5f);
        currEx.position.set(centerX, centerY, centerZ);
        currEx.normal.set(normalX, normalY, normalZ);
        short center = this.vertex(currEx);
        float angle = 0.0f;
        float us = 0.5f * (innerWidth / width);
        float vs = 0.5f * (innerHeight / height);
        short i2 = 0;
        short i3 = 0;
        short i4 = 0;
        for (int i = 0; i <= divisions; ++i) {
            angle = ao + step * (float)i;
            float x = MathUtils.cos(angle);
            float y = MathUtils.sin(angle);
            currEx.position.set(centerX, centerY, centerZ).add(sxEx.x * x + syEx.x * y, sxEx.y * x + syEx.y * y, sxEx.z * x + syEx.z * y);
            currEx.uv.set(0.5f + 0.5f * x, 0.5f + 0.5f * y);
            short i1 = this.vertex(currEx);
            if (innerWidth <= 0.0f || innerHeight <= 0.0f) {
                if (i != 0) {
                    this.triangle(i1, i2, center);
                }
                i2 = i1;
                continue;
            }
            if (innerWidth == width && innerHeight == height) {
                if (i != 0) {
                    this.line(i1, i2);
                }
                i2 = i1;
                continue;
            }
            currIn.position.set(centerX, centerY, centerZ).add(sxIn.x * x + syIn.x * y, sxIn.y * x + syIn.y * y, sxIn.z * x + syIn.z * y);
            currIn.uv.set(0.5f + us * x, 0.5f + vs * y);
            i2 = i1;
            i1 = this.vertex(currIn);
            if (i != 0) {
                this.rect(i1, i2, i4, i3);
            }
            i4 = i2;
            i3 = i1;
        }
    }

    @Override
    public void cylinder(float width, float height, float depth, int divisions) {
        this.cylinder(width, height, depth, divisions, 0.0f, 360.0f);
    }

    @Override
    public void cylinder(float width, float height, float depth, int divisions, float angleFrom, float angleTo) {
        this.cylinder(width, height, depth, divisions, angleFrom, angleTo, true);
    }

    @Override
    public void cylinder(float width, float height, float depth, int divisions, float angleFrom, float angleTo, boolean close) {
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = depth * 0.5f;
        float ao = 0.017453292f * angleFrom;
        float step = 0.017453292f * (angleTo - angleFrom) / (float)divisions;
        float us = 1.0f / (float)divisions;
        float u = 0.0f;
        float angle = 0.0f;
        MeshPartBuilder.VertexInfo curr1 = this.vertTmp3.set(null, null, null, null);
        curr1.hasNormal = true;
        curr1.hasPosition = true;
        curr1.hasUV = true;
        MeshPartBuilder.VertexInfo curr2 = this.vertTmp4.set(null, null, null, null);
        curr2.hasNormal = true;
        curr2.hasPosition = true;
        curr2.hasUV = true;
        short i3 = 0;
        short i4 = 0;
        this.ensureRectangles(2 * (divisions + 1), divisions);
        for (int i = 0; i <= divisions; ++i) {
            angle = ao + step * (float)i;
            u = 1.0f - us * (float)i;
            curr1.position.set(MathUtils.cos(angle) * hw, 0.0f, MathUtils.sin(angle) * hd);
            curr1.normal.set(curr1.position).nor();
            curr1.position.y = - hh;
            curr1.uv.set(u, 1.0f);
            curr2.position.set(curr1.position);
            curr2.normal.set(curr1.normal);
            curr2.position.y = hh;
            curr2.uv.set(u, 0.0f);
            short i2 = this.vertex(curr1);
            short i1 = this.vertex(curr2);
            if (i != 0) {
                this.rect(i3, i1, i2, i4);
            }
            i4 = i2;
            i3 = i1;
        }
        if (close) {
            this.ellipse(width, depth, 0.0f, 0.0f, divisions, 0.0f, hh, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, angleFrom, angleTo);
            this.ellipse(width, depth, 0.0f, 0.0f, divisions, 0.0f, - hh, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 180.0f - angleTo, 180.0f - angleFrom);
        }
    }

    @Override
    public void cone(float width, float height, float depth, int divisions) {
        this.cone(width, height, depth, divisions, 0.0f, 360.0f);
    }

    @Override
    public void cone(float width, float height, float depth, int divisions, float angleFrom, float angleTo) {
        this.ensureTriangles(divisions + 2, divisions);
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = depth * 0.5f;
        float ao = 0.017453292f * angleFrom;
        float step = 0.017453292f * (angleTo - angleFrom) / (float)divisions;
        float us = 1.0f / (float)divisions;
        float u = 0.0f;
        float angle = 0.0f;
        MeshPartBuilder.VertexInfo curr1 = this.vertTmp3.set(null, null, null, null);
        curr1.hasNormal = true;
        curr1.hasPosition = true;
        curr1.hasUV = true;
        MeshPartBuilder.VertexInfo curr2 = this.vertTmp4.set(null, null, null, null).setPos(0.0f, hh, 0.0f).setNor(0.0f, 1.0f, 0.0f).setUV(0.5f, 0.0f);
        short base = this.vertex(curr2);
        short i2 = 0;
        for (int i = 0; i <= divisions; ++i) {
            angle = ao + step * (float)i;
            u = 1.0f - us * (float)i;
            curr1.position.set(MathUtils.cos(angle) * hw, 0.0f, MathUtils.sin(angle) * hd);
            curr1.normal.set(curr1.position).nor();
            curr1.position.y = - hh;
            curr1.uv.set(u, 1.0f);
            short i1 = this.vertex(curr1);
            if (i != 0) {
                this.triangle(base, i1, i2);
            }
            i2 = i1;
        }
        this.ellipse(width, depth, 0.0f, 0.0f, divisions, 0.0f, - hh, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 180.0f - angleTo, 180.0f - angleFrom);
    }

    @Override
    public void sphere(float width, float height, float depth, int divisionsU, int divisionsV) {
        this.sphere(width, height, depth, divisionsU, divisionsV, 0.0f, 360.0f, 0.0f, 180.0f);
    }

    @Override
    public void sphere(Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV) {
        this.sphere(transform, width, height, depth, divisionsU, divisionsV, 0.0f, 360.0f, 0.0f, 180.0f);
    }

    @Override
    public void sphere(float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        this.sphere(this.matTmp1.idt(), width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }

    @Override
    public void sphere(Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = depth * 0.5f;
        float auo = 0.017453292f * angleUFrom;
        float stepU = 0.017453292f * (angleUTo - angleUFrom) / (float)divisionsU;
        float avo = 0.017453292f * angleVFrom;
        float stepV = 0.017453292f * (angleVTo - angleVFrom) / (float)divisionsV;
        float us = 1.0f / (float)divisionsU;
        float vs = 1.0f / (float)divisionsV;
        float u = 0.0f;
        float v = 0.0f;
        float angleU = 0.0f;
        float angleV = 0.0f;
        MeshPartBuilder.VertexInfo curr1 = this.vertTmp3.set(null, null, null, null);
        curr1.hasNormal = true;
        curr1.hasPosition = true;
        curr1.hasUV = true;
        int s = divisionsU + 3;
        tmpIndices.clear();
        tmpIndices.ensureCapacity(divisionsU * 2);
        MeshBuilder.tmpIndices.size = s;
        int tempOffset = 0;
        this.ensureRectangles((divisionsV + 1) * (divisionsU + 1), divisionsV * divisionsU);
        for (int iv = 0; iv <= divisionsV; ++iv) {
            angleV = avo + stepV * (float)iv;
            v = vs * (float)iv;
            float t = MathUtils.sin(angleV);
            float h = MathUtils.cos(angleV) * hh;
            for (int iu = 0; iu <= divisionsU; ++iu) {
                angleU = auo + stepU * (float)iu;
                u = 1.0f - us * (float)iu;
                curr1.position.set(MathUtils.cos(angleU) * hw * t, h, MathUtils.sin(angleU) * hd * t).mul(transform);
                curr1.normal.set(curr1.position).nor();
                curr1.uv.set(u, v);
                tmpIndices.set(tempOffset, this.vertex(curr1));
                int o = tempOffset + s;
                if (iv > 0 && iu > 0) {
                    this.rect(tmpIndices.get(tempOffset), tmpIndices.get((o - 1) % s), tmpIndices.get((o - (divisionsU + 2)) % s), tmpIndices.get((o - (divisionsU + 1)) % s));
                }
                tempOffset = (tempOffset + 1) % MeshBuilder.tmpIndices.size;
            }
        }
    }

    @Override
    public void capsule(float radius, float height, int divisions) {
        if (height < 2.0f * radius) {
            throw new GdxRuntimeException("Height must be at least twice the radius");
        }
        float d = 2.0f * radius;
        this.cylinder(d, height - d, d, divisions, 0.0f, 360.0f, false);
        this.sphere(this.matTmp1.setToTranslation(0.0f, 0.5f * (height - d), 0.0f), d, d, d, divisions, divisions, 0.0f, 360.0f, 0.0f, 90.0f);
        this.sphere(this.matTmp1.setToTranslation(0.0f, -0.5f * (height - d), 0.0f), d, d, d, divisions, divisions, 0.0f, 360.0f, 90.0f, 180.0f);
    }

    @Override
    public void arrow(float x1, float y1, float z1, float x2, float y2, float z2, float capLength, float stemThickness, int divisions) {
        Vector3 begin = this.tmp(x1, y1, z1);
        Vector3 end = this.tmp(x2, y2, z2);
        float length = begin.dst(end);
        float coneHeight = length * capLength;
        float coneDiameter = 2.0f * (float)((double)coneHeight * Math.sqrt(0.3333333432674408));
        float stemLength = length - coneHeight;
        float stemDiameter = coneDiameter * stemThickness;
        Vector3 up = this.tmp(end).sub(begin).nor();
        Vector3 forward = this.tmp(up).crs(Vector3.Z);
        if (forward.isZero()) {
            forward.set(Vector3.X);
        }
        forward.crs(up).nor();
        Vector3 left = this.tmp(up).crs(forward).nor();
        Vector3 direction = this.tmp(end).sub(begin).nor();
        Matrix4 userTransform = this.getVertexTransform(this.tmp());
        Matrix4 transform = this.tmp();
        float[] val = transform.val;
        val[0] = left.x;
        val[4] = up.x;
        val[8] = forward.x;
        val[1] = left.y;
        val[5] = up.y;
        val[9] = forward.y;
        val[2] = left.z;
        val[6] = up.z;
        val[10] = forward.z;
        Matrix4 temp = this.tmp();
        transform.setTranslation(this.tmp(direction).scl(stemLength / 2.0f).add(x1, y1, z1));
        this.setVertexTransform(temp.set(transform).mul(userTransform));
        this.cylinder(stemDiameter, stemLength, stemDiameter, divisions);
        transform.setTranslation(this.tmp(direction).scl(stemLength).add(x1, y1, z1));
        this.setVertexTransform(temp.set(transform).mul(userTransform));
        this.cone(coneDiameter, coneHeight, coneDiameter, divisions);
        this.setVertexTransform(userTransform);
        this.cleanup();
    }

    @Override
    public void addMesh(Mesh mesh) {
        this.addMesh(mesh, 0, mesh.getNumIndices());
    }

    @Override
    public void addMesh(MeshPart meshpart) {
        if (meshpart.primitiveType != this.primitiveType) {
            throw new GdxRuntimeException("Primitive type doesn't match");
        }
        this.addMesh(meshpart.mesh, meshpart.indexOffset, meshpart.numVertices);
    }

    @Override
    public void addMesh(Mesh mesh, int indexOffset, int numIndices) {
        if (!this.attributes.equals(mesh.getVertexAttributes())) {
            throw new GdxRuntimeException("Vertex attributes do not match");
        }
        if (numIndices <= 0) {
            return;
        }
        int numFloats = mesh.getNumVertices() * this.stride;
        tmpVertices.clear();
        tmpVertices.ensureCapacity(numFloats);
        MeshBuilder.tmpVertices.size = numFloats;
        mesh.getVertices(MeshBuilder.tmpVertices.items);
        tmpIndices.clear();
        tmpIndices.ensureCapacity(numIndices);
        MeshBuilder.tmpIndices.size = numIndices;
        mesh.getIndices(indexOffset, numIndices, MeshBuilder.tmpIndices.items, 0);
        this.addMesh(MeshBuilder.tmpVertices.items, MeshBuilder.tmpIndices.items, 0, numIndices);
    }

    private void addMesh(float[] vertices, short[] indices, int indexOffset, int numIndices) {
        if (indicesMap == null) {
            indicesMap = new IntIntMap(numIndices);
        } else {
            indicesMap.clear();
            indicesMap.ensureCapacity(numIndices);
        }
        this.ensureIndices(numIndices);
        this.ensureVertices(vertices.length < numIndices ? vertices.length : numIndices);
        for (int i = 0; i < numIndices; ++i) {
            short sidx = indices[i];
            int didx = indicesMap.get(sidx, -1);
            if (didx < 0) {
                this.addVertex(vertices, sidx * this.stride);
                didx = this.lastIndex;
                indicesMap.put(sidx, didx);
            }
            this.index((short)didx);
        }
    }

}

