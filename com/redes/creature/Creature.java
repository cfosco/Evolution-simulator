/*
 * Decompiled with CFR 0_122.
 */
package com.redes.creature;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.redes.creature.BasicMLPBrain;
import com.redes.creature.Brain;
import com.redes.helpers.AssetLoader;
import com.redes.helpers.TPGlobals;
import com.redes.world.TPWorld;
import com.redes.worldobjects.WorldObject;
import java.util.Random;

public class Creature {
    private static final float MAX_ABS_SPEED = 50.0f;
    private static final double FOOD_DEP_RATE = 1.0;
    private static final float REP_FOOD_LVL = 15.0f;
    private static final float MAX_FOOD_LVL = 30.0f;
    private static final float FOOD_SPEED_DEP_RATE = 0.05f;
    private static final float REP_TIME = 15.0f;
    private static final float BASE_FOOD_LVL = 7.0f;
    private Brain brain;
    private Vector2 pos;
    private Vector2 dir;
    private float speed;
    private Sprite s;
    private Random r;
    private Color color;
    private int gen = 0;
    private int ID = 0;
    private int childQ = 0;
    private float foodLvl;
    private float totalFood;
    private float width;
    private float height;
    private float repTime;
    private float runTime;
    private float lifeTime;
    private boolean alive;
    private boolean readyToRep;
    private boolean foodCollision;
    private boolean fireCollision;
    private boolean maxLife;
    private boolean mostFood;

    public Creature() {
        this.init();
        this.brain = new BasicMLPBrain(this);
        this.pos = new Vector2(this.r.nextFloat() * TPGlobals.gameWidth, this.r.nextFloat() * TPGlobals.gameHeight);
        this.s.setBounds(0.0f, 0.0f, this.width, this.height);
        this.s.setCenter(this.pos.x, this.pos.y);
        this.color = new Color(this.r.nextFloat(), this.r.nextFloat(), this.r.nextFloat(), 1.0f);
    }

    public Creature(Creature c) {
        this.init();
        this.brain = c.getBrain().copyAndMutateFor(this);
        this.color = this.brain.isMutated() ? c.mutateColor() : c.getColor();
        this.pos = c.getPos().cpy().sub(this.width * 2.0f, this.height * 2.0f);
        this.gen = c.getGen() + 1;
        this.s.setBounds(0.0f, 0.0f, this.width, this.height);
        this.s.setCenter(this.pos.x, this.pos.y);
    }

    public Creature(Brain brain) {
        this.init();
        this.brain = brain;
        this.brain.setCreature(this);
        this.pos = new Vector2(this.r.nextFloat() * TPGlobals.gameWidth, this.r.nextFloat() * TPGlobals.gameHeight);
        this.s.setBounds(0.0f, 0.0f, this.width, this.height);
        this.s.setCenter(this.pos.x, this.pos.y);
        this.color = new Color(this.r.nextFloat(), this.r.nextFloat(), this.r.nextFloat(), 1.0f);
    }

    public Color getColor() {
        return this.color;
    }

    public void init() {
        this.r = new Random();
        this.alive = true;
        this.foodCollision = false;
        this.readyToRep = false;
        this.maxLife = false;
        this.foodLvl = this.r.nextFloat() * 3.0f + 7.0f;
        this.totalFood = 0.0f;
        this.repTime = 15.0f;
        this.runTime = 0.0f;
        this.lifeTime = 0.0f;
        this.height = this.width = 7.0f;
        this.s = new Sprite(AssetLoader.creatureSprite1);
        this.dir = new Vector2(this.r.nextFloat() * 2.0f - 1.0f, this.r.nextFloat() * 2.0f - 1.0f);
        this.speed = 1.0f;
    }

    public void update(float delta) {
        this.brain.process();
        this.updateStatus(delta);
        this.updatePosition(this.dir, delta);
    }

    public void observeWorld(TPWorld world) {
        this.brain.observeWorld(world);
    }

    public void resetBooleans() {
        this.maxLife = false;
        this.mostFood = false;
    }

    public void updateStatus(float delta) {
        this.resetBooleans();
        this.foodLvl = (float)((double)this.foodLvl - (1.0 + (double)(this.speed * 0.05f)) * (double)delta);
        this.runTime += delta;
        this.lifeTime += delta;
        if (this.foodLvl <= 0.0f) {
            this.alive = false;
        }
        if (this.foodLvl > 30.0f) {
            this.alive = false;
        }
        if (this.runTime > this.repTime) {
            this.runTime = 0.0f;
            this.readyToRep = true;
        }
    }

    public void updatePosition(Vector2 v, float delta) {
        this.pos.add(v.cpy().scl(delta * this.speed));
        if (this.pos.x > TPGlobals.gameWidth) {
            this.pos.x = 0.0f;
        }
        if (this.pos.y > TPGlobals.gameHeight) {
            this.pos.y = 0.0f;
        }
        if (this.pos.x < 0.0f) {
            this.pos.x = TPGlobals.gameWidth;
        }
        if (this.pos.y < 0.0f) {
            this.pos.y = TPGlobals.gameHeight;
        }
        this.s.setCenter(this.pos.x, this.pos.y);
    }

    public void draw(Batch batcher) {
        if (this.ID == TPGlobals.currentCreature) {
            this.drawStats(batcher);
            this.drawSelectHighlighter(batcher, 1.8f);
        }
        if (this.maxLife) {
            this.drawMaxLifeHighlighter(batcher, 1.8f);
        }
        this.s.setColor(this.color);
        this.s.draw(batcher);
        if (this.brain.isMutated()) {
            batcher.draw(AssetLoader.mutatedSprite, this.pos.x - this.width / 2.0f, this.pos.y - this.height / 2.0f, this.pos.x, this.pos.y, this.width, this.height, 1.0f, 1.0f, 0.0f);
        }
        if (this.brain.isCrossover()) {
            batcher.draw(AssetLoader.crossoverSprite, this.pos.x - this.width / 2.0f, this.pos.y - this.height / 2.0f, this.pos.x, this.pos.y, this.width, this.height, 1.0f, 1.0f, 0.0f);
        }
        if (this.maxLife) {
            batcher.draw(AssetLoader.maxLifeSprite, this.pos.x - this.width / 2.0f, this.pos.y - this.height / 2.0f, this.pos.x, this.pos.y, this.width, this.height, 1.0f, 1.0f, 0.0f);
        }
        if (this.mostFood) {
            batcher.draw(AssetLoader.maxFoodSprite, this.pos.x - this.width / 2.0f, this.pos.y - this.height / 2.0f, this.pos.x, this.pos.y, this.width, this.height, 1.0f, 1.0f, 0.0f);
        }
        this.drawMvmtVector(batcher);
    }

    public void drawStats(Batch batcher) {
        BitmapFont font = AssetLoader.courierNewBlackFont;
        float scale = 0.18f;
        float x = TPGlobals.gameWidth * 0.55f;
        float y = TPGlobals.totalHeight - TPGlobals.gameWidth * 0.01f;
        float width = TPGlobals.gameWidth * 0.25f;
        String creatStatStr = String.valueOf(String.format("CRIATURA ACTUAL: %d\n", this.ID)) + String.format("T de vida: %.2f\n", Float.valueOf(this.lifeTime)) + String.format("Niv de comida: %.1f\n", Float.valueOf(this.foodLvl)) + String.format("Comida total: %.1f\n", Float.valueOf(this.totalFood)) + String.format("Velocidad: %.1f\n", Float.valueOf(this.speed)) + String.format("Hijos: %d\n", this.childQ) + String.format("Generacion: %d\n", this.gen);
        float prevScaleX = font.getScaleX();
        float prevScaleY = font.getScaleY();
        font.setScale(prevScaleX * scale, prevScaleY * scale);
        font.drawWrapped(batcher, creatStatStr, x, y, width);
        font.setScale(prevScaleX, prevScaleY);
        this.drawBrain(batcher);
    }

    public void drawMaxLifeHighlighter(Batch batcher, float k) {
        batcher.draw(AssetLoader.glowHl, this.pos.x - this.width * k / 2.0f, this.pos.y - this.height * k / 2.0f, this.pos.x, this.pos.y, this.width * k, this.height * k, 1.0f, 1.0f, 0.0f);
    }

    public void drawSelectHighlighter(Batch batcher, float k) {
        batcher.draw(AssetLoader.circleHl, this.pos.x - this.width * k / 2.0f, this.pos.y - this.height * k / 2.0f, this.pos.x, this.pos.y, this.width * k, this.height * k, 1.0f, 1.0f, 0.0f);
    }

    public void drawBrain(Batch batcher) {
        this.brain.draw(batcher, TPGlobals.gameWidth * 0.8f, TPGlobals.gameHeight, TPGlobals.gameWidth * 0.2f, TPGlobals.statHeight);
    }

    public void drawStatsOnCreature(Batch batcher) {
        if (TPGlobals.drawCreatureStats) {
            BitmapFont font = AssetLoader.courierNewBlackFont;
            float scale = 0.15f;
            float prevScaleX = font.getScaleX();
            float prevScaleY = font.getScaleY();
            font.setScale(prevScaleX * scale, prevScaleY * scale);
            font.draw(batcher, "F=" + this.foodLvl, this.pos.x + this.width, this.pos.y + this.height);
            font.draw(batcher, "S=" + this.speed, this.pos.x + this.width, this.pos.y);
            font.draw(batcher, "ID=" + this.ID, this.pos.x + this.width, this.pos.y - this.height);
            font.setScale(prevScaleX, prevScaleY);
        }
    }

    public void drawMvmtVector(Batch batcher) {
    }

    public boolean isAlive() {
        return this.alive;
    }

    public double getdirX() {
        return this.dir.x;
    }

    public double getdirY() {
        return this.dir.y;
    }

    public Vector2 getDir() {
        return this.dir;
    }

    public void setDir(double x, double y) {
        this.dir.set((float)x, (float)y);
        this.dir.nor();
    }

    public Vector2 getPos() {
        return this.pos;
    }

    public void modifyFoodLvl(float f) {
        this.foodLvl += f;
    }

    public void eat(float foodEaten) {
        this.foodLvl += foodEaten;
        this.totalFood += foodEaten;
    }

    public boolean collides(WorldObject o) {
        if (o.getPos().x + o.getWidth() / 2.0f > this.pos.x - this.width / 2.0f && o.getPos().x - o.getWidth() / 2.0f < this.pos.x + this.width / 2.0f) {
            if (o.getPos().y + o.getHeight() / 2.0f > this.pos.y - this.height / 2.0f && o.getPos().y - o.getHeight() / 2.0f < this.pos.y + this.height / 2.0f) {
                return true;
            }
            return false;
        }
        return false;
    }

    public Brain getBrain() {
        return this.brain;
    }

    public boolean isReadyToRep() {
        return this.readyToRep;
    }

    public void setSpeed(double s) {
        this.speed = (float)s * 50.0f;
    }

    public void setColor(double r, double g, double b) {
        this.color.set((float)r, (float)g, (float)b, 1.0f);
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getFoodLvl() {
        return this.foodLvl;
    }

    public float getFoodLvl0to1() {
        return this.foodLvl / 30.0f;
    }

    public boolean isOnFood() {
        return this.foodCollision;
    }

    public float getSpeed0to1() {
        return this.speed / 50.0f;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return this.ID;
    }

    public void setOnFood(boolean b) {
        this.foodCollision = b;
    }

    public boolean collides(float x, float y) {
        if (x > this.pos.x - this.width / 2.0f && x < this.pos.x + this.width / 2.0f) {
            if (y > this.pos.y - this.height / 2.0f && y < this.pos.y + this.height / 2.0f) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void setOnFire(boolean b) {
        this.fireCollision = b;
    }

    public boolean isOnFire() {
        return this.fireCollision;
    }

    public int getGen() {
        return this.gen;
    }

    public int getChildrenCount() {
        return this.childQ;
    }

    public float getLifetime() {
        return this.lifeTime;
    }

    public float getTotalFood() {
        return this.totalFood;
    }

    public Creature reproduce() {
        this.readyToRep = false;
        this.foodLvl -= 2.0f;
        ++this.childQ;
        return new Creature(this);
    }

    public Creature reproduceWith(Creature c) {
        Gdx.app.log("Creature", "Creat " + this.ID + " (LT = " + this.lifeTime + ") reproducing with " + c.getID() + " (LT = " + c.getLifetime() + ")");
        Brain b = this.brain.crossWith(c.brain);
        Creature creat = new Creature(b);
        if (b.isMutated()) {
            creat.setColor(this.mutateColor());
        } else {
            creat.setColor(this.color.cpy());
        }
        creat.setPosition(this.pos.cpy().add(this.width * 2.0f, this.height * 2.0f));
        creat.setGen(this.gen + 1);
        ++this.childQ;
        ++c.childQ;
        return creat;
    }

    private void setGen(int g) {
        this.gen = g;
    }

    private void setPosition(Vector2 v) {
        this.pos = v;
    }

    private void setColor(Color c) {
        this.color = c;
    }

    public void setMaxLifetime() {
        this.maxLife = true;
    }

    public void setMostFood() {
        this.mostFood = true;
    }

    public Color mutateColor() {
        float k = 0.1f;
        return this.color.cpy().add((this.r.nextFloat() * 2.0f - 1.0f) * k, (this.r.nextFloat() * 2.0f - 1.0f) * k, (this.r.nextFloat() * 2.0f - 1.0f) * k, 1.0f);
    }
}

