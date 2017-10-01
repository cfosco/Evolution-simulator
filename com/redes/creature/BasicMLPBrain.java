/*
 * Decompiled with CFR 0_122.
 */
package com.redes.creature;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.redes.creature.Brain;
import com.redes.creature.Creature;
import com.redes.helpers.AssetLoader;
import com.redes.helpers.TPGlobals;
import com.redes.neuralnet.NeuralNet;
import com.redes.world.TPWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class BasicMLPBrain
extends Brain {
    private static final double MAX_ABS_INPUT_VALUE = 255.0;
    private static final double MAX_VIEW_DST = TPGlobals.gameWidth * 0.2f;
    private static final float MUTATION_PROB = 0.1f;
    private boolean mutated;
    private boolean crossed;
    NeuralNet net;
    Creature creature;
    Vector<Double> in;
    Vector<Double> out;
    List<String> inIDs;
    List<String> outIDs;
    Random r;

    public BasicMLPBrain(Creature c) {
        this.r = new Random();
        this.creature = c;
        this.initNet();
    }

    private BasicMLPBrain(Creature c, Vector<Double> w, boolean m, boolean b) {
        this.mutated = m;
        this.crossed = b;
        this.r = new Random();
        this.creature = c;
        this.initNet();
        this.net.setWeights(w);
    }

    private void initNet() {
        int numIn = 11;
        int numOut = 3;
        int numHLayer = 2;
        int neuPerHLayer = 15;
        this.net = new NeuralNet(numIn, numOut, numHLayer, neuPerHLayer);
        this.in = new Vector(numIn);
        this.out = new Vector(numOut);
        this.inIDs = new ArrayList<String>();
        this.outIDs = new ArrayList<String>();
        this.inIDs.add("Fx");
        this.inIDs.add("Fy");
        this.inIDs.add("Fix");
        this.inIDs.add("Fiy");
        this.inIDs.add("Dx");
        this.inIDs.add("Dy");
        this.inIDs.add("Fd");
        this.inIDs.add("Fid");
        this.inIDs.add("S");
        this.inIDs.add("F?");
        this.inIDs.add("Fi?");
        this.outIDs.add("Dx");
        this.outIDs.add("Dy");
        this.outIDs.add("S");
    }

    @Override
    public void process() {
        this.out = this.net.computeOutput(this.in);
        this.creature.setDir(this.out.get(0) * 2.0 - 1.0, this.out.get(1) * 2.0 - 1.0);
        this.creature.setSpeed(this.out.get(2));
    }

    @Override
    public void observeWorld(TPWorld world) {
        Vector2 foodVec = this.getVectorToClosest(world.getFoodPositions(), this.creature.getPos());
        Vector2 fireVec = this.getVectorToClosest(world.getFirePositions(), this.creature.getPos());
        double foodDst = foodVec.len();
        double fireDst = fireVec.len();
        if (foodDst > MAX_VIEW_DST) {
            foodDst = MAX_VIEW_DST;
        }
        foodDst /= MAX_VIEW_DST;
        if (fireDst > MAX_VIEW_DST) {
            fireDst = MAX_VIEW_DST;
        }
        foodVec.nor();
        fireVec.nor();
        this.in.removeAllElements();
        this.in.add(Double.valueOf(foodVec.x));
        this.in.add(Double.valueOf(foodVec.y));
        this.in.add(Double.valueOf(fireVec.x));
        this.in.add(Double.valueOf(fireVec.y));
        this.in.add(Double.valueOf(this.creature.getDir().x));
        this.in.add(Double.valueOf(this.creature.getDir().y));
        this.in.add(foodDst);
        this.in.add(1.0 - (fireDst /= MAX_VIEW_DST));
        this.in.add(Double.valueOf(this.creature.getSpeed0to1()));
        if (this.creature.isOnFood()) {
            this.in.add(1.0);
        } else {
            this.in.add(0.0);
        }
        if (this.creature.isOnFire()) {
            this.in.add(1.0);
        } else {
            this.in.add(0.0);
        }
    }

    public Vector2 getVectorToClosest(List<Vector2> list, Vector2 pos) {
        if (list.isEmpty()) {
            return new Vector2(0.0f, 0.0f);
        }
        float minDst = 10000.0f;
        float dst2 = 0.0f;
        int idx = 0;
        int i = 0;
        while (i < list.size()) {
            dst2 = pos.dst2(list.get(i));
            if (dst2 < minDst) {
                minDst = dst2;
                idx = i;
            }
            ++i;
        }
        return list.get(idx).cpy().sub(pos);
    }

    public Brain copy(Creature creature) {
        return new BasicMLPBrain(creature, this.net.getWeights(), false, false);
    }

    @Override
    public Brain copyAndMutateFor(Creature creature) {
        if (this.r.nextFloat() < 0.1f) {
            return new BasicMLPBrain(creature, this.net.mutateWeights(), true, false);
        }
        return this.copy(creature);
    }

    @Override
    public Vector<Double> getWeights() {
        return this.net.getWeights();
    }

    @Override
    public void draw(Batch batcher, float x, float y, float width, float height) {
        float inWscl = 0.4f;
        float outWscl = 0.4f;
        float hlWscl = 0.1f;
        float hlHscl = 0.7f;
        batcher.draw(AssetLoader.node, x, y, x, y, width, height, 1.0f, 1.0f, 0.0f);
        this.drawNodes(batcher, x, y, width * inWscl, height, this.in, this.inIDs, true);
        this.drawNodes(batcher, x + width * 0.6f, y, width * outWscl, height, this.out, this.outIDs, false);
        batcher.draw(AssetLoader.node, x + width / 2.0f - width * hlWscl / 2.0f, y + height * (1.0f - hlHscl) / 2.0f, x, y, width * hlWscl, height * hlHscl, 1.0f, 1.0f, 0.0f);
        AssetLoader.whiteFont.draw(batcher, "" + this.creature.getID(), x, y);
    }

    public void drawNodes(Batch batcher, float x, float y, float w, float h, Vector<Double> nodes, List<String> IDs, boolean leftAlign) {
        float nodeWidth = w / 2.0f;
        float nodeHeight = h * 0.1f;
        float margin = w * 0.07f;
        Color c = batcher.getColor();
        BitmapFont font = AssetLoader.courierNewBlackFont;
        float scale = 0.18f;
        float prevScaleX = font.getScaleX();
        float prevScaleY = font.getScaleY();
        font.setScale(prevScaleX * scale, prevScaleY * scale);
        int k = nodes.size();
        if ((float)k * nodeHeight > h) {
            nodeHeight = h * 0.9f / (float)k;
        }
        int i = 0;
        while (i < nodes.size()) {
            if (nodes.get(i) < 0.0) {
                batcher.setColor(0.0f, 0.0f, -1.0f * new Float(nodes.get(i)).floatValue(), 1.0f);
            } else {
                batcher.setColor(new Float(nodes.get(i)).floatValue(), 0.0f, 0.0f, 1.0f);
            }
            if (leftAlign) {
                font.draw(batcher, IDs.get(i), x + margin, y + (float)(i + 1) * h / (float)(k + 1) + nodeHeight / 2.0f);
                batcher.draw(AssetLoader.node, x + w / 2.0f, y + (float)(i + 1) * h / (float)(k + 1) - nodeHeight / 2.0f, nodeWidth, nodeHeight);
            } else {
                font.draw(batcher, IDs.get(i), x + w / 2.0f + margin, y + (float)(i + 1) * h / (float)(k + 1) + nodeHeight / 2.0f);
                batcher.draw(AssetLoader.node, x, y + (float)(i + 1) * h / (float)(k + 1) - nodeHeight / 2.0f, nodeWidth, nodeHeight);
            }
            ++i;
        }
        font.setScale(prevScaleX, prevScaleY);
        batcher.setColor(c);
    }

    @Override
    public boolean isMutated() {
        return this.mutated;
    }

    @Override
    public boolean isCrossover() {
        return this.crossed;
    }

    @Override
    public Brain crossWith(Brain brain) {
        Vector<Double> w1 = brain.getWeights();
        Vector<Double> w2 = this.getWeights();
        float parent1Prob = 0.5f;
        Vector<Double> w = new Vector<Double>();
        int i = 0;
        while (i < w1.size()) {
            float roll = this.r.nextFloat();
            if (roll <= parent1Prob) {
                w.add(w1.get(i));
            } else {
                w.add(w2.get(i));
            }
            ++i;
        }
        BasicMLPBrain b = new BasicMLPBrain(null, w, false, true);
        if (this.r.nextFloat() < 0.1f) {
            b.mutate();
            Gdx.app.log("Brain", "MUTATING CROSSOVER BRAIN");
        }
        return b;
    }

    private void mutate() {
        this.net.setWeights(this.net.mutateWeights());
        this.mutated = true;
    }

    @Override
    public void setCreature(Creature creature) {
        this.creature = creature;
    }
}

