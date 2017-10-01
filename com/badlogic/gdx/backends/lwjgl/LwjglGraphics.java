/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGL20;
import com.badlogic.gdx.backends.lwjgl.LwjglGL30;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.awt.Canvas;
import java.awt.Toolkit;
import java.io.PrintStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class LwjglGraphics
implements Graphics {
    static int major;
    static int minor;
    GL20 gl20;
    GL30 gl30;
    long frameId = -1;
    float deltaTime = 0.0f;
    long frameStart = 0;
    int frames = 0;
    int fps;
    long lastTime = System.nanoTime();
    Canvas canvas;
    boolean vsync = false;
    boolean resize = false;
    LwjglApplicationConfiguration config;
    Graphics.BufferFormat bufferFormat = new Graphics.BufferFormat(8, 8, 8, 8, 16, 8, 0, false);
    String extensions;
    volatile boolean isContinuous = true;
    volatile boolean requestRendering = false;
    boolean softwareMode;

    LwjglGraphics(LwjglApplicationConfiguration config) {
        this.config = config;
    }

    LwjglGraphics(Canvas canvas) {
        this.config = new LwjglApplicationConfiguration();
        this.config.width = canvas.getWidth();
        this.config.height = canvas.getHeight();
        this.canvas = canvas;
    }

    LwjglGraphics(Canvas canvas, LwjglApplicationConfiguration config) {
        this.config = config;
        this.canvas = canvas;
    }

    @Override
    public GL20 getGL20() {
        return this.gl20;
    }

    @Override
    public int getHeight() {
        if (this.canvas != null) {
            return Math.max(1, this.canvas.getHeight());
        }
        return (int)((float)Display.getHeight() * Display.getPixelScaleFactor());
    }

    @Override
    public int getWidth() {
        if (this.canvas != null) {
            return Math.max(1, this.canvas.getWidth());
        }
        return (int)((float)Display.getWidth() * Display.getPixelScaleFactor());
    }

    public boolean isGL20Available() {
        return this.gl20 != null;
    }

    @Override
    public long getFrameId() {
        return this.frameId;
    }

    @Override
    public float getDeltaTime() {
        return this.deltaTime;
    }

    @Override
    public float getRawDeltaTime() {
        return this.deltaTime;
    }

    @Override
    public Graphics.GraphicsType getType() {
        return Graphics.GraphicsType.LWJGL;
    }

    @Override
    public int getFramesPerSecond() {
        return this.fps;
    }

    void updateTime() {
        long time = System.nanoTime();
        this.deltaTime = (float)(time - this.lastTime) / 1.0E9f;
        this.lastTime = time;
        if (time - this.frameStart >= 1000000000) {
            this.fps = this.frames;
            this.frames = 0;
            this.frameStart = time;
        }
        ++this.frames;
    }

    void setupDisplay() throws LWJGLException {
        if (this.config.useHDPI) {
            System.setProperty("org.lwjgl.opengl.Display.enableHighDPI", "true");
        }
        if (this.canvas != null) {
            Display.setParent(this.canvas);
        } else {
            boolean displayCreated = this.setDisplayMode(this.config.width, this.config.height, this.config.fullscreen);
            if (!displayCreated) {
                if (this.config.setDisplayModeCallback != null) {
                    this.config = this.config.setDisplayModeCallback.onFailure(this.config);
                    if (this.config != null) {
                        displayCreated = this.setDisplayMode(this.config.width, this.config.height, this.config.fullscreen);
                    }
                }
                if (!displayCreated) {
                    throw new GdxRuntimeException("Couldn't set display mode " + this.config.width + "x" + this.config.height + ", fullscreen: " + this.config.fullscreen);
                }
            }
            if (this.config.iconPaths.size > 0) {
                ByteBuffer[] icons = new ByteBuffer[this.config.iconPaths.size];
                int n = this.config.iconPaths.size;
                for (int i = 0; i < n; ++i) {
                    Pixmap pixmap = new Pixmap(Gdx.files.getFileHandle(this.config.iconPaths.get(i), this.config.iconFileTypes.get(i)));
                    if (pixmap.getFormat() != Pixmap.Format.RGBA8888) {
                        Pixmap rgba = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
                        rgba.drawPixmap(pixmap, 0, 0);
                        pixmap = rgba;
                    }
                    icons[i] = ByteBuffer.allocateDirect(pixmap.getPixels().limit());
                    icons[i].put(pixmap.getPixels()).flip();
                    pixmap.dispose();
                }
                Display.setIcon(icons);
            }
        }
        Display.setTitle(this.config.title);
        Display.setResizable(this.config.resizable);
        Display.setInitialBackground(this.config.initialBackgroundColor.r, this.config.initialBackgroundColor.g, this.config.initialBackgroundColor.b);
        Display.setLocation(this.config.x, this.config.y);
        this.createDisplayPixelFormat();
        this.initiateGLInstances();
    }

    private void createDisplayPixelFormat() {
        block20 : {
            try {
                if (this.config.useGL30) {
                    ContextAttribs context = new ContextAttribs(3, 2).withForwardCompatible(false).withProfileCore(true);
                    try {
                        Display.create(new PixelFormat(this.config.r + this.config.g + this.config.b, this.config.a, this.config.depth, this.config.stencil, this.config.samples), context);
                    }
                    catch (Exception e) {
                        System.out.println("LwjglGraphics: couldn't create OpenGL 3.2+ core profile context");
                        throw e;
                    }
                    System.out.println("LwjglGraphics: created OpenGL 3.2+ core profile context. This is experimental!");
                } else {
                    Display.create(new PixelFormat(this.config.r + this.config.g + this.config.b, this.config.a, this.config.depth, this.config.stencil, this.config.samples));
                }
                this.bufferFormat = new Graphics.BufferFormat(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil, this.config.samples, false);
            }
            catch (Exception ex) {
                Display.destroy();
                try {
                    Thread.sleep(200);
                }
                catch (InterruptedException ignored) {
                    // empty catch block
                }
                try {
                    Display.create(new PixelFormat(0, 16, 8));
                    if (this.getDesktopDisplayMode().bitsPerPixel == 16) {
                        this.bufferFormat = new Graphics.BufferFormat(5, 6, 5, 0, 16, 8, 0, false);
                    }
                    if (this.getDesktopDisplayMode().bitsPerPixel == 24) {
                        this.bufferFormat = new Graphics.BufferFormat(8, 8, 8, 0, 16, 8, 0, false);
                    }
                    if (this.getDesktopDisplayMode().bitsPerPixel == 32) {
                        this.bufferFormat = new Graphics.BufferFormat(8, 8, 8, 8, 16, 8, 0, false);
                    }
                }
                catch (Exception ex2) {
                    Display.destroy();
                    try {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException ignored) {
                        // empty catch block
                    }
                    try {
                        Display.create(new PixelFormat());
                    }
                    catch (Exception ex3) {
                        String glInfo;
                        if (!this.softwareMode && this.config.allowSoftwareMode) {
                            this.softwareMode = true;
                            System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
                            this.createDisplayPixelFormat();
                            return;
                        }
                        throw new GdxRuntimeException("OpenGL is not supported by the video driver" + ((glInfo = this.glInfo()).isEmpty() ? "." : new StringBuilder().append(":").append(this.glInfo()).toString()), ex3);
                    }
                    if (this.getDesktopDisplayMode().bitsPerPixel == 16) {
                        this.bufferFormat = new Graphics.BufferFormat(5, 6, 5, 0, 8, 0, 0, false);
                    }
                    if (this.getDesktopDisplayMode().bitsPerPixel == 24) {
                        this.bufferFormat = new Graphics.BufferFormat(8, 8, 8, 0, 8, 0, 0, false);
                    }
                    if (this.getDesktopDisplayMode().bitsPerPixel != 32) break block20;
                    this.bufferFormat = new Graphics.BufferFormat(8, 8, 8, 8, 8, 0, 0, false);
                }
            }
        }
    }

    public void initiateGLInstances() {
        String version = GL11.glGetString(7938);
        major = Integer.parseInt("" + version.charAt(0));
        minor = Integer.parseInt("" + version.charAt(2));
        if (major >= 3) {
            this.gl30 = new LwjglGL30();
            this.gl20 = this.gl30;
        } else {
            this.gl20 = new LwjglGL20();
        }
        if (major <= 1) {
            throw new GdxRuntimeException("OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: " + version);
        }
        if (!(major != 2 && !version.contains("2.1") || this.supportsExtension("GL_EXT_framebuffer_object") || this.supportsExtension("GL_ARB_framebuffer_object"))) {
            String glInfo;
            throw new GdxRuntimeException("OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: " + version + ", FBO extension: false" + ((glInfo = this.glInfo()).isEmpty() ? "" : new StringBuilder().append("\n").append(this.glInfo()).toString()));
        }
        Gdx.gl = this.gl20;
        Gdx.gl20 = this.gl20;
        Gdx.gl30 = this.gl30;
    }

    private String glInfo() {
        try {
            return GL11.glGetString(7936) + "\n" + GL11.glGetString(7937) + "\n" + GL11.glGetString(7938);
        }
        catch (Throwable ignored) {
            return "";
        }
    }

    @Override
    public float getPpiX() {
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    @Override
    public float getPpiY() {
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    @Override
    public float getPpcX() {
        return (float)Toolkit.getDefaultToolkit().getScreenResolution() / 2.54f;
    }

    @Override
    public float getPpcY() {
        return (float)Toolkit.getDefaultToolkit().getScreenResolution() / 2.54f;
    }

    @Override
    public float getDensity() {
        if (this.config.overrideDensity != -1) {
            return (float)this.config.overrideDensity / 160.0f;
        }
        return (float)Toolkit.getDefaultToolkit().getScreenResolution() / 160.0f;
    }

    @Override
    public boolean supportsDisplayModeChange() {
        return true;
    }

    @Override
    public boolean setDisplayMode(Graphics.DisplayMode displayMode) {
        DisplayMode mode = ((LwjglDisplayMode)displayMode).mode;
        try {
            if (!mode.isFullscreenCapable()) {
                Display.setDisplayMode(mode);
            } else {
                Display.setDisplayModeAndFullscreen(mode);
            }
            float scaleFactor = Display.getPixelScaleFactor();
            this.config.width = (int)((float)mode.getWidth() * scaleFactor);
            this.config.height = (int)((float)mode.getHeight() * scaleFactor);
            if (Gdx.gl != null) {
                Gdx.gl.glViewport(0, 0, this.config.width, this.config.height);
            }
            this.resize = true;
            return true;
        }
        catch (LWJGLException e) {
            return false;
        }
    }

    @Override
    public boolean setDisplayMode(int width, int height, boolean fullscreen) {
        if (this.getWidth() == width && this.getHeight() == height && Display.isFullscreen() == fullscreen) {
            return true;
        }
        try {
            DisplayMode targetDisplayMode;
            targetDisplayMode = null;
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                for (int i = 0; i < modes.length; ++i) {
                    DisplayMode current = modes[i];
                    if (current.getWidth() != width || current.getHeight() != height) continue;
                    if (!(targetDisplayMode != null && current.getFrequency() < freq || targetDisplayMode != null && current.getBitsPerPixel() <= targetDisplayMode.getBitsPerPixel())) {
                        targetDisplayMode = current;
                        freq = targetDisplayMode.getFrequency();
                    }
                    if (current.getBitsPerPixel() != Display.getDesktopDisplayMode().getBitsPerPixel() || current.getFrequency() != Display.getDesktopDisplayMode().getFrequency()) continue;
                    targetDisplayMode = current;
                    break;
                }
            } else {
                targetDisplayMode = new DisplayMode(width, height);
            }
            if (targetDisplayMode == null) {
                return false;
            }
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
            Display.setResizable(!fullscreen && this.config.resizable);
            float scaleFactor = Display.getPixelScaleFactor();
            this.config.width = (int)((float)targetDisplayMode.getWidth() * scaleFactor);
            this.config.height = (int)((float)targetDisplayMode.getHeight() * scaleFactor);
            if (Gdx.gl != null) {
                Gdx.gl.glViewport(0, 0, this.config.width, this.config.height);
            }
            this.resize = true;
            return true;
        }
        catch (LWJGLException e) {
            return false;
        }
    }

    @Override
    public Graphics.DisplayMode[] getDisplayModes() {
        try {
            DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes();
            Graphics.DisplayMode[] modes = new Graphics.DisplayMode[availableDisplayModes.length];
            int idx = 0;
            for (DisplayMode mode : availableDisplayModes) {
                if (!mode.isFullscreenCapable()) continue;
                modes[idx++] = new LwjglDisplayMode(mode.getWidth(), mode.getHeight(), mode.getFrequency(), mode.getBitsPerPixel(), mode);
            }
            return modes;
        }
        catch (LWJGLException e) {
            throw new GdxRuntimeException("Couldn't fetch available display modes", e);
        }
    }

    @Override
    public Graphics.DisplayMode getDesktopDisplayMode() {
        DisplayMode mode = Display.getDesktopDisplayMode();
        return new LwjglDisplayMode(mode.getWidth(), mode.getHeight(), mode.getFrequency(), mode.getBitsPerPixel(), mode);
    }

    @Override
    public void setTitle(String title) {
        Display.setTitle(title);
    }

    @Override
    public Graphics.BufferFormat getBufferFormat() {
        return this.bufferFormat;
    }

    @Override
    public void setVSync(boolean vsync) {
        this.vsync = vsync;
        Display.setVSyncEnabled(vsync);
    }

    @Override
    public boolean supportsExtension(String extension) {
        if (this.extensions == null) {
            this.extensions = this.gl20.glGetString(7939);
        }
        return this.extensions.contains(extension);
    }

    @Override
    public void setContinuousRendering(boolean isContinuous) {
        this.isContinuous = isContinuous;
    }

    @Override
    public boolean isContinuousRendering() {
        return this.isContinuous;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void requestRendering() {
        LwjglGraphics lwjglGraphics = this;
        synchronized (lwjglGraphics) {
            this.requestRendering = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean shouldRender() {
        LwjglGraphics lwjglGraphics = this;
        synchronized (lwjglGraphics) {
            boolean rq = this.requestRendering;
            this.requestRendering = false;
            return rq || this.isContinuous || Display.isDirty();
        }
    }

    @Override
    public boolean isFullscreen() {
        return Display.isFullscreen();
    }

    public boolean isSoftwareMode() {
        return this.softwareMode;
    }

    @Override
    public boolean isGL30Available() {
        return this.gl30 != null;
    }

    @Override
    public GL30 getGL30() {
        return this.gl30;
    }

    public static interface SetDisplayModeCallback {
        public LwjglApplicationConfiguration onFailure(LwjglApplicationConfiguration var1);
    }

    private class LwjglDisplayMode
    extends Graphics.DisplayMode {
        DisplayMode mode;

        public LwjglDisplayMode(int width, int height, int refreshRate, int bitsPerPixel, DisplayMode mode) {
            super(width, height, refreshRate, bitsPerPixel);
            this.mode = mode;
        }
    }

}

