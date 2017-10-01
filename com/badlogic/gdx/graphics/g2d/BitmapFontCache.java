/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextMarkup;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.NumberUtils;

public class BitmapFontCache {
    private final BitmapFont font;
    private float[][] vertexData;
    private int[] idx;
    private int[] tmpGlyphCount;
    private float x;
    private float y;
    private float color = Color.WHITE.toFloatBits();
    private final Color tempColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private final BitmapFont.TextBounds textBounds = new BitmapFont.TextBounds();
    private boolean integer = true;
    private int glyphCount = 0;
    private IntArray[] glyphIndices;
    private boolean textChanged;
    private float oldTint = 0.0f;
    private TextMarkup markup = new TextMarkup();
    private int charsCount;

    public BitmapFontCache(BitmapFont font) {
        this(font, font.usesIntegerPositions());
    }

    public BitmapFontCache(BitmapFont font, boolean integer) {
        this.font = font;
        this.integer = integer;
        int regionsLength = font.regions.length;
        if (regionsLength == 0) {
            throw new IllegalArgumentException("The specified font must contain at least one texture page.");
        }
        this.vertexData = new float[regionsLength][];
        this.idx = new int[regionsLength];
        int vertexDataLength = this.vertexData.length;
        if (vertexDataLength > 1) {
            this.glyphIndices = new IntArray[vertexDataLength];
            int n = this.glyphIndices.length;
            for (int i = 0; i < n; ++i) {
                this.glyphIndices[i] = new IntArray();
            }
            this.tmpGlyphCount = new int[vertexDataLength];
        }
    }

    public void setPosition(float x, float y) {
        this.translate(x - this.x, y - this.y);
    }

    public void translate(float xAmount, float yAmount) {
        if (xAmount == 0.0f && yAmount == 0.0f) {
            return;
        }
        if (this.integer) {
            xAmount = Math.round(xAmount);
            yAmount = Math.round(yAmount);
        }
        this.x += xAmount;
        this.y += yAmount;
        int length = this.vertexData.length;
        for (int j = 0; j < length; ++j) {
            float[] vertices = this.vertexData[j];
            int n = this.idx[j];
            for (int i = 0; i < n; i += 5) {
                float[] arrf = vertices;
                int n2 = i;
                arrf[n2] = arrf[n2] + xAmount;
                float[] arrf2 = vertices;
                int n3 = i + 1;
                arrf2[n3] = arrf2[n3] + yAmount;
            }
        }
    }

    private Color setColor(Color color, float floatColor) {
        int intBits = NumberUtils.floatToIntColor(floatColor);
        color.r = (float)(intBits & 255) / 255.0f;
        color.g = (float)(intBits >>> 8 & 255) / 255.0f;
        color.b = (float)(intBits >>> 16 & 255) / 255.0f;
        color.a = (float)(intBits >>> 24 & 255) / 255.0f;
        return color;
    }

    public void tint(Color tint) {
        float floatTint = tint.toFloatBits();
        if (this.textChanged || this.oldTint != floatTint) {
            this.textChanged = false;
            this.oldTint = floatTint;
            this.markup.tint(this, tint);
        }
    }

    public void setColors(float color) {
        int length = this.vertexData.length;
        for (int j = 0; j < length; ++j) {
            float[] vertices = this.vertexData[j];
            int n = this.idx[j];
            for (int i = 2; i < n; i += 5) {
                vertices[i] = color;
            }
        }
    }

    public void setColors(Color tint) {
        float color = tint.toFloatBits();
        int length = this.vertexData.length;
        for (int j = 0; j < length; ++j) {
            float[] vertices = this.vertexData[j];
            int n = this.idx[j];
            for (int i = 2; i < n; i += 5) {
                vertices[i] = color;
            }
        }
    }

    public void setColors(float r, float g, float b, float a) {
        int intBits = (int)(255.0f * a) << 24 | (int)(255.0f * b) << 16 | (int)(255.0f * g) << 8 | (int)(255.0f * r);
        float color = NumberUtils.intToFloatColor(intBits);
        int length = this.vertexData.length;
        for (int j = 0; j < length; ++j) {
            float[] vertices = this.vertexData[j];
            int n = this.idx[j];
            for (int i = 2; i < n; i += 5) {
                vertices[i] = color;
            }
        }
    }

    public void setColors(Color tint, int start, int end) {
        this.setColors(tint.toFloatBits(), start, end);
    }

    public void setColors(float color, int start, int end) {
        if (this.vertexData.length == 1) {
            float[] vertices = this.vertexData[0];
            int n = end * 20;
            for (int i = start * 20 + 2; i < n; i += 5) {
                vertices[i] = color;
            }
        } else {
            for (float[] vertices : this.vertexData) {
                int gInd;
                int n = this.glyphIndices[i].size;
                for (int j = 0; j < n && (gInd = this.glyphIndices[i].items[j]) < end; ++j) {
                    if (gInd < start) continue;
                    for (int off = 0; off < 20; off += 5) {
                        vertices[off + (j * 20 + 2)] = color;
                    }
                }
            }
        }
    }

    public void setColor(Color tint) {
        this.color = tint.toFloatBits();
        this.markup.setDefaultChunk(tint, this.charsCount);
    }

    public void setColor(float r, float g, float b, float a) {
        int intBits = (int)(255.0f * a) << 24 | (int)(255.0f * b) << 16 | (int)(255.0f * g) << 8 | (int)(255.0f * r);
        this.color = NumberUtils.intToFloatColor(intBits);
        this.markup.setDefaultChunk(intBits, this.charsCount);
    }

    public void setColor(float color) {
        this.color = color;
        this.markup.setDefaultChunk(color, this.charsCount);
    }

    public Color getColor() {
        int intBits = NumberUtils.floatToIntColor(this.color);
        Color color = this.tempColor;
        color.r = (float)(intBits & 255) / 255.0f;
        color.g = (float)(intBits >>> 8 & 255) / 255.0f;
        color.b = (float)(intBits >>> 16 & 255) / 255.0f;
        color.a = (float)(intBits >>> 24 & 255) / 255.0f;
        return color;
    }

    public void draw(Batch spriteBatch) {
        TextureRegion[] regions = this.font.getRegions();
        int n = this.vertexData.length;
        for (int j = 0; j < n; ++j) {
            if (this.idx[j] <= 0) continue;
            float[] vertices = this.vertexData[j];
            spriteBatch.draw(regions[j].getTexture(), vertices, 0, this.idx[j]);
        }
    }

    public void draw(Batch spriteBatch, int start, int end) {
        if (this.vertexData.length == 1) {
            spriteBatch.draw(this.font.getRegion().getTexture(), this.vertexData[0], start * 20, (end - start) * 20);
        } else {
            TextureRegion[] regions = this.font.getRegions();
            int pageCount = this.vertexData.length;
            for (int i = 0; i < pageCount; ++i) {
                int glyphIndex;
                int offset = -1;
                int count = 0;
                IntArray currentGlyphIndices = this.glyphIndices[i];
                int n = currentGlyphIndices.size;
                for (int j = 0; j < n && (glyphIndex = currentGlyphIndices.items[j]) < end; ++j) {
                    if (offset == -1 && glyphIndex >= start) {
                        offset = j;
                    }
                    if (glyphIndex < start) continue;
                    ++count;
                }
                if (offset == -1 || count == 0) continue;
                spriteBatch.draw(regions[i].getTexture(), this.vertexData[i], offset * 20, count * 20);
            }
        }
    }

    public void draw(Batch spriteBatch, float alphaModulation) {
        if (alphaModulation == 1.0f) {
            this.draw(spriteBatch);
            return;
        }
        Color color = this.getColor();
        float oldAlpha = color.a;
        color.a *= alphaModulation;
        this.setColors(color);
        this.draw(spriteBatch);
        color.a = oldAlpha;
        this.setColors(color);
    }

    public void clear() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.glyphCount = 0;
        this.charsCount = 0;
        this.markup.clear();
        int n = this.idx.length;
        for (int i = 0; i < n; ++i) {
            if (this.glyphIndices != null) {
                this.glyphIndices[i].clear();
            }
            this.idx[i] = 0;
        }
    }

    private int countGlyphs(CharSequence seq, int start, int end) {
        int count = end - start;
        while (start < end) {
            char ch;
            if ((ch = seq.charAt(start++)) != '[') continue;
            --count;
            if (start >= end || seq.charAt(start) != '[') {
                while (start < end && seq.charAt(start) != ']') {
                    ++start;
                    --count;
                }
                --count;
            }
            ++start;
        }
        return count;
    }

    private void requireSequence(CharSequence seq, int start, int end) {
        if (this.vertexData.length == 1) {
            int newGlyphCount = this.font.markupEnabled ? this.countGlyphs(seq, start, end) : end - start;
            this.require(0, newGlyphCount);
        } else {
            int i;
            int n = this.tmpGlyphCount.length;
            for (i = 0; i < n; ++i) {
                this.tmpGlyphCount[i] = 0;
            }
            while (start < end) {
                BitmapFont.Glyph g;
                char ch;
                if ((ch = seq.charAt(start++)) == '[' && this.font.markupEnabled) {
                    if (start >= end || seq.charAt(start) != '[') {
                        while (start < end && seq.charAt(start) != ']') {
                            ++start;
                        }
                        ++start;
                        continue;
                    }
                    ++start;
                }
                if ((g = this.font.data.getGlyph(ch)) == null) continue;
                int[] arrn = this.tmpGlyphCount;
                int n2 = g.page;
                arrn[n2] = arrn[n2] + 1;
            }
            n = this.tmpGlyphCount.length;
            for (i = 0; i < n; ++i) {
                this.require(i, this.tmpGlyphCount[i]);
            }
        }
    }

    private void require(int page, int glyphCount) {
        if (this.glyphIndices != null && glyphCount > this.glyphIndices[page].items.length) {
            this.glyphIndices[page].ensureCapacity(glyphCount - this.glyphIndices[page].items.length);
        }
        int vertexCount = this.idx[page] + glyphCount * 20;
        float[] vertices = this.vertexData[page];
        if (vertices == null) {
            this.vertexData[page] = new float[vertexCount];
        } else if (vertices.length < vertexCount) {
            float[] newVertices = new float[vertexCount];
            System.arraycopy(vertices, 0, newVertices, 0, this.idx[page]);
            this.vertexData[page] = newVertices;
        }
    }

    private float addToCache(CharSequence str, float x, float y, int start, int end) {
        float startX = x;
        BitmapFont font = this.font;
        BitmapFont.Glyph lastGlyph = null;
        BitmapFont.BitmapFontData data = font.data;
        boolean bl = this.textChanged = start < end;
        if (data.scaleX == 1.0f && data.scaleY == 1.0f) {
            char ch;
            while (start < end) {
                if ((ch = str.charAt(start++)) == '[' && font.markupEnabled) {
                    if (start >= end || str.charAt(start) != '[') {
                        start += TextMarkup.parseColorTag(this.markup, str, this.charsCount, start, end) + 1;
                        this.color = this.markup.getLastColor().toFloatBits();
                        continue;
                    }
                    ++start;
                }
                if ((lastGlyph = data.getGlyph(ch)) == null) continue;
                this.addGlyph(lastGlyph, x + (float)lastGlyph.xoffset, y + (float)lastGlyph.yoffset, lastGlyph.width, lastGlyph.height);
                x += (float)lastGlyph.xadvance;
                break;
            }
            while (start < end) {
                BitmapFont.Glyph g;
                if ((ch = str.charAt(start++)) == '[' && font.markupEnabled) {
                    if (start >= end || str.charAt(start) != '[') {
                        start += TextMarkup.parseColorTag(this.markup, str, this.charsCount, start, end) + 1;
                        this.color = this.markup.getLastColor().toFloatBits();
                        continue;
                    }
                    ++start;
                }
                if ((g = data.getGlyph(ch)) == null) continue;
                lastGlyph = g;
                this.addGlyph(lastGlyph, (x += (float)lastGlyph.getKerning(ch)) + (float)g.xoffset, y + (float)g.yoffset, g.width, g.height);
                x += (float)g.xadvance;
            }
        } else {
            char ch;
            float scaleX = data.scaleX;
            float scaleY = data.scaleY;
            while (start < end) {
                if ((ch = str.charAt(start++)) == '[' && font.markupEnabled) {
                    if (start >= end || str.charAt(start) != '[') {
                        start += TextMarkup.parseColorTag(this.markup, str, this.charsCount, start, end) + 1;
                        this.color = this.markup.getLastColor().toFloatBits();
                        continue;
                    }
                    ++start;
                }
                if ((lastGlyph = data.getGlyph(ch)) == null) continue;
                this.addGlyph(lastGlyph, x + (float)lastGlyph.xoffset * scaleX, y + (float)lastGlyph.yoffset * scaleY, (float)lastGlyph.width * scaleX, (float)lastGlyph.height * scaleY);
                x += (float)lastGlyph.xadvance * scaleX;
                break;
            }
            while (start < end) {
                BitmapFont.Glyph g;
                if ((ch = str.charAt(start++)) == '[' && font.markupEnabled) {
                    if (start >= end || str.charAt(start) != '[') {
                        start += TextMarkup.parseColorTag(this.markup, str, this.charsCount, start, end) + 1;
                        this.color = this.markup.getLastColor().toFloatBits();
                        continue;
                    }
                    ++start;
                }
                if ((g = data.getGlyph(ch)) == null) continue;
                lastGlyph = g;
                this.addGlyph(lastGlyph, (x += (float)lastGlyph.getKerning(ch) * scaleX) + (float)g.xoffset * scaleX, y + (float)g.yoffset * scaleY, (float)g.width * scaleX, (float)g.height * scaleY);
                x += (float)g.xadvance * scaleX;
            }
        }
        return x - startX;
    }

    private void addGlyph(BitmapFont.Glyph glyph, float x, float y, float width, float height) {
        float x2 = x + width;
        float y2 = y + height;
        float u = glyph.u;
        float u2 = glyph.u2;
        float v = glyph.v;
        float v2 = glyph.v2;
        int page = glyph.page;
        if (this.glyphIndices != null) {
            this.glyphIndices[page].add(this.glyphCount++);
        }
        float[] vertices = this.vertexData[page];
        if (this.integer) {
            x = Math.round(x);
            y = Math.round(y);
            x2 = Math.round(x2);
            y2 = Math.round(y2);
        }
        int idx = this.idx[page];
        int[] arrn = this.idx;
        int n = page;
        arrn[n] = arrn[n] + 20;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = this.color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x;
        vertices[idx++] = y2;
        vertices[idx++] = this.color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = this.color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = x2;
        vertices[idx++] = y;
        vertices[idx++] = this.color;
        vertices[idx++] = u2;
        vertices[idx] = v;
        ++this.charsCount;
    }

    public BitmapFont.TextBounds setText(CharSequence str, float x, float y) {
        this.clear();
        return this.addText(str, x, y, 0, str.length());
    }

    public BitmapFont.TextBounds setText(CharSequence str, float x, float y, int start, int end) {
        this.clear();
        return this.addText(str, x, y, start, end);
    }

    public BitmapFont.TextBounds addText(CharSequence str, float x, float y) {
        return this.addText(str, x, y, 0, str.length());
    }

    public BitmapFont.TextBounds addText(CharSequence str, float x, float y, int start, int end) {
        this.requireSequence(str, start, end);
        this.textBounds.width = this.addToCache(str, x, y += this.font.data.ascent, start, end);
        this.textBounds.height = this.font.data.capHeight;
        return this.textBounds;
    }

    public BitmapFont.TextBounds setMultiLineText(CharSequence str, float x, float y) {
        this.clear();
        return this.addMultiLineText(str, x, y, 0.0f, BitmapFont.HAlignment.LEFT);
    }

    public BitmapFont.TextBounds setMultiLineText(CharSequence str, float x, float y, float alignmentWidth, BitmapFont.HAlignment alignment) {
        this.clear();
        return this.addMultiLineText(str, x, y, alignmentWidth, alignment);
    }

    public BitmapFont.TextBounds addMultiLineText(CharSequence str, float x, float y) {
        return this.addMultiLineText(str, x, y, 0.0f, BitmapFont.HAlignment.LEFT);
    }

    public BitmapFont.TextBounds addMultiLineText(CharSequence str, float x, float y, float alignmentWidth, BitmapFont.HAlignment alignment) {
        BitmapFont font = this.font;
        int length = str.length();
        this.requireSequence(str, 0, length);
        float down = font.data.down;
        float maxWidth = 0.0f;
        float startY = y += font.data.ascent;
        int start = 0;
        int numLines = 0;
        while (start < length) {
            float lineWidth;
            int lineEnd = BitmapFont.indexOf(str, '\n', start);
            float xOffset = 0.0f;
            if (alignment != BitmapFont.HAlignment.LEFT) {
                lineWidth = font.getBounds((CharSequence)str, (int)start, (int)lineEnd).width;
                xOffset = alignmentWidth - lineWidth;
                if (alignment == BitmapFont.HAlignment.CENTER) {
                    xOffset /= 2.0f;
                }
            }
            lineWidth = this.addToCache(str, x + xOffset, y, start, lineEnd);
            maxWidth = Math.max(maxWidth, lineWidth);
            start = lineEnd + 1;
            y += down;
            ++numLines;
        }
        this.textBounds.width = maxWidth;
        this.textBounds.height = font.data.capHeight + (float)(numLines - 1) * font.data.lineHeight;
        return this.textBounds;
    }

    public BitmapFont.TextBounds setWrappedText(CharSequence str, float x, float y, float wrapWidth) {
        this.clear();
        return this.addWrappedText(str, x, y, wrapWidth, BitmapFont.HAlignment.LEFT);
    }

    public BitmapFont.TextBounds setWrappedText(CharSequence str, float x, float y, float wrapWidth, BitmapFont.HAlignment alignment) {
        this.clear();
        return this.addWrappedText(str, x, y, wrapWidth, alignment);
    }

    public BitmapFont.TextBounds addWrappedText(CharSequence str, float x, float y, float wrapWidth) {
        return this.addWrappedText(str, x, y, wrapWidth, BitmapFont.HAlignment.LEFT);
    }

    public BitmapFont.TextBounds addWrappedText(CharSequence str, float x, float y, float wrapWidth, BitmapFont.HAlignment alignment) {
        BitmapFont font = this.font;
        int length = str.length();
        this.requireSequence(str, 0, length);
        y += font.data.ascent;
        float down = font.data.down;
        if (wrapWidth <= 0.0f) {
            wrapWidth = 2.14748365E9f;
        }
        float maxWidth = 0.0f;
        int start = 0;
        int numLines = 0;
        while (start < length) {
            int lineEnd;
            int newLine = BitmapFont.indexOf(str, '\n', start);
            int nextStart = lineEnd + 1;
            if (lineEnd < newLine) {
                for (lineEnd = start + font.computeVisibleGlyphs((CharSequence)str, (int)start, (int)newLine, (float)wrapWidth); lineEnd > start && !BitmapFont.isWhitespace(str.charAt(lineEnd)) && !font.isBreakChar(str.charAt(lineEnd - 1)); --lineEnd) {
                }
                if (lineEnd == start) {
                    if (nextStart > start + 1) {
                        --nextStart;
                    }
                    lineEnd = nextStart;
                } else {
                    char c;
                    for (nextStart = lineEnd; nextStart < length && BitmapFont.isWhitespace(c = str.charAt(nextStart)); ++nextStart) {
                        if (c != '\n') continue;
                        break;
                    }
                    while (lineEnd > start && BitmapFont.isWhitespace(str.charAt(lineEnd - 1))) {
                        --lineEnd;
                    }
                }
            }
            if (lineEnd > start) {
                float lineWidth;
                float xOffset = 0.0f;
                if (alignment != BitmapFont.HAlignment.LEFT) {
                    lineWidth = font.getBounds((CharSequence)str, (int)start, (int)lineEnd).width;
                    xOffset = wrapWidth - lineWidth;
                    if (alignment == BitmapFont.HAlignment.CENTER) {
                        xOffset /= 2.0f;
                    }
                }
                lineWidth = this.addToCache(str, x + xOffset, y, start, lineEnd);
                maxWidth = Math.max(maxWidth, lineWidth);
            }
            start = nextStart;
            y += down;
            ++numLines;
        }
        this.textBounds.width = maxWidth;
        this.textBounds.height = font.data.capHeight + (float)(numLines - 1) * font.data.lineHeight;
        return this.textBounds;
    }

    public void setBreakChars(char[] breakChars) {
        this.font.setBreakChars(breakChars);
    }

    public BitmapFont.TextBounds getBounds() {
        return this.textBounds;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public BitmapFont getFont() {
        return this.font;
    }

    public void setUseIntegerPositions(boolean use) {
        this.integer = use;
    }

    public boolean usesIntegerPositions() {
        return this.integer;
    }

    public float[] getVertices() {
        return this.getVertices(0);
    }

    public float[] getVertices(int page) {
        return this.vertexData[page];
    }

    public int getCharsCount() {
        return this.charsCount;
    }
}

