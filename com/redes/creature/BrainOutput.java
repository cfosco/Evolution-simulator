/*
 * Decompiled with CFR 0_122.
 */
package com.redes.creature;

import com.badlogic.gdx.math.Vector2;
import java.util.Random;

public class BrainOutput {
    Vector2 mvmt;
    Random r = new Random();

    public BrainOutput() {
        this.mvmt = new Vector2(this.r.nextFloat() * 4.0f - 2.0f, this.r.nextFloat() * 4.0f - 2.0f);
    }

    public Vector2 getMvmtVector() {
        return this.mvmt;
    }
}

