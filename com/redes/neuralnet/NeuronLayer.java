/*
 * Decompiled with CFR 0_122.
 */
package com.redes.neuralnet;

import com.redes.neuralnet.Neuron;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class NeuronLayer {
    private int numNeurons;
    private int inPerNeu;
    private List<Neuron> neurons;

    public NeuronLayer(int neuronsPerLayer, int inputsPerNeuron) {
        this.numNeurons = neuronsPerLayer;
        this.inPerNeu = inputsPerNeuron;
        this.neurons = new Vector<Neuron>();
        int i = 0;
        while (i < this.numNeurons) {
            this.neurons.add(new Neuron(this.inPerNeu));
            ++i;
        }
    }

    public Vector<Double> computeOutput(Vector<Double> input) {
        Vector<Double> out = new Vector<Double>();
        for (Neuron n : this.neurons) {
            out.add(n.computeOutput(input));
        }
        return out;
    }

    public Vector<Double> getWeights() {
        Vector<Double> w = new Vector<Double>();
        for (Neuron n : this.neurons) {
            w.addAll(n.getWeights());
        }
        return w;
    }

    public void setWeights(Vector<Double> w) {
        int i = 0;
        while (i < this.neurons.size()) {
            Vector<Double> auxW = new Vector<Double>(this.inPerNeu);
            int j = 0;
            while (j < this.inPerNeu + 1) {
                auxW.add(w.get(0));
                w.remove(0);
                ++j;
            }
            this.neurons.get(i).setWeights(auxW);
            ++i;
        }
    }
}

