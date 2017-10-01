/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class IndexArray
implements IndexData {
    static final IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
    ShortBuffer buffer;
    ByteBuffer byteBuffer;

    public IndexArray(int maxIndices) {
        this.byteBuffer = BufferUtils.newUnsafeByteBuffer(maxIndices * 2);
        this.buffer = this.byteBuffer.asShortBuffer();
        this.buffer.flip();
        this.byteBuffer.flip();
    }

    @Override
    public int getNumIndices() {
        return this.buffer.limit();
    }

    @Override
    public int getNumMaxIndices() {
        return this.buffer.capacity();
    }

    @Override
    public void setIndices(short[] indices, int offset, int count) {
        this.buffer.clear();
        this.buffer.put(indices, offset, count);
        this.buffer.flip();
        this.byteBuffer.position(0);
        this.byteBuffer.limit(count << 1);
    }

    @Override
    public void setIndices(ShortBuffer indices) {
        int pos = indices.position();
        this.buffer.clear();
        this.buffer.limit(indices.remaining());
        this.buffer.put(indices);
        this.buffer.flip();
        indices.position(pos);
        this.byteBuffer.position(0);
        this.byteBuffer.limit(this.buffer.limit() << 1);
    }

    @Override
    public ShortBuffer getBuffer() {
        return this.buffer;
    }

    @Override
    public void bind() {
    }

    @Override
    public void unbind() {
    }

    @Override
    public void invalidate() {
    }

    @Override
    public void dispose() {
        BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
    }
}

