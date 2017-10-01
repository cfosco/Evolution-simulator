/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

public class SpriteDrawable
extends BaseDrawable
implements TransformDrawable {
    private Sprite sprite;

    public SpriteDrawable() {
    }

    public SpriteDrawable(Sprite sprite) {
        this.setSprite(sprite);
    }

    public SpriteDrawable(SpriteDrawable drawable) {
        super(drawable);
        this.setSprite(drawable.sprite);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        this.draw(batch, x, y, width / 2.0f, height / 2.0f, width, height, 1.0f, 1.0f, 0.0f);
    }

    @Override
    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        this.sprite.setOrigin(originX, originY);
        this.sprite.setRotation(rotation);
        this.sprite.setScale(scaleX, scaleY);
        this.sprite.setBounds(x, y, width, height);
        Color color = this.sprite.getColor();
        this.sprite.setColor(Color.tmp.set(color).mul(batch.getColor()));
        this.sprite.draw(batch);
        this.sprite.setColor(color);
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.setMinWidth(sprite.getWidth());
        this.setMinHeight(sprite.getHeight());
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public SpriteDrawable tint(Color tint) {
        SpriteDrawable drawable = new SpriteDrawable(this);
        Sprite sprite = drawable.getSprite();
        sprite = sprite instanceof TextureAtlas.AtlasSprite ? new TextureAtlas.AtlasSprite((TextureAtlas.AtlasSprite)sprite) : new Sprite(sprite);
        sprite.setColor(tint);
        drawable.setSprite(sprite);
        return drawable;
    }
}

