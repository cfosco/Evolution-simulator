/*
 * Decompiled with CFR 0_122.
 */
package com.redes.neuralnet;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.redes.neuralnet.NeuronLayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class NeuralNet {
    private int numInputs;
    private int numOutputs;
    private int numHLayers;
    private int neuPerHLayer;
    private List<NeuronLayer> net;
    private NeuronLayer outLayer;
    private Random r;

    public NeuralNet(int numInputs, int numOutputs, int numHLayers, int neuPerHLayer) {
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
        this.numHLayers = numHLayers;
        this.neuPerHLayer = neuPerHLayer;
        this.r = new Random();
        this.net = new ArrayList<NeuronLayer>();
        this.net.add(new NeuronLayer(neuPerHLayer, numInputs));
        int i = 0;
        while (i < numHLayers - 1) {
            this.net.add(new NeuronLayer(neuPerHLayer, neuPerHLayer));
            ++i;
        }
        this.outLayer = new NeuronLayer(numOutputs, neuPerHLayer);
    }

    public Vector<Double> computeOutput(Vector<Double> input) {
        Vector<Double> auxIn = new Vector<Double>();
        auxIn.addAll(input);
        for (NeuronLayer nl : this.net) {
            auxIn = nl.computeOutput(auxIn);
        }
        return this.outLayer.computeOutput(auxIn);
    }

    public int getNeuPerHLayer() {
        return this.neuPerHLayer;
    }

    public Vector<Double> getWeights() {
        Vector<Double> weights = new Vector<Double>();
        for (NeuronLayer nl : this.net) {
            weights.addAll(nl.getWeights());
        }
        weights.addAll(this.outLayer.getWeights());
        return weights;
    }

    public void setWeights(Vector<Double> w) {
        for (NeuronLayer nl : this.net) {
            nl.setWeights(w);
        }
        this.outLayer.setWeights(w);
    }

    public void draw(Batch batcher, float x, float y) {
    }

    public Vector<Double> mutateWeights() {
        Vector<Double> w = this.getWeights();
        int k = this.r.nextInt(w.size());
        double prev = w.get(k);
        w.set(k, (this.r.nextDouble() * 2.0 - 1.0) * 10.0);
        return w;
    }
}

