/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron;

import maths.Function;

/**
 * @author main
 */
public class NetworkLayer {
    int maxStartValue = 1;

    protected final int NUMBER_OR_WEIGHTS;
    protected final Function function;
    protected final Neuron[] neurons;

    public NetworkLayer(int numberOfNeurons, int numberOfWeights) {
        this(numberOfNeurons, numberOfWeights, null);
    }

    public NetworkLayer(int numberOfNeurons, int numberOfWeights, Function function) {
        if (numberOfNeurons <= 0 || numberOfWeights <= 0) {
            throw new IllegalArgumentException("Number of nodes and number of weights must be greater than zero.");
        }

        NUMBER_OR_WEIGHTS = numberOfWeights + Neuron.EXTRA_WEIGHTS;// the weights for one neuron
        neurons = new Neuron[numberOfNeurons];

        this.function = function;
    }

    public Neuron getNeuronAt(int position) {
        return neurons[position];
    }

    public int getNeuronsCount() {
        return neurons.length;
    }

    public int getLayerInputCount() {
        return NUMBER_OR_WEIGHTS;
    }

    public Function getFunction() {
        return function;
    }

    protected void setNeuronAt(int position, Neuron neuron) {
        neurons[position] = neuron;
    }

    protected void buildNeurons(double[] weights, int startPoint) {
        int endPoint = startPoint + NUMBER_OR_WEIGHTS;
        for (int i = 0; i < neurons.length; i++) {
            if (function == null) {
                neurons[i] = new Neuron(weights, startPoint, endPoint, maxStartValue);
            } else {
                neurons[i] = new Neuron(weights, startPoint, endPoint, maxStartValue, function);
            }

            startPoint += NUMBER_OR_WEIGHTS;
            endPoint += NUMBER_OR_WEIGHTS;
        }
    }

    protected double[] computeLayer(double[] layerInputs) {
        double[] results = new double[neurons.length];
        for (int i = 0; i < neurons.length; i++) {
            results[i] = neurons[i].compute(layerInputs);
        }

        return results;
    }
}
