/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.StringBuilder;

class TextMarkup {
    private static final Pool<ColorChunk> colorChunkPool = new Pool<ColorChunk>(32){

        @Override
        protected ColorChunk newObject() {
            return new ColorChunk();
        }
    };
    private static final Color tempColor = new Color();
    private static final StringBuilder tempColorBuffer = new StringBuilder();
    private Array<ColorChunk> colorChunks = new Array();
    private Array<Color> currentColorStack = new Array();
    private Color lastColor = Color.WHITE;
    private Color defaultColor = Color.WHITE;

    TextMarkup() {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static int parseColorTag(TextMarkup markup, CharSequence str, int nomarkupStart, int start, int end) {
        if (start >= end) throw new GdxRuntimeException("Unclosed color tag.");
        Color hexColor = tempColor;
        if (str.charAt(start) == '#') {
            int colorInt = 0;
            for (int i = start + 1; i < end; ++i) {
                char ch = str.charAt(i);
                if (ch == ']') {
                    if (i < start + 2 || i > start + 9) {
                        throw new GdxRuntimeException("Hex color cannot have " + (i - start - 1) + " digits.");
                    }
                    if (i <= start + 7) {
                        Color.rgb888ToColor(hexColor, colorInt);
                        hexColor.a = 1.0f;
                    } else {
                        Color.rgba8888ToColor(hexColor, colorInt);
                    }
                    markup.beginChunk(hexColor, nomarkupStart);
                    return i - start;
                }
                if (ch >= '0' && ch <= '9') {
                    colorInt = colorInt * 16 + (ch - 48);
                    continue;
                }
                if (ch >= 'a' && ch <= 'f') {
                    colorInt = colorInt * 16 + (ch - 87);
                    continue;
                }
                if (ch < 'A' || ch > 'F') throw new GdxRuntimeException("Unexpected character in hex color: " + ch);
                colorInt = colorInt * 16 + (ch - 55);
            }
            throw new GdxRuntimeException("Unclosed color tag.");
        } else {
            tempColorBuffer.setLength(0);
            for (int i = start; i < end; ++i) {
                char ch = str.charAt(i);
                if (ch == ']') {
                    if (tempColorBuffer.length() == 0) {
                        markup.endChunk(nomarkupStart);
                        return i - start;
                    } else {
                        String colorString = tempColorBuffer.toString();
                        Color newColor = Colors.get(colorString);
                        if (newColor == null) {
                            throw new GdxRuntimeException("Unknown color: " + colorString);
                        }
                        markup.beginChunk(newColor, nomarkupStart);
                    }
                    return i - start;
                }
                tempColorBuffer.append(ch);
            }
        }
        throw new GdxRuntimeException("Unclosed color tag.");
    }

    public void beginChunk(Color color, int start) {
        ColorChunk newChunk = colorChunkPool.obtain();
        newChunk.color.set(color);
        newChunk.start = start;
        this.colorChunks.add(newChunk);
        this.currentColorStack.add(this.lastColor);
        this.lastColor = newChunk.color;
    }

    public void endChunk(int start) {
        if (this.currentColorStack.size > 0) {
            this.lastColor = this.currentColorStack.pop();
            ColorChunk newChunk = colorChunkPool.obtain();
            newChunk.color.set(this.lastColor);
            newChunk.start = start;
            this.colorChunks.add(newChunk);
        }
    }

    public void tint(BitmapFontCache cache, Color tint) {
        int current = 0;
        float floatColor = tempColor.set(this.defaultColor).mul(tint).toFloatBits();
        for (ColorChunk chunk : this.colorChunks) {
            int next = chunk.start;
            if (current < next) {
                cache.setColors(floatColor, current, next);
                current = next;
            }
            floatColor = tempColor.set(chunk.color).mul(tint).toFloatBits();
        }
        int charsCount = cache.getCharsCount();
        if (current < charsCount) {
            cache.setColors(floatColor, current, charsCount);
        }
    }

    public void clear() {
        int size = this.colorChunks.size;
        for (int i = 0; i < size; ++i) {
            colorChunkPool.free(this.colorChunks.get(i));
            this.colorChunks.set(i, null);
        }
        this.colorChunks.size = 0;
        this.currentColorStack.clear();
        this.setDefaultChunk(this.defaultColor, 0);
    }

    public Color getLastColor() {
        return this.lastColor;
    }

    private void setDefaultColor(Color defaultColor) {
        if (this.currentColorStack.size == 0) {
            this.defaultColor = defaultColor;
            this.lastColor = defaultColor;
        }
    }

    public void setDefaultChunk(float color, int start) {
        int abgr = NumberUtils.floatToIntColor(color);
        this.setDefaultChunk(abgr, start);
    }

    public void setDefaultChunk(int abgr, int start) {
        ColorChunk newChunk = colorChunkPool.obtain();
        Color color = newChunk.color;
        color.r = (float)(abgr & 255) / 255.0f;
        color.g = (float)(abgr >>> 8 & 255) / 255.0f;
        color.b = (float)(abgr >>> 16 & 255) / 255.0f;
        color.a = (float)(abgr >>> 24 & 255) / 255.0f;
        newChunk.start = start;
        this.colorChunks.add(newChunk);
        this.setDefaultColor(newChunk.color);
    }

    public void setDefaultChunk(Color color, int start) {
        ColorChunk newChunk = colorChunkPool.obtain();
        newChunk.color.set(color);
        newChunk.start = start;
        this.colorChunks.add(newChunk);
        this.setDefaultColor(newChunk.color);
    }

    public static class ColorChunk {
        public int start;
        public final Color color = new Color();
    }

}

