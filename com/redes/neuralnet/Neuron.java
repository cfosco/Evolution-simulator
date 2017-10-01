/*
 * Decompiled with CFR 0_122.
 */
package com.redes.neuralnet;

import java.util.Random;
import java.util.Vector;

public class Neuron {
    private int numInputs;
    private int maxInitWeightValue = 10;
    private Vector<Double> weights;
    private Random r;

    public Neuron(int nI) {
        this.numInputs = nI;
        this.weights = new Vector(this.numInputs);
        this.r = new Random();
        int i = 0;
        while (i < this.numInputs + 1) {
            this.weights.add((this.r.nextDouble() * 2.0 - 1.0) * (double)this.maxInitWeightValue);
            ++i;
        }
    }

    public Neuron(int nI, Vector<Double> w) {
        this.numInputs = nI;
        this.weights = w;
    }

    public double computeOutput(Vector<Double> inputs) {
        double sum = 0.0;
        int i = 0;
        while (i < this.numInputs) {
            sum += inputs.get(i) * this.weights.get(i);
            ++i;
        }
        return this.sigmoid(sum += -1.0 * this.weights.get(i));
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(- x));
    }

    public Vector<Double> getWeights() {
        return this.weights;
    }

    public void setWeights(Vector<Double> w) {
        this.weights = w;
    }
}

