/*
 * Decompiled with CFR 0_122.
 */
package com.redes.world;

import com.badlogic.gdx.files.FileHandle;
import com.redes.creature.Creature;
import com.redes.helpers.AssetLoader;
import com.redes.world.TPWorld;
import com.redes.worldobjects.Food;
import java.util.ArrayList;
import java.util.List;

public class TPStats {
    private static final float WRITE_TIME = 0.5f;
    public int currentPop;
    public int totalPop;
    public int totalFood;
    public int currentFood;
    public float currentMaxLifetime;
    public float currentMostFood;
    public float executionTime = 0.0f;
    public float lifeTimeAvg = 0.0f;
    public float foodQAvg = 0.0f;
    public float popLifetimeAvg = 0.0f;
    public float popTotalFoodAvg = 0.0f;
    public float maxLifetime = 0.0f;
    public int maxChildren = 0;
    public int maxGen = 0;
    public float mostFood = 0.0f;
    public int maxGenID;
    public int maxChildrenID;
    public int maxLifetimeID;
    public int mostFoodID;
    public int currentMaxLifetimeID;
    public int currentMaxChildrenID;
    public int currentMaxChildren;
    public int currentMostFoodID;
    public int Q = 1500;
    public int q = 0;
    private float writeCount = 0.0f;
    public int count2 = 0;
    private TPWorld world;
    private List<Float> lifeTimes;
    private List<Float> foodQs;

    public TPStats(TPWorld world) {
        this.world = world;
        this.lifeTimes = new ArrayList<Float>();
        this.foodQs = new ArrayList<Float>();
        this.update(0.0f);
    }

    public void update(float delta) {
        this.executionTime += delta;
        List<Creature> creats = this.world.getCreatures();
        this.currentPop = creats.size();
        this.currentFood = this.world.getFood().size();
        this.totalFood = this.world.getFCount();
        this.totalPop = this.world.getCCount();
        this.currentMaxLifetimeID = creats.get(0).getID();
        this.currentMaxLifetime = creats.get(0).getLifetime();
        creats.get(0).setMaxLifetime();
        this.currentMaxChildren = 0;
        this.currentMostFood = 0.0f;
        float popLifetimeSum = 0.0f;
        float popTotalFoodSum = 0.0f;
        for (Creature c : creats) {
            popLifetimeSum += c.getLifetime();
            popTotalFoodSum += c.getTotalFood();
            if (c.getGen() > this.maxGen) {
                this.maxGen = c.getGen();
                this.maxGenID = c.getID();
            }
            if (c.getTotalFood() > this.currentMostFood) {
                this.currentMostFood = c.getTotalFood();
                this.currentMostFoodID = c.getID();
            }
            if (c.getChildrenCount() <= this.currentMaxChildren) continue;
            this.currentMaxChildren = c.getChildrenCount();
            this.currentMaxChildrenID = c.getID();
        }
        this.world.getCreatureByID(this.currentMostFoodID).setMostFood();
        if (this.currentMaxLifetime > this.maxLifetime) {
            this.maxLifetime = this.currentMaxLifetime;
            this.maxLifetimeID = this.currentMaxLifetimeID;
        }
        if (this.currentMaxChildren > this.maxChildren) {
            this.maxChildren = this.currentMaxChildren;
            this.maxChildrenID = this.currentMaxChildrenID;
        }
        if (this.currentMostFood > this.mostFood) {
            this.mostFood = this.currentMostFood;
            this.mostFoodID = this.currentMostFoodID;
        }
        this.lifeTimeAvg = this.getAvg(this.lifeTimes);
        this.foodQAvg = this.getAvg(this.foodQs);
        this.popLifetimeAvg = popLifetimeSum / (float)creats.size();
        this.popTotalFoodAvg = popTotalFoodSum / (float)creats.size();
        this.writeCount += delta;
        if (this.writeCount > 0.5f) {
            AssetLoader.statsFile.writeString(String.valueOf(this.lifeTimeAvg) + " " + this.foodQAvg + "\n", true);
            ++this.count2;
            this.writeCount = 0.0f;
        }
    }

    public float getAvg(List<Float> list) {
        if (!list.isEmpty()) {
            float listSum = 0.0f;
            int i = list.size() - 1;
            this.q = 0;
            while (i >= 0 && i > list.size() - 1 - this.Q) {
                listSum += list.get(i).floatValue();
                --i;
                ++this.q;
            }
            float avg = listSum / (float)this.q;
            return avg;
        }
        return 0.0f;
    }

    public void addLifeTimeFromDeadCreat(float l) {
        this.lifeTimes.add(Float.valueOf(l));
        if (this.lifeTimes.size() > this.Q) {
            this.lifeTimes.remove(0);
        }
    }

    public void addFoodFromDeadCreat(float f) {
        this.foodQs.add(Float.valueOf(f));
        if (this.foodQs.size() > this.Q) {
            this.foodQs.remove(0);
        }
    }
}

