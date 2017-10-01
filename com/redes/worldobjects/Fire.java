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

public class Fire
extends WorldObject {
    private static final float DMG_PER_SECOND = 9.0f;
    private Sprite sprite;
    private Random r;
    private Vector2 speed;
    private float a = 4.0f;
    private float b = 10.0f;

    public Fire() {
        this.init();
    }

    public void init() {
        this.r = new Random();
        this.pos = new Vector2(this.r.nextFloat() * TPGlobals.gameWidth, this.r.nextFloat() * TPGlobals.gameHeight);
        this.speed = new Vector2(this.r.nextFloat() * this.a + this.b, 0.0f);
        this.sprite = new Sprite(AssetLoader.node);
        this.width = 10.0f;
        this.height = 10.0f;
        this.sprite.setBounds(0.0f, 0.0f, this.width, this.height);
        this.sprite.setCenter(this.pos.x, this.pos.y);
    }

    public float getDPS() {
        return 9.0f;
    }

    public void draw(Batch batcher) {
        this.sprite.setColor(1.0f, 0.0f, 0.0f, 1.0f);
        this.sprite.draw(batcher);
    }

    public void update(float delta) {
        this.pos.add(this.speed.cpy().scl(delta));
        if (this.pos.x > TPGlobals.gameWidth) {
            this.pos.x = 0.0f;
        }
        this.sprite.setCenter(this.pos.x, this.pos.y);
    }
}

