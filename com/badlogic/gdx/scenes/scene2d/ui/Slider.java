/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class Slider
extends ProgressBar {
    int draggingPointer = -1;

    public Slider(float min, float max, float stepSize, boolean vertical, Skin skin) {
        this(min, max, stepSize, vertical, skin.get("default-" + (vertical ? "vertical" : "horizontal"), SliderStyle.class));
    }

    public Slider(float min, float max, float stepSize, boolean vertical, Skin skin, String styleName) {
        this(min, max, stepSize, vertical, skin.get(styleName, SliderStyle.class));
    }

    public Slider(float min, float max, float stepSize, boolean vertical, SliderStyle style) {
        super(min, max, stepSize, vertical, style);
        this.shiftIgnoresSnap = true;
        this.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Slider.this.disabled) {
                    return false;
                }
                if (Slider.this.draggingPointer != -1) {
                    return false;
                }
                Slider.this.draggingPointer = pointer;
                Slider.this.calculatePositionAndValue(x, y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer != Slider.this.draggingPointer) {
                    return;
                }
                Slider.this.draggingPointer = -1;
                if (!Slider.this.calculatePositionAndValue(x, y)) {
                    ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
                    Slider.this.fire(changeEvent);
                    Pools.free(changeEvent);
                }
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                Slider.this.calculatePositionAndValue(x, y);
            }
        });
    }

    public void setStyle(SliderStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        if (!(style instanceof SliderStyle)) {
            throw new IllegalArgumentException("style must be a SliderStyle.");
        }
        super.setStyle(style);
    }

    @Override
    public SliderStyle getStyle() {
        return (SliderStyle)super.getStyle();
    }

    boolean calculatePositionAndValue(float x, float y) {
        float value;
        SliderStyle style = this.getStyle();
        Drawable knob = this.disabled && style.disabledKnob != null ? style.disabledKnob : style.knob;
        Drawable bg = this.disabled && style.disabledBackground != null ? style.disabledBackground : style.background;
        float oldPosition = this.position;
        float min = this.getMinValue();
        float max = this.getMaxValue();
        if (this.vertical) {
            float height = this.getHeight() - bg.getTopHeight() - bg.getBottomHeight();
            float knobHeight = knob == null ? 0.0f : knob.getMinHeight();
            this.position = y - bg.getBottomHeight() - knobHeight * 0.5f;
            value = min + (max - min) * (this.position / (height - knobHeight));
            this.position = Math.max(0.0f, this.position);
            this.position = Math.min(height - knobHeight, this.position);
        } else {
            float width = this.getWidth() - bg.getLeftWidth() - bg.getRightWidth();
            float knobWidth = knob == null ? 0.0f : knob.getMinWidth();
            this.position = x - bg.getLeftWidth() - knobWidth * 0.5f;
            value = min + (max - min) * (this.position / (width - knobWidth));
            this.position = Math.max(0.0f, this.position);
            this.position = Math.min(width - knobWidth, this.position);
        }
        float oldValue = value;
        boolean valueSet = this.setValue(value);
        if (value == oldValue) {
            this.position = oldPosition;
        }
        return valueSet;
    }

    public boolean isDragging() {
        return this.draggingPointer != -1;
    }

    public static class SliderStyle
    extends ProgressBar.ProgressBarStyle {
        public SliderStyle() {
        }

        public SliderStyle(Drawable background, Drawable knob) {
            super(background, knob);
        }

        public SliderStyle(SliderStyle style) {
            super(style);
        }
    }

}

