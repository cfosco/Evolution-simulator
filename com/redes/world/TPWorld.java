/*
 * Decompiled with CFR 0_122.
 */
package com.redes.world;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.redes.creature.Brain;
import com.redes.creature.Creature;
import com.redes.helpers.InputHandler;
import com.redes.helpers.TPGlobals;
import com.redes.neuralnet.NeuralNet;
import com.redes.world.TPRenderer;
import com.redes.world.TPStats;
import com.redes.worldobjects.Fire;
import com.redes.worldobjects.Food;
import com.redes.worldobjects.WorldObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class TPWorld {
    private static final int INITIAL_CREATURES = 40;
    private static final int INITIAL_FOOD = 300;
    private static final int INITIAL_FIRE = 10;
    private static final float CREATURE_FREQ = 0.5f;
    private static final float FOOD_FREQ = 0.02f;
    private static final int MAX_FOOD = 2000;
    private float crossoverProb = 0.5f;
    private float lastCreatureTime = 0.0f;
    private float lastFoodTime = 0.0f;
    private int cCount = 0;
    private int fCount = 0;
    private int deadCount = 0;
    private TPStats stats;
    List<Creature> creatures;
    List<Food> food;
    List<Fire> fires;
    TPRenderer renderer;
    InputHandler ih;
    GameState currentState;
    Random r;
    NeuralNet testNet;
    Creature testCreature;

    public TPWorld() {
        this.initCreatures();
        this.initFood();
        this.initFires();
        this.currentState = GameState.RUNNING;
        TPGlobals.currentCreature = this.creatures.get(0).getID();
        this.stats = new TPStats(this);
        this.testCreature = new Creature();
        Gdx.app.log("TPWorld", "Brain1 is: " + this.testCreature.getBrain().getWeights());
        Creature c = this.testCreature.reproduce();
        Gdx.app.log("TPWorld", "Brain2 is: " + c.getBrain().getWeights());
        this.r = new Random();
    }

    public void initFood() {
        this.food = new ArrayList<Food>();
        int i = 0;
        while (i < 300) {
            this.addFood();
            ++i;
        }
    }

    public void initFires() {
        this.fires = new ArrayList<Fire>();
        int i = 0;
        while (i < 10) {
            this.addFire();
            ++i;
        }
    }

    public void initCreatures() {
        this.creatures = new ArrayList<Creature>();
        int i = 0;
        while (i < 40) {
            this.addCreature(new Creature());
            ++i;
        }
    }

    public void addRandomCreature() {
        this.addCreature(new Creature());
    }

    public void addCrossCreature() {
        List<Creature> couple = this.getFitCoupleRoulette();
        this.addCreature(this.getOffspring(couple));
    }

    public List<Creature> getFitCoupleRoulette() {
        ArrayList<Creature> couple = new ArrayList<Creature>();
        ArrayList<Integer> idxs = new ArrayList<Integer>();
        float lifeSum = 0.0f;
        for (Creature c : this.creatures) {
            lifeSum += c.getLifetime();
        }
        int j = 0;
        while (j < 2) {
            float tempSum = 0.0f;
            float N = this.r.nextFloat() * lifeSum;
            idxs.removeAll(idxs);
            int i = 0;
            while (i < this.creatures.size()) {
                idxs.add(i);
                ++i;
            }
            i = 0;
            while (i < this.creatures.size()) {
                Integer idx = (Integer)idxs.get(this.r.nextInt(idxs.size()));
                if (this.creatures.get(idx).getLifetime() + tempSum >= N) {
                    couple.add(this.creatures.get(idx));
                    break;
                }
                tempSum += this.creatures.get(idx).getLifetime();
                idxs.remove(idx);
                ++i;
            }
            ++j;
        }
        return couple;
    }

    public List<Creature> getFitCoupleGaussian() {
        ArrayList<Creature> couple = new ArrayList<Creature>();
        ArrayList<Integer> idxs = new ArrayList<Integer>();
        int i = 0;
        while (i < this.creatures.size()) {
            idxs.add(i);
            ++i;
        }
        i = 0;
        while (i < this.creatures.size()) {
            int k = this.r.nextInt(idxs.size());
            int idx = (Integer)idxs.get(k);
            float roll = (float)this.r.nextGaussian() / 4.0f + 1.0f;
            if (roll > 1.0f) {
                roll = 1.0f;
            }
            if (roll <= this.creatures.get(idx).getLifetime() / this.stats.currentMaxLifetime) {
                couple.add(this.creatures.get(idx));
            }
            idxs.remove(k);
            if (couple.size() >= 2) break;
            ++i;
        }
        if (couple.size() < 2) {
            couple.add(this.creatures.get(this.r.nextInt(this.creatures.size())));
        }
        return couple;
    }

    public Creature getCreature(int ID) {
        for (Creature c : this.creatures) {
            if (c.getID() != ID) continue;
            return c;
        }
        return null;
    }

    public Creature getOffspring(List<Creature> couple) {
        return couple.get(0).reproduceWith(couple.get(1));
    }

    public void addCreature(Creature c) {
        c.setID(this.cCount);
        ++this.cCount;
        this.creatures.add(c);
    }

    public void addFood() {
        this.food.add(new Food());
        ++this.fCount;
    }

    public void addFire() {
        this.fires.add(new Fire());
    }

    public void setRenderer(TPRenderer renderer) {
        this.renderer = renderer;
    }

    public void setInputHandler(InputHandler ih) {
        this.ih = ih;
    }

    public void update(float delta) {
        this.updateFood(delta);
        this.updateFire(delta);
        this.updateCreatures(delta);
        this.stats.update(delta);
    }

    public void updateFire(float delta) {
        for (Fire f : this.fires) {
            f.update(delta);
        }
    }

    public void updateFood(float delta) {
        this.lastFoodTime += delta;
        if (this.lastFoodTime >= 0.02f && this.food.size() < 2000) {
            this.lastFoodTime = 0.0f;
            this.addFood();
        }
        int k = 0;
        while (k < this.food.size()) {
            this.food.get(k).update(delta);
            if (this.food.get(k).isDepleted()) {
                this.food.remove(k);
                --this.fCount;
            }
            ++k;
        }
    }

    public void updateCreatures(float delta) {
        this.lastCreatureTime += delta;
        if (this.lastCreatureTime >= 0.5f) {
            this.lastCreatureTime = 0.0f;
            if (this.r.nextFloat() < this.crossoverProb) {
                this.addCrossCreature();
            } else {
                this.addRandomCreature();
            }
        }
        int i = 0;
        while (i < this.creatures.size()) {
            Creature c = this.creatures.get(i);
            c.observeWorld(this);
            c.update(delta);
            c.setOnFood(false);
            c.setOnFire(false);
            int j = 0;
            while (j < this.food.size()) {
                if (c.collides(this.food.get(j))) {
                    c.setOnFood(true);
                    c.eat(this.food.get(j).consume(delta));
                }
                ++j;
            }
            j = 0;
            while (j < this.fires.size()) {
                if (c.collides(this.fires.get(j))) {
                    c.setOnFire(true);
                    c.modifyFoodLvl((- this.fires.get(j).getDPS()) * delta);
                }
                ++j;
            }
            if (c.isReadyToRep()) {
                this.addCreature(c.reproduce());
            }
            if (!this.creatures.get(i).isAlive()) {
                if (this.creatures.get(i).getGen() >= 1) {
                    this.stats.addLifeTimeFromDeadCreat(this.creatures.get(i).getLifetime());
                    this.stats.addFoodFromDeadCreat(this.creatures.get(i).getTotalFood());
                }
                this.creatures.remove(i);
                ++this.deadCount;
            }
            ++i;
        }
    }

    public boolean isRunning() {
        if (this.currentState == GameState.RUNNING) {
            return true;
        }
        return false;
    }

    public boolean isMenu() {
        if (this.currentState == GameState.MENU) {
            return true;
        }
        return false;
    }

    public List<Creature> getCreatures() {
        return this.creatures;
    }

    public List<Food> getFood() {
        return this.food;
    }

    public List<Fire> getFires() {
        return this.fires;
    }

    public Creature getTouchedCreature(float x, float y) {
        int i = this.creatures.size() - 1;
        while (i >= 0) {
            if (this.creatures.get(i).collides(x, y)) {
                return this.creatures.get(i);
            }
            --i;
        }
        return null;
    }

    public List<Vector2> getFoodPositions() {
        ArrayList<Vector2> list = new ArrayList<Vector2>();
        for (Food f : this.food) {
            list.add(f.getPos());
        }
        return list;
    }

    public List<Vector2> getFirePositions() {
        ArrayList<Vector2> list = new ArrayList<Vector2>();
        for (Fire f : this.fires) {
            list.add(f.getPos());
        }
        return list;
    }

    public TPStats getStats() {
        return this.stats;
    }

    public int getCCount() {
        return this.cCount;
    }

    public int getFCount() {
        return this.fCount;
    }

    public Creature getCreatureByID(int ID) {
        for (Creature c : this.creatures) {
            if (c.getID() != ID) continue;
            return c;
        }
        return null;
    }

    public void selectCreature(String s) {
        if (s == "first") {
            TPGlobals.currentCreature = this.creatures.get(0).getID();
            Gdx.app.log("WORLD", "currentCreature = " + TPGlobals.currentCreature);
        }
        if (s == "last") {
            TPGlobals.currentCreature = this.creatures.get(this.creatures.size() - 1).getID();
            Gdx.app.log("WORLD", "currentCreature = " + TPGlobals.currentCreature);
        }
        if (s == "next") {
            Gdx.app.log("WORLD", "currentCreature = " + ++TPGlobals.currentCreature);
        }
        if (s == "prev") {
            Gdx.app.log("WORLD", "currentCreature = " + --TPGlobals.currentCreature);
        }
        if (s == "best") {
            TPGlobals.currentCreature = this.stats.currentMaxLifetimeID;
            Gdx.app.log("WORLD", "currentCreature = " + TPGlobals.currentCreature);
        }
    }

    public static enum GameState {
        MENU,
        RUNNING;
        

        private GameState(String string2, int n2) {
        }
    }

}

