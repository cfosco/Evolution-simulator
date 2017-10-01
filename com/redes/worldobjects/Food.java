/*
 * Decompiled with CFR 0_122.
 */
package com.redes.worldobjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.redes.helpers.AssetLoader;
import com.redes.helpers.TPGlobals;
import com.redes.worldobjects.WorldObject;
import java.util.Random;

public class Food
extends WorldObject {
    private static final float INITIAL_FOOD = 10.0f;
    private static final float DEPLETION_RATE = 0.5f;
    private static final float FOOD_PER_SECOND = 3.0f;
    private Sprite sprite;
    private float a;
    private float foodQuantity;
    private boolean depleted = false;
    Random r = new Random();

    public Food() {
        this.pos = new Vector2(this.r.nextFloat() * TPGlobals.gameWidth, this.r.nextFloat() * TPGlobals.gameHeight);
        this.a = 1.0f;
        this.foodQuantity = 10.0f;
        this.height = this.width = 2.0f;
        this.sprite = new Sprite(AssetLoader.creatureSprite1);
        this.sprite.setBounds(0.0f, 0.0f, this.width, this.height);
        this.sprite.setCenter(this.pos.x, this.pos.y);
    }

    public void draw(Batch batcher) {
        this.sprite.setAlpha(this.a);
        this.sprite.draw(batcher);
    }

    @Override
    public Vector2 getPos() {
        return this.pos;
    }

    public float getFoodPerSecond() {
        return 3.0f;
    }

    public void update(float delta) {
        this.foodQuantity -= 0.5f * delta;
        this.a = this.foodQuantity / 10.0f;
        if (this.foodQuantity <= 0.0f) {
            this.depleted = true;
        }
    }

    public float consume(float delta) {
        float f = delta * 3.0f;
        this.foodQuantity -= f;
        return f;
    }

    public boolean isDepleted() {
        return this.depleted;
    }
}

