/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

public class BitmapFont
implements Disposable {
    private static final int LOG2_PAGE_SIZE = 9;
    private static final int PAGE_SIZE = 512;
    private static final int PAGES = 128;
    public static final char[] xChars = new char[]{'x', 'e', 'a', 'o', 'n', 's', 'r', 'c', 'u', 'm', 'v', 'w', 'z'};
    public static final char[] capChars = new char[]{'M', 'N', 'B', 'D', 'C', 'E', 'F', 'K', 'A', 'G', 'H', 'I', 'J', 'L', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    final BitmapFontData data;
    TextureRegion[] regions;
    private final BitmapFontCache cache;
    private boolean flipped;
    private boolean integer;
    private boolean ownsTexture;
    boolean markupEnabled;
    private char[] breakChars;

    public BitmapFont() {
        this(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"), false, true);
    }

    public BitmapFont(boolean flip) {
        this(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"), flip, true);
    }

    public BitmapFont(FileHandle fontFile, TextureRegion region) {
        this(fontFile, region, false);
    }

    public BitmapFont(FileHandle fontFile, TextureRegion region, boolean flip) {
        this(new BitmapFontData(fontFile, flip), region, true);
    }

    public BitmapFont(FileHandle fontFile) {
        this(fontFile, false);
    }

    public BitmapFont(FileHandle fontFile, boolean flip) {
        this(new BitmapFontData(fontFile, flip), (TextureRegion)null, true);
    }

    public BitmapFont(FileHandle fontFile, FileHandle imageFile, boolean flip) {
        this(fontFile, imageFile, flip, true);
    }

    public BitmapFont(FileHandle fontFile, FileHandle imageFile, boolean flip, boolean integer) {
        this(new BitmapFontData(fontFile, flip), new TextureRegion(new Texture(imageFile, false)), integer);
        this.ownsTexture = true;
    }

    public BitmapFont(BitmapFontData data, TextureRegion region, boolean integer) {
        TextureRegion[] arrtextureRegion;
        if (region != null) {
            TextureRegion[] arrtextureRegion2 = new TextureRegion[1];
            arrtextureRegion = arrtextureRegion2;
            arrtextureRegion2[0] = region;
        } else {
            arrtextureRegion = null;
        }
        this(data, arrtextureRegion, integer);
    }

    public BitmapFont(BitmapFontData data, TextureRegion[] regions, boolean integer) {
        if (regions == null || regions.length == 0) {
            this.regions = new TextureRegion[data.imagePaths.length];
            for (int i = 0; i < this.regions.length; ++i) {
                this.regions[i] = data.fontFile == null ? new TextureRegion(new Texture(Gdx.files.internal(data.imagePaths[i]), false)) : new TextureRegion(new Texture(Gdx.files.getFileHandle(data.imagePaths[i], data.fontFile.type()), false));
            }
            this.ownsTexture = true;
        } else {
            this.regions = regions;
            this.ownsTexture = false;
        }
        this.cache = new BitmapFontCache(this);
        this.cache.setUseIntegerPositions(integer);
        this.flipped = data.flipped;
        this.data = data;
        this.integer = integer;
        this.load(data);
    }

    private void load(BitmapFontData data) {
        for (Glyph[] page : data.glyphs) {
            if (page == null) continue;
            for (Glyph glyph : page) {
                if (glyph == null) continue;
                TextureRegion region = this.regions[glyph.page];
                if (region == null) {
                    throw new IllegalArgumentException("BitmapFont texture region array cannot contain null elements.");
                }
                float invTexWidth = 1.0f / (float)region.getTexture().getWidth();
                float invTexHeight = 1.0f / (float)region.getTexture().getHeight();
                float offsetX = 0.0f;
                float offsetY = 0.0f;
                float u = region.u;
                float v = region.v;
                float regionWidth = region.getRegionWidth();
                float regionHeight = region.getRegionHeight();
                if (region instanceof TextureAtlas.AtlasRegion) {
                    TextureAtlas.AtlasRegion atlasRegion = (TextureAtlas.AtlasRegion)region;
                    offsetX = atlasRegion.offsetX;
                    offsetY = (float)(atlasRegion.originalHeight - atlasRegion.packedHeight) - atlasRegion.offsetY;
                }
                float x = glyph.srcX;
                float x2 = glyph.srcX + glyph.width;
                float y = glyph.srcY;
                float y2 = glyph.srcY + glyph.height;
                if (offsetX > 0.0f) {
                    if ((x -= offsetX) < 0.0f) {
                        glyph.width = (int)((float)glyph.width + x);
                        glyph.xoffset = (int)((float)glyph.xoffset - x);
                        x = 0.0f;
                    }
                    if ((x2 -= offsetX) > regionWidth) {
                        glyph.width = (int)((float)glyph.width - (x2 - regionWidth));
                        x2 = regionWidth;
                    }
                }
                if (offsetY > 0.0f) {
                    if ((y -= offsetY) < 0.0f) {
                        glyph.height = (int)((float)glyph.height + y);
                        y = 0.0f;
                    }
                    if ((y2 -= offsetY) > regionHeight) {
                        float amount = y2 - regionHeight;
                        glyph.height = (int)((float)glyph.height - amount);
                        glyph.yoffset = (int)((float)glyph.yoffset + amount);
                        y2 = regionHeight;
                    }
                }
                glyph.u = u + x * invTexWidth;
                glyph.u2 = u + x2 * invTexWidth;
                if (data.flipped) {
                    glyph.v = v + y * invTexHeight;
                    glyph.v2 = v + y2 * invTexHeight;
                    continue;
                }
                glyph.v2 = v + y * invTexHeight;
                glyph.v = v + y2 * invTexHeight;
            }
        }
    }

    public TextBounds draw(Batch batch, CharSequence str, float x, float y) {
        this.cache.clear();
        TextBounds bounds = this.cache.addText(str, x, y, 0, str.length());
        this.cache.draw(batch);
        return bounds;
    }

    public TextBounds draw(Batch batch, CharSequence str, float x, float y, int start, int end) {
        this.cache.clear();
        TextBounds bounds = this.cache.addText(str, x, y, start, end);
        this.cache.draw(batch);
        return bounds;
    }

    public TextBounds drawMultiLine(Batch batch, CharSequence str, float x, float y) {
        this.cache.clear();
        TextBounds bounds = this.cache.addMultiLineText(str, x, y, 0.0f, HAlignment.LEFT);
        this.cache.draw(batch);
        return bounds;
    }

    public TextBounds drawMultiLine(Batch batch, CharSequence str, float x, float y, float alignmentWidth, HAlignment alignment) {
        this.cache.clear();
        TextBounds bounds = this.cache.addMultiLineText(str, x, y, alignmentWidth, alignment);
        this.cache.draw(batch);
        return bounds;
    }

    public TextBounds drawWrapped(Batch batch, CharSequence str, float x, float y, float wrapWidth) {
        this.cache.clear();
        TextBounds bounds = this.cache.addWrappedText(str, x, y, wrapWidth, HAlignment.LEFT);
        this.cache.draw(batch);
        return bounds;
    }

    public TextBounds drawWrapped(Batch batch, CharSequence str, float x, float y, float wrapWidth, HAlignment alignment) {
        this.cache.clear();
        TextBounds bounds = this.cache.addWrappedText(str, x, y, wrapWidth, alignment);
        this.cache.draw(batch);
        return bounds;
    }

    public TextBounds getBounds(CharSequence str) {
        return this.getBounds(str, 0, str.length(), this.cache.getBounds());
    }

    public TextBounds getBounds(CharSequence str, TextBounds textBounds) {
        return this.getBounds(str, 0, str.length(), textBounds);
    }

    public TextBounds getBounds(CharSequence str, int start, int end) {
        return this.getBounds(str, start, end, this.cache.getBounds());
    }

    public TextBounds getBounds(CharSequence str, int start, int end, TextBounds textBounds) {
        char ch;
        BitmapFontData data = this.data;
        int width = 0;
        Glyph lastGlyph = null;
        while (start < end) {
            if ((ch = str.charAt(start++)) == '[' && this.markupEnabled) {
                if (start >= end || str.charAt(start) != '[') {
                    while (start < end && str.charAt(start) != ']') {
                        ++start;
                    }
                    ++start;
                    continue;
                }
                ++start;
            }
            if ((lastGlyph = data.getGlyph(ch)) == null) continue;
            width = lastGlyph.xadvance;
            break;
        }
        while (start < end) {
            Glyph g;
            if ((ch = str.charAt(start++)) == '[' && this.markupEnabled) {
                if (start >= end || str.charAt(start) != '[') {
                    while (start < end && str.charAt(start) != ']') {
                        ++start;
                    }
                    ++start;
                    continue;
                }
                ++start;
            }
            if ((g = data.getGlyph(ch)) == null) continue;
            width += lastGlyph.getKerning(ch);
            lastGlyph = g;
            width += g.xadvance;
        }
        textBounds.width = (float)width * data.scaleX;
        textBounds.height = data.capHeight;
        return textBounds;
    }

    public TextBounds getMultiLineBounds(CharSequence str) {
        return this.getMultiLineBounds(str, this.cache.getBounds());
    }

    public TextBounds getMultiLineBounds(CharSequence str, TextBounds textBounds) {
        int start = 0;
        float maxWidth = 0.0f;
        int numLines = 0;
        int length = str.length();
        while (start < length) {
            int lineEnd = BitmapFont.indexOf(str, '\n', start);
            float lineWidth = this.getBounds((CharSequence)str, (int)start, (int)lineEnd).width;
            maxWidth = Math.max(maxWidth, lineWidth);
            start = lineEnd + 1;
            ++numLines;
        }
        textBounds.width = maxWidth;
        textBounds.height = this.data.capHeight + (float)(numLines - 1) * this.data.lineHeight;
        return textBounds;
    }

    public TextBounds getWrappedBounds(CharSequence str, float wrapWidth) {
        return this.getWrappedBounds(str, wrapWidth, this.cache.getBounds());
    }

    public TextBounds getWrappedBounds(CharSequence str, float wrapWidth, TextBounds textBounds) {
        if (wrapWidth <= 0.0f) {
            wrapWidth = 2.14748365E9f;
        }
        int start = 0;
        int numLines = 0;
        int length = str.length();
        float maxWidth = 0.0f;
        while (start < length) {
            int lineEnd;
            int newLine = BitmapFont.indexOf(str, '\n', start);
            int nextStart = lineEnd + 1;
            if (lineEnd < newLine) {
                for (lineEnd = start + this.computeVisibleGlyphs((CharSequence)str, (int)start, (int)newLine, (float)wrapWidth); lineEnd > start && !BitmapFont.isWhitespace(str.charAt(lineEnd)) && !this.isBreakChar(str.charAt(lineEnd - 1)); --lineEnd) {
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
                float lineWidth = this.getBounds((CharSequence)str, (int)start, (int)lineEnd).width;
                maxWidth = Math.max(maxWidth, lineWidth);
            }
            start = nextStart;
            ++numLines;
        }
        textBounds.width = maxWidth;
        textBounds.height = this.data.capHeight + (float)(numLines - 1) * this.data.lineHeight;
        return textBounds;
    }

    public void computeGlyphAdvancesAndPositions(CharSequence str, FloatArray glyphAdvances, FloatArray glyphPositions) {
        int index;
        glyphAdvances.clear();
        glyphPositions.clear();
        int end = str.length();
        float width = 0.0f;
        Glyph lastGlyph = null;
        BitmapFontData data = this.data;
        if (data.scaleX == 1.0f) {
            for (index = 0; index < end; ++index) {
                char ch = str.charAt(index);
                Glyph g = data.getGlyph(ch);
                if (g == null) continue;
                if (lastGlyph != null) {
                    width += (float)lastGlyph.getKerning(ch);
                }
                lastGlyph = g;
                glyphAdvances.add(g.xadvance);
                glyphPositions.add(width);
                width += (float)g.xadvance;
            }
            glyphAdvances.add(0.0f);
            glyphPositions.add(width);
        } else {
            float scaleX = this.data.scaleX;
            while (index < end) {
                char ch = str.charAt(index);
                Glyph g = data.getGlyph(ch);
                if (g != null) {
                    if (lastGlyph != null) {
                        width += (float)lastGlyph.getKerning(ch) * scaleX;
                    }
                    lastGlyph = g;
                    float xadvance = (float)g.xadvance * scaleX;
                    glyphAdvances.add(xadvance);
                    glyphPositions.add(width);
                    width += xadvance;
                }
                ++index;
            }
            glyphAdvances.add(0.0f);
            glyphPositions.add(width);
        }
    }

    public int computeVisibleGlyphs(CharSequence str, int start, int end, float availableWidth) {
        int index;
        BitmapFontData data = this.data;
        float width = 0.0f;
        Glyph lastGlyph = null;
        availableWidth /= data.scaleX;
        for (index = start; index < end; ++index) {
            char ch = str.charAt(index);
            if (ch == '[' && this.markupEnabled && (++index >= end || str.charAt(index) != '[')) {
                while (index < end && str.charAt(index) != ']') {
                    ++index;
                }
                continue;
            }
            Glyph g = data.getGlyph(ch);
            if (g == null) continue;
            if (lastGlyph != null) {
                width += (float)lastGlyph.getKerning(ch);
            }
            if (width + (float)g.xadvance - availableWidth > 0.001f) break;
            width += (float)g.xadvance;
            lastGlyph = g;
        }
        return index - start;
    }

    public void setColor(float color) {
        this.cache.setColor(color);
    }

    public void setColor(Color color) {
        this.cache.setColor(color);
    }

    public void setColor(float r, float g, float b, float a) {
        this.cache.setColor(r, g, b, a);
    }

    public Color getColor() {
        return this.cache.getColor();
    }

    public void setScale(float scaleX, float scaleY) {
        if (scaleX == 0.0f) {
            throw new IllegalArgumentException("scaleX cannot be 0.");
        }
        if (scaleY == 0.0f) {
            throw new IllegalArgumentException("scaleY cannot be 0.");
        }
        BitmapFontData data = this.data;
        float x = scaleX / data.scaleX;
        float y = scaleY / data.scaleY;
        data.lineHeight *= y;
        data.spaceWidth *= x;
        data.xHeight *= y;
        data.capHeight *= y;
        data.ascent *= y;
        data.descent *= y;
        data.down *= y;
        data.scaleX = scaleX;
        data.scaleY = scaleY;
    }

    public void setScale(float scaleXY) {
        this.setScale(scaleXY, scaleXY);
    }

    public void scale(float amount) {
        this.setScale(this.data.scaleX + amount, this.data.scaleY + amount);
    }

    public float getScaleX() {
        return this.data.scaleX;
    }

    public float getScaleY() {
        return this.data.scaleY;
    }

    public TextureRegion getRegion() {
        return this.regions[0];
    }

    public TextureRegion[] getRegions() {
        return this.regions;
    }

    public TextureRegion getRegion(int index) {
        return this.regions[index];
    }

    public float getLineHeight() {
        return this.data.lineHeight;
    }

    public float getSpaceWidth() {
        return this.data.spaceWidth;
    }

    public float getXHeight() {
        return this.data.xHeight;
    }

    public float getCapHeight() {
        return this.data.capHeight;
    }

    public float getAscent() {
        return this.data.ascent;
    }

    public float getDescent() {
        return this.data.descent;
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public boolean isMarkupEnabled() {
        return this.markupEnabled;
    }

    public void setMarkupEnabled(boolean markupEnabled) {
        this.markupEnabled = markupEnabled;
    }

    @Override
    public void dispose() {
        if (this.ownsTexture) {
            for (int i = 0; i < this.regions.length; ++i) {
                this.regions[i].getTexture().dispose();
            }
        }
    }

    public void setFixedWidthGlyphs(CharSequence glyphs) {
        Glyph g;
        int index;
        BitmapFontData data = this.data;
        int maxAdvance = 0;
        int end = glyphs.length();
        for (index = 0; index < end; ++index) {
            g = data.getGlyph(glyphs.charAt(index));
            if (g == null || g.xadvance <= maxAdvance) continue;
            maxAdvance = g.xadvance;
        }
        end = glyphs.length();
        for (index = 0; index < end; ++index) {
            g = data.getGlyph(glyphs.charAt(index));
            if (g == null) continue;
            g.xoffset += (maxAdvance - g.xadvance) / 2;
            g.xadvance = maxAdvance;
            g.kerning = null;
        }
    }

    public boolean containsCharacter(char character) {
        return this.data.getGlyph(character) != null;
    }

    public void setUseIntegerPositions(boolean integer) {
        this.integer = integer;
        this.cache.setUseIntegerPositions(integer);
    }

    public boolean usesIntegerPositions() {
        return this.integer;
    }

    public BitmapFontCache getCache() {
        return this.cache;
    }

    public BitmapFontData getData() {
        return this.data;
    }

    public boolean ownsTexture() {
        return this.ownsTexture;
    }

    public void setOwnsTexture(boolean ownsTexture) {
        this.ownsTexture = ownsTexture;
    }

    public String toString() {
        if (this.data.fontFile != null) {
            return this.data.fontFile.nameWithoutExtension();
        }
        return super.toString();
    }

    static int indexOf(CharSequence text, char ch, int start) {
        int n = text.length();
        while (start < n) {
            if (text.charAt(start) == ch) {
                return start;
            }
            ++start;
        }
        return n;
    }

    public void setBreakChars(char[] breakChars) {
        this.breakChars = breakChars;
    }

    public boolean isBreakChar(char c) {
        if (this.breakChars == null) {
            return false;
        }
        for (char br : this.breakChars) {
            if (c != br) continue;
            return true;
        }
        return false;
    }

    static boolean isWhitespace(char c) {
        switch (c) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': {
                return true;
            }
        }
        return false;
    }

    public static class BitmapFontData {
        @Deprecated
        public String imagePath;
        public String[] imagePaths;
        public FileHandle fontFile;
        public boolean flipped;
        public float lineHeight;
        public float capHeight;
        public float ascent;
        public float descent;
        public float down;
        public float scaleX;
        public float scaleY;
        public final Glyph[][] glyphs;
        public float spaceWidth;
        public float xHeight;

        public BitmapFontData() {
            this.capHeight = 1.0f;
            this.scaleX = 1.0f;
            this.scaleY = 1.0f;
            this.glyphs = new Glyph[128][];
            this.xHeight = 1.0f;
        }

        public BitmapFontData(FileHandle fontFile, boolean flip) {
            this.capHeight = 1.0f;
            this.scaleX = 1.0f;
            this.scaleY = 1.0f;
            this.glyphs = new Glyph[128][];
            this.xHeight = 1.0f;
            this.fontFile = fontFile;
            this.flipped = flip;
            BufferedReader reader = new BufferedReader(new InputStreamReader(fontFile.read()), 512);
            try {
                reader.readLine();
                String line = reader.readLine();
                if (line == null) {
                    throw new GdxRuntimeException("File is empty.");
                }
                String[] common = line.split(" ", 7);
                if (common.length < 3) {
                    throw new GdxRuntimeException("Invalid header.");
                }
                if (!common[1].startsWith("lineHeight=")) {
                    throw new GdxRuntimeException("Missing: lineHeight");
                }
                this.lineHeight = Integer.parseInt(common[1].substring(11));
                if (!common[2].startsWith("base=")) {
                    throw new GdxRuntimeException("Missing: base");
                }
                float baseLine = Integer.parseInt(common[2].substring(5));
                int pageCount = 1;
                if (common.length >= 6 && common[5] != null && common[5].startsWith("pages=")) {
                    try {
                        pageCount = Math.max(1, Integer.parseInt(common[5].substring(6)));
                    }
                    catch (NumberFormatException ignored) {
                        // empty catch block
                    }
                }
                this.imagePaths = new String[pageCount];
                for (int p = 0; p < pageCount; ++p) {
                    line = reader.readLine();
                    if (line == null) {
                        throw new GdxRuntimeException("Missing additional page definitions.");
                    }
                    String[] pageLine = line.split(" ", 4);
                    if (!pageLine[2].startsWith("file=")) {
                        throw new GdxRuntimeException("Missing: file");
                    }
                    if (pageLine[1].startsWith("id=")) {
                        try {
                            int pageID = Integer.parseInt(pageLine[1].substring(3));
                            if (pageID != p) {
                                throw new GdxRuntimeException("Page IDs must be indices starting at 0: " + pageLine[1].substring(3));
                            }
                        }
                        catch (NumberFormatException ex) {
                            throw new GdxRuntimeException("Invalid page id: " + pageLine[1].substring(3), ex);
                        }
                    }
                    String fileName = null;
                    fileName = pageLine[2].endsWith("\"") ? pageLine[2].substring(6, pageLine[2].length() - 1) : pageLine[2].substring(5, pageLine[2].length());
                    String path = fontFile.parent().child(fileName).path().replaceAll("\\\\", "/");
                    if (this.imagePath == null) {
                        this.imagePath = path;
                    }
                    this.imagePaths[p] = path;
                }
                this.descent = 0.0f;
                while ((line = reader.readLine()) != null && !line.startsWith("kernings ")) {
                    if (!line.startsWith("char ")) continue;
                    Glyph glyph = new Glyph();
                    StringTokenizer tokens = new StringTokenizer(line, " =");
                    tokens.nextToken();
                    tokens.nextToken();
                    int ch = Integer.parseInt(tokens.nextToken());
                    if (ch > 65535) continue;
                    this.setGlyph(ch, glyph);
                    glyph.id = ch;
                    tokens.nextToken();
                    glyph.srcX = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.srcY = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.width = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.height = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.xoffset = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.yoffset = flip ? Integer.parseInt(tokens.nextToken()) : - glyph.height + Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.xadvance = Integer.parseInt(tokens.nextToken());
                    if (tokens.hasMoreTokens()) {
                        tokens.nextToken();
                    }
                    if (tokens.hasMoreTokens()) {
                        try {
                            glyph.page = Integer.parseInt(tokens.nextToken());
                        }
                        catch (NumberFormatException ignored) {
                            // empty catch block
                        }
                    }
                    if (glyph.width <= 0 || glyph.height <= 0) continue;
                    this.descent = Math.min(baseLine + (float)glyph.yoffset, this.descent);
                }
                while ((line = reader.readLine()) != null && line.startsWith("kerning ")) {
                    StringTokenizer tokens = new StringTokenizer(line, " =");
                    tokens.nextToken();
                    tokens.nextToken();
                    int first = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    int second = Integer.parseInt(tokens.nextToken());
                    if (first < 0 || first > 65535 || second < 0 || second > 65535) continue;
                    Glyph glyph = this.getGlyph((char)first);
                    tokens.nextToken();
                    int amount = Integer.parseInt(tokens.nextToken());
                    if (glyph == null) continue;
                    glyph.setKerning(second, amount);
                }
                Glyph spaceGlyph = this.getGlyph(' ');
                if (spaceGlyph == null) {
                    spaceGlyph = new Glyph();
                    spaceGlyph.id = 32;
                    Glyph xadvanceGlyph = this.getGlyph('l');
                    if (xadvanceGlyph == null) {
                        xadvanceGlyph = this.getFirstGlyph();
                    }
                    spaceGlyph.xadvance = xadvanceGlyph.xadvance;
                    this.setGlyph(32, spaceGlyph);
                }
                this.spaceWidth = spaceGlyph != null ? (float)(spaceGlyph.xadvance + spaceGlyph.width) : 1.0f;
                Glyph xGlyph = null;
                for (int i = 0; i < BitmapFont.xChars.length && (xGlyph = this.getGlyph(BitmapFont.xChars[i])) == null; ++i) {
                }
                if (xGlyph == null) {
                    xGlyph = this.getFirstGlyph();
                }
                this.xHeight = xGlyph.height;
                Glyph capGlyph = null;
                for (int i = 0; i < BitmapFont.capChars.length && (capGlyph = this.getGlyph(BitmapFont.capChars[i])) == null; ++i) {
                }
                if (capGlyph == null) {
                    for (Glyph[] page : this.glyphs) {
                        if (page == null) continue;
                        for (Glyph glyph : page) {
                            if (glyph == null || glyph.height == 0 || glyph.width == 0) continue;
                            this.capHeight = Math.max(this.capHeight, (float)glyph.height);
                        }
                    }
                } else {
                    this.capHeight = capGlyph.height;
                }
                this.ascent = baseLine - this.capHeight;
                this.down = - this.lineHeight;
                if (flip) {
                    this.ascent = - this.ascent;
                    this.down = - this.down;
                }
            }
            catch (Exception ex) {
                throw new GdxRuntimeException("Error loading font file: " + fontFile, ex);
            }
            finally {
                StreamUtils.closeQuietly(reader);
            }
        }

        public void setLineHeight(float height) {
            this.lineHeight = height * this.scaleY;
            this.down = this.flipped ? this.lineHeight : - this.lineHeight;
        }

        public void setGlyph(int ch, Glyph glyph) {
            Glyph[] page = this.glyphs[ch / 512];
            if (page == null) {
                page = new Glyph[512];
                this.glyphs[ch / 512] = page;
            }
            page[ch & 511] = glyph;
        }

        public Glyph getFirstGlyph() {
            for (Glyph[] page : this.glyphs) {
                if (page == null) continue;
                for (Glyph glyph : page) {
                    if (glyph == null || glyph.height == 0 || glyph.width == 0) continue;
                    return glyph;
                }
            }
            throw new GdxRuntimeException("No glyphs found.");
        }

        public Glyph getGlyph(char ch) {
            Glyph[] page = this.glyphs[ch / 512];
            if (page != null) {
                return page[ch & 511];
            }
            return null;
        }

        @Deprecated
        public String getImagePath() {
            return this.imagePath;
        }

        public String getImagePath(int index) {
            return this.imagePaths[index];
        }

        public String[] getImagePaths() {
            return this.imagePaths;
        }

        public FileHandle getFontFile() {
            return this.fontFile;
        }
    }

    public static enum HAlignment {
        LEFT,
        CENTER,
        RIGHT;
        

        private HAlignment() {
        }
    }

    public static class TextBounds {
        public float width;
        public float height;

        public TextBounds() {
        }

        public TextBounds(TextBounds bounds) {
            this.set(bounds);
        }

        public void set(TextBounds bounds) {
            this.width = bounds.width;
            this.height = bounds.height;
        }
    }

    public static class Glyph {
        public int id;
        public int srcX;
        public int srcY;
        public int width;
        public int height;
        public float u;
        public float v;
        public float u2;
        public float v2;
        public int xoffset;
        public int yoffset;
        public int xadvance;
        public byte[][] kerning;
        public int page = 0;

        public int getKerning(char ch) {
            byte[] page;
            if (this.kerning != null && (page = this.kerning[ch >>> 9]) != null) {
                return page[ch & 511];
            }
            return 0;
        }

        public void setKerning(int ch, int value) {
            byte[] page;
            if (this.kerning == null) {
                this.kerning = new byte[128][];
            }
            if ((page = this.kerning[ch >>> 9]) == null) {
                page = new byte[512];
                this.kerning[ch >>> 9] = page;
            }
            page[ch & 511] = (byte)value;
        }
    }

}

