/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.StringBuilder;

public class Label
extends Widget {
    private static final Color tempColor = new Color();
    private LabelStyle style;
    private final BitmapFont.TextBounds bounds = new BitmapFont.TextBounds();
    private final StringBuilder text = new StringBuilder();
    private StringBuilder tempText;
    private BitmapFontCache cache;
    private int labelAlign = 8;
    private BitmapFont.HAlignment lineAlign = BitmapFont.HAlignment.LEFT;
    private boolean wrap;
    private float lastPrefHeight;
    private boolean sizeInvalid = true;
    private float fontScaleX = 1.0f;
    private float fontScaleY = 1.0f;
    private boolean ellipsis;

    public Label(CharSequence text, Skin skin) {
        this(text, skin.get(LabelStyle.class));
    }

    public Label(CharSequence text, Skin skin, String styleName) {
        this(text, skin.get(styleName, LabelStyle.class));
    }

    public Label(CharSequence text, Skin skin, String fontName, Color color) {
        this(text, new LabelStyle(skin.getFont(fontName), color));
    }

    public Label(CharSequence text, Skin skin, String fontName, String colorName) {
        this(text, new LabelStyle(skin.getFont(fontName), skin.getColor(colorName)));
    }

    public Label(CharSequence text, LabelStyle style) {
        if (text != null) {
            this.text.append(text);
        }
        this.setStyle(style);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }

    public void setStyle(LabelStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        if (style.font == null) {
            throw new IllegalArgumentException("Missing LabelStyle font.");
        }
        this.style = style;
        this.cache = new BitmapFontCache(style.font, style.font.usesIntegerPositions());
        this.invalidateHierarchy();
    }

    public LabelStyle getStyle() {
        return this.style;
    }

    public void setText(CharSequence newText) {
        if (newText == null) {
            newText = "";
        }
        if (newText instanceof StringBuilder) {
            if (this.text.equals(newText)) {
                return;
            }
            this.text.setLength(0);
            this.text.append((StringBuilder)newText);
        } else {
            if (this.textEquals(newText)) {
                return;
            }
            this.text.setLength(0);
            this.text.append(newText);
        }
        this.invalidateHierarchy();
    }

    public boolean textEquals(CharSequence other) {
        int length = this.text.length;
        char[] chars = this.text.chars;
        if (length != other.length()) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (chars[i] == other.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public StringBuilder getText() {
        return this.text;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private void scaleAndComputeSize() {
        BitmapFont font = this.cache.getFont();
        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();
        if (this.fontScaleX != 1.0f || this.fontScaleY != 1.0f) {
            font.setScale(this.fontScaleX, this.fontScaleY);
        }
        this.computeSize();
        if (this.fontScaleX != 1.0f || this.fontScaleY != 1.0f) {
            font.setScale(oldScaleX, oldScaleY);
        }
    }

    private void computeSize() {
        this.sizeInvalid = false;
        if (this.wrap) {
            float width = this.getWidth();
            if (this.style.background != null) {
                width -= this.style.background.getLeftWidth() + this.style.background.getRightWidth();
            }
            this.bounds.set(this.cache.getFont().getWrappedBounds(this.text, width));
        } else {
            this.bounds.set(this.cache.getFont().getMultiLineBounds(this.text));
        }
    }

    @Override
    public void layout() {
        StringBuilder text;
        float prefHeight;
        BitmapFont font = this.cache.getFont();
        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();
        if (this.fontScaleX != 1.0f || this.fontScaleY != 1.0f) {
            font.setScale(this.fontScaleX, this.fontScaleY);
        }
        if (this.sizeInvalid) {
            this.computeSize();
        }
        if (this.wrap && (prefHeight = this.getPrefHeight()) != this.lastPrefHeight) {
            this.lastPrefHeight = prefHeight;
            this.invalidateHierarchy();
        }
        float width = this.getWidth();
        float height = this.getHeight();
        if (this.ellipsis && width < this.bounds.width) {
            float ellipseWidth = font.getBounds((CharSequence)"...").width;
            StringBuilder stringBuilder = this.tempText != null ? this.tempText : (this.tempText = new StringBuilder());
            text = stringBuilder;
            text.setLength(0);
            if (width > ellipseWidth) {
                text.append(this.text, 0, font.computeVisibleGlyphs(this.text, 0, this.text.length, width - ellipseWidth));
                text.append("...");
            }
        } else {
            text = this.text;
        }
        Drawable background = this.style.background;
        float x = 0.0f;
        float y = 0.0f;
        if (background != null) {
            x = background.getLeftWidth();
            y = background.getBottomHeight();
            width -= background.getLeftWidth() + background.getRightWidth();
            height -= background.getBottomHeight() + background.getTopHeight();
        }
        if ((this.labelAlign & 2) != 0) {
            y += this.cache.getFont().isFlipped() ? 0.0f : height - this.bounds.height;
            y += this.style.font.getDescent();
        } else if ((this.labelAlign & 4) != 0) {
            y += this.cache.getFont().isFlipped() ? height - this.bounds.height : 0.0f;
            y -= this.style.font.getDescent();
        } else {
            y += (float)((int)((height - this.bounds.height) / 2.0f));
        }
        if (!this.cache.getFont().isFlipped()) {
            y += this.bounds.height;
        }
        if ((this.labelAlign & 8) == 0) {
            x = (this.labelAlign & 16) != 0 ? (x += width - this.bounds.width) : (x += (float)((int)((width - this.bounds.width) / 2.0f)));
        }
        this.cache.setColor(Color.WHITE);
        if (this.wrap) {
            this.cache.setWrappedText(text, x, y, this.bounds.width, this.lineAlign);
        } else {
            this.cache.setMultiLineText(text, x, y, this.bounds.width, this.lineAlign);
        }
        if (this.fontScaleX != 1.0f || this.fontScaleY != 1.0f) {
            font.setScale(oldScaleX, oldScaleY);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.validate();
        Color color = tempColor.set(this.getColor());
        color.a *= parentAlpha;
        if (this.style.background != null) {
            batch.setColor(color.r, color.g, color.b, color.a);
            this.style.background.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        if (this.style.fontColor != null) {
            color.mul(this.style.fontColor);
        }
        this.cache.tint(color);
        this.cache.setPosition(this.getX(), this.getY());
        this.cache.draw(batch);
    }

    @Override
    public float getPrefWidth() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.sizeInvalid) {
            this.scaleAndComputeSize();
        }
        float width = this.bounds.width;
        Drawable background = this.style.background;
        if (background != null) {
            width += background.getLeftWidth() + background.getRightWidth();
        }
        return width;
    }

    @Override
    public float getPrefHeight() {
        if (this.sizeInvalid) {
            this.scaleAndComputeSize();
        }
        float height = this.bounds.height - this.style.font.getDescent() * 2.0f;
        Drawable background = this.style.background;
        if (background != null) {
            height += background.getTopHeight() + background.getBottomHeight();
        }
        return height;
    }

    public BitmapFont.TextBounds getTextBounds() {
        if (this.sizeInvalid) {
            this.scaleAndComputeSize();
        }
        return this.bounds;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
        this.invalidateHierarchy();
    }

    public void setBreakChars(char[] breakChars) {
        this.cache.setBreakChars(breakChars);
        this.invalidateHierarchy();
    }

    public void setAlignment(int alignment) {
        this.setAlignment(alignment, alignment);
    }

    public void setAlignment(int labelAlign, int lineAlign) {
        this.labelAlign = labelAlign;
        this.lineAlign = (lineAlign & 8) != 0 ? BitmapFont.HAlignment.LEFT : ((lineAlign & 16) != 0 ? BitmapFont.HAlignment.RIGHT : BitmapFont.HAlignment.CENTER);
        this.invalidate();
    }

    public void setFontScale(float fontScale) {
        this.fontScaleX = fontScale;
        this.fontScaleY = fontScale;
        this.invalidateHierarchy();
    }

    public void setFontScale(float fontScaleX, float fontScaleY) {
        this.fontScaleX = fontScaleX;
        this.fontScaleY = fontScaleY;
        this.invalidateHierarchy();
    }

    public float getFontScaleX() {
        return this.fontScaleX;
    }

    public void setFontScaleX(float fontScaleX) {
        this.fontScaleX = fontScaleX;
        this.invalidateHierarchy();
    }

    public float getFontScaleY() {
        return this.fontScaleY;
    }

    public void setFontScaleY(float fontScaleY) {
        this.fontScaleY = fontScaleY;
        this.invalidateHierarchy();
    }

    public void setEllipsis(boolean ellipsis) {
        this.ellipsis = ellipsis;
    }

    protected BitmapFontCache getBitmapFontCache() {
        return this.cache;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + this.text;
    }

    public static class LabelStyle {
        public BitmapFont font;
        public Color fontColor;
        public Drawable background;

        public LabelStyle() {
        }

        public LabelStyle(BitmapFont font, Color fontColor) {
            this.font = font;
            this.fontColor = fontColor;
        }

        public LabelStyle(LabelStyle style) {
            this.font = style.font;
            if (style.fontColor != null) {
                this.fontColor = new Color(style.fontColor);
            }
            this.background = style.background;
        }
    }

}

