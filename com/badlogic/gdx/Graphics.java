/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

public interface Graphics {
    public boolean isGL30Available();

    public GL20 getGL20();

    public GL30 getGL30();

    public int getWidth();

    public int getHeight();

    public long getFrameId();

    public float getDeltaTime();

    public float getRawDeltaTime();

    public int getFramesPerSecond();

    public GraphicsType getType();

    public float getPpiX();

    public float getPpiY();

    public float getPpcX();

    public float getPpcY();

    public float getDensity();

    public boolean supportsDisplayModeChange();

    public DisplayMode[] getDisplayModes();

    public DisplayMode getDesktopDisplayMode();

    public boolean setDisplayMode(DisplayMode var1);

    public boolean setDisplayMode(int var1, int var2, boolean var3);

    public void setTitle(String var1);

    public void setVSync(boolean var1);

    public BufferFormat getBufferFormat();

    public boolean supportsExtension(String var1);

    public void setContinuousRendering(boolean var1);

    public boolean isContinuousRendering();

    public void requestRendering();

    public boolean isFullscreen();

    public static class BufferFormat {
        public final int r;
        public final int g;
        public final int b;
        public final int a;
        public final int depth;
        public final int stencil;
        public final int samples;
        public final boolean coverageSampling;

        public BufferFormat(int r, int g, int b, int a, int depth, int stencil, int samples, boolean coverageSampling) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.depth = depth;
            this.stencil = stencil;
            this.samples = samples;
            this.coverageSampling = coverageSampling;
        }

        public String toString() {
            return "r: " + this.r + ", g: " + this.g + ", b: " + this.b + ", a: " + this.a + ", depth: " + this.depth + ", stencil: " + this.stencil + ", num samples: " + this.samples + ", coverage sampling: " + this.coverageSampling;
        }
    }

    public static class DisplayMode {
        public final int width;
        public final int height;
        public final int refreshRate;
        public final int bitsPerPixel;

        protected DisplayMode(int width, int height, int refreshRate, int bitsPerPixel) {
            this.width = width;
            this.height = height;
            this.refreshRate = refreshRate;
            this.bitsPerPixel = bitsPerPixel;
        }

        public String toString() {
            return "" + this.width + "x" + this.height + ", bpp: " + this.bitsPerPixel + ", hz: " + this.refreshRate;
        }
    }

    public static enum GraphicsType {
        AndroidGL,
        LWJGL,
        Angle,
        WebGL,
        iOSGL,
        JGLFW,
        Mock;
        

        private GraphicsType() {
        }
    }

}

