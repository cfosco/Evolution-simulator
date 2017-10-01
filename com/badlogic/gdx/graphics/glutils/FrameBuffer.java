/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FrameBuffer
implements Disposable {
    private static final Map<Application, Array<FrameBuffer>> buffers = new HashMap<Application, Array<FrameBuffer>>();
    protected Texture colorTexture;
    private static int defaultFramebufferHandle;
    private static boolean defaultFramebufferHandleInitialized;
    private int framebufferHandle;
    private int depthbufferHandle;
    private int stencilbufferHandle;
    protected final int width;
    protected final int height;
    protected final boolean hasDepth;
    protected final boolean hasStencil;
    protected final Pixmap.Format format;

    public FrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth) {
        this(format, width, height, hasDepth, false);
    }

    public FrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.hasDepth = hasDepth;
        this.hasStencil = hasStencil;
        this.build();
        FrameBuffer.addManagedFrameBuffer(Gdx.app, this);
    }

    protected void setupTexture() {
        this.colorTexture = new Texture(this.width, this.height, this.format);
        this.colorTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.colorTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    private void build() {
        GL20 gl = Gdx.gl20;
        if (!defaultFramebufferHandleInitialized) {
            defaultFramebufferHandleInitialized = true;
            if (Gdx.app.getType() == Application.ApplicationType.iOS) {
                IntBuffer intbuf = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
                gl.glGetIntegerv(36006, intbuf);
                defaultFramebufferHandle = intbuf.get(0);
            } else {
                defaultFramebufferHandle = 0;
            }
        }
        this.setupTexture();
        this.framebufferHandle = gl.glGenFramebuffer();
        if (this.hasDepth) {
            this.depthbufferHandle = gl.glGenRenderbuffer();
        }
        if (this.hasStencil) {
            this.stencilbufferHandle = gl.glGenRenderbuffer();
        }
        gl.glBindTexture(3553, this.colorTexture.getTextureObjectHandle());
        if (this.hasDepth) {
            gl.glBindRenderbuffer(36161, this.depthbufferHandle);
            gl.glRenderbufferStorage(36161, 33189, this.colorTexture.getWidth(), this.colorTexture.getHeight());
        }
        if (this.hasStencil) {
            gl.glBindRenderbuffer(36161, this.stencilbufferHandle);
            gl.glRenderbufferStorage(36161, 36168, this.colorTexture.getWidth(), this.colorTexture.getHeight());
        }
        gl.glBindFramebuffer(36160, this.framebufferHandle);
        gl.glFramebufferTexture2D(36160, 36064, 3553, this.colorTexture.getTextureObjectHandle(), 0);
        if (this.hasDepth) {
            gl.glFramebufferRenderbuffer(36160, 36096, 36161, this.depthbufferHandle);
        }
        if (this.hasStencil) {
            gl.glFramebufferRenderbuffer(36160, 36128, 36161, this.stencilbufferHandle);
        }
        int result = gl.glCheckFramebufferStatus(36160);
        gl.glBindRenderbuffer(36161, 0);
        gl.glBindTexture(3553, 0);
        gl.glBindFramebuffer(36160, defaultFramebufferHandle);
        if (result != 36053) {
            this.colorTexture.dispose();
            if (this.hasDepth) {
                gl.glDeleteRenderbuffer(this.depthbufferHandle);
            }
            if (this.hasStencil) {
                gl.glDeleteRenderbuffer(this.stencilbufferHandle);
            }
            gl.glDeleteFramebuffer(this.framebufferHandle);
            if (result == 36054) {
                throw new IllegalStateException("frame buffer couldn't be constructed: incomplete attachment");
            }
            if (result == 36057) {
                throw new IllegalStateException("frame buffer couldn't be constructed: incomplete dimensions");
            }
            if (result == 36055) {
                throw new IllegalStateException("frame buffer couldn't be constructed: missing attachment");
            }
            if (result == 36061) {
                throw new IllegalStateException("frame buffer couldn't be constructed: unsupported combination of formats");
            }
            throw new IllegalStateException("frame buffer couldn't be constructed: unknown error " + result);
        }
    }

    @Override
    public void dispose() {
        GL20 gl = Gdx.gl20;
        IntBuffer handle = BufferUtils.newIntBuffer(1);
        this.colorTexture.dispose();
        if (this.hasDepth) {
            gl.glDeleteRenderbuffer(this.depthbufferHandle);
        }
        if (this.hasStencil) {
            gl.glDeleteRenderbuffer(this.stencilbufferHandle);
        }
        gl.glDeleteFramebuffer(this.framebufferHandle);
        if (buffers.get(Gdx.app) != null) {
            buffers.get(Gdx.app).removeValue(this, true);
        }
    }

    public void bind() {
        Gdx.gl20.glBindFramebuffer(36160, this.framebufferHandle);
    }

    public static void unbind() {
        Gdx.gl20.glBindFramebuffer(36160, defaultFramebufferHandle);
    }

    public void begin() {
        this.bind();
        this.setFrameBufferViewport();
    }

    protected void setFrameBufferViewport() {
        Gdx.gl20.glViewport(0, 0, this.colorTexture.getWidth(), this.colorTexture.getHeight());
    }

    public void end() {
        FrameBuffer.unbind();
        this.setDefaultFrameBufferViewport();
    }

    protected void setDefaultFrameBufferViewport() {
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void end(int x, int y, int width, int height) {
        FrameBuffer.unbind();
        Gdx.gl20.glViewport(x, y, width, height);
    }

    public Texture getColorBufferTexture() {
        return this.colorTexture;
    }

    public int getHeight() {
        return this.colorTexture.getHeight();
    }

    public int getWidth() {
        return this.colorTexture.getWidth();
    }

    private static void addManagedFrameBuffer(Application app, FrameBuffer frameBuffer) {
        Array managedResources = buffers.get(app);
        if (managedResources == null) {
            managedResources = new Array();
        }
        managedResources.add(frameBuffer);
        buffers.put(app, managedResources);
    }

    public static void invalidateAllFrameBuffers(Application app) {
        if (Gdx.gl20 == null) {
            return;
        }
        Array<FrameBuffer> bufferArray = buffers.get(app);
        if (bufferArray == null) {
            return;
        }
        for (int i = 0; i < bufferArray.size; ++i) {
            bufferArray.get(i).build();
        }
    }

    public static void clearAllFrameBuffers(Application app) {
        buffers.remove(app);
    }

    public static StringBuilder getManagedStatus(StringBuilder builder) {
        builder.append("Managed buffers/app: { ");
        for (Application app : buffers.keySet()) {
            builder.append(FrameBuffer.buffers.get((Object)app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder;
    }

    public static String getManagedStatus() {
        return FrameBuffer.getManagedStatus(new StringBuilder()).toString();
    }

    static {
        defaultFramebufferHandleInitialized = false;
    }
}

