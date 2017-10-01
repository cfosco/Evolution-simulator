/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TiledDrawable
extends TextureRegionDrawable {
    public TiledDrawable() {
    }

    public TiledDrawable(TextureRegion region) {
        super(region);
    }

    public TiledDrawable(TextureRegionDrawable drawable) {
        super(drawable);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        float v;
        float u2;
        TextureRegion region = this.getRegion();
        float regionWidth = region.getRegionWidth();
        float regionHeight = region.getRegionHeight();
        float remainingX = width % regionWidth;
        float remainingY = height % regionHeight;
        float startX = x;
        float startY = y;
        float endX = x + width - remainingX;
        float endY = y + height - remainingY;
        while (x < endX) {
            for (y = startY; y < endY; y += regionHeight) {
                batch.draw(region, x, y, regionWidth, regionHeight);
            }
            x += regionWidth;
        }
        Texture texture = region.getTexture();
        float u = region.getU();
        float v2 = region.getV2();
        if (remainingX > 0.0f) {
            u2 = u + remainingX / (float)texture.getWidth();
            v = region.getV();
            for (y = startY; y < endY; y += regionHeight) {
                batch.draw(texture, x, y, remainingX, regionHeight, u, v2, u2, v);
            }
            if (remainingY > 0.0f) {
                v = v2 - remainingY / (float)texture.getHeight();
                batch.draw(texture, x, y, remainingX, remainingY, u, v2, u2, v);
            }
        }
        if (remainingY > 0.0f) {
            u2 = region.getU2();
            v = v2 - remainingY / (float)texture.getHeight();
            for (x = startX; x < endX; x += regionWidth) {
                batch.draw(texture, x, y, regionWidth, remainingY, u, v2, u2, v);
            }
        }
    }
}

