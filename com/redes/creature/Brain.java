/*
 * Decompiled with CFR 0_122.
 */
package com.redes.creature;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.redes.creature.Creature;
import com.redes.world.TPWorld;
import java.util.Vector;

public abstract class Brain {
    public abstract void process();

    public abstract void observeWorld(TPWorld var1);

    public abstract Brain copyAndMutateFor(Creature var1);

    public abstract Vector<Double> getWeights();

    public abstract void draw(Batch var1, float var2, float var3, float var4, float var5);

    public abstract boolean isMutated();

    public abstract Brain crossWith(Brain var1);

    public abstract void setCreature(Creature var1);

    public abstract boolean isCrossover();
}

