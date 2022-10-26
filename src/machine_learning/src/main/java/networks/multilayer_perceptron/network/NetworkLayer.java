package networks.multilayer_perceptron.network;

import core.layer.Imitable;
import maths.Function;

/**
 * @author main
 */
public class NetworkLayer implements Imitable<NetworkLayer> {

    protected final int totalWeightsCount;
    protected final int inputWeightsCount;
    protected final Function function;
    protected final Neuron[] neurons;
    int maxStartValue = 1;

    public NetworkLayer(int numberOfNeurons, int numberOfWeights) {
        this(numberOfNeurons, numberOfWeights, null);
    }

    public NetworkLayer(int numberOfNeurons, int numberOfWeights, Function function) {
        if (numberOfNeurons <= 0 || numberOfWeights <= 0) {
            throw new IllegalArgumentException("Number of nodes and number of weights must be greater than zero.");
        }

        inputWeightsCount = numberOfWeights;

        this.totalWeightsCount = numberOfWeights + Neuron.EXTRA_WEIGHTS;// the weights for one neuron
        neurons = new Neuron[numberOfNeurons];

        this.function = function;
    }

    public int getNeuronsCount() {
        return neurons.length;
    }

    public int getLayerInputCount() {
        return inputWeightsCount;
    }

    public Function getFunction() {
        return function;
    }

    public Neuron getNeuronAt(int position) {
        return neurons[position];
    }

    @Override
    public NetworkLayer copy() {
        return new NetworkLayer(neurons.length, inputWeightsCount, function);
    }

    protected void setNeuronAt(int position, Neuron neuron) {
        neurons[position] = neuron;
    }

    protected void buildNeurons(double[] weights, boolean[] weightStatuses, int startPoint) {
        int endPoint = startPoint + totalWeightsCount;
        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = new Neuron(weights, weightStatuses, startPoint, endPoint, maxStartValue, function);

            startPoint += totalWeightsCount;
            endPoint += totalWeightsCount;
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
