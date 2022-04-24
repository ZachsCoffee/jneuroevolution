/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import functions.ActivationFunction;
import maths.Function;
import networks.interfaces.Network;
import maths.MinMax;
import networks.representations.LayerImage;
import networks.representations.NetworkImage;

/**
 * @author main
 */
public class NeuralNetwork implements Network {

    public final int MAX_START_VALUE;

    protected final NetworkLayer[] layers;
    protected final double[] weights;

    public NeuralNetwork(double[] weights, NetworkLayer[] layers) {
        this(layers);

        if (weights.length != this.weights.length) throw new IllegalArgumentException(
                "Given weights length is different from the actual needed to build the network. Given: "+weights.length+" needed: "+this.weights.length
        );

        System.arraycopy(weights, 0, this.weights, 0, this.weights.length);
    }

    public NeuralNetwork(NetworkLayer[] layers) {
        this(layers, 1);
    }

    public NeuralNetwork(NetworkLayer[] layers, int maxStartValue) {
        if (layers == null) {
            throw new IllegalArgumentException("Layers not null.");
        }

        if (layers.length <= 0) {
            throw new IllegalArgumentException("Layers length, must be greater than zero");
        }

        this.layers = layers;

        int sumOfWeights = 0;
        for (int i = 0; i < layers.length; i++) {
            sumOfWeights += layers[i].getNeuronsCount() * layers[i].NUMBER_OR_WEIGHTS;
        }

        weights = new double[sumOfWeights];

        MAX_START_VALUE = maxStartValue;

        int startPoint = 0;
        for (int i = 0; i < layers.length; i++) {
            layers[i].maxStartValue = MAX_START_VALUE;
            layers[i].buildNeurons(weights, startPoint);
            startPoint += layers[i].getNeuronsCount() * layers[i].NUMBER_OR_WEIGHTS;
        }
    }

    @Override
    public int getWeightsCount() {
        return weights.length;
    }

    @Override
    public double getWeightAt(int position) {
        return weights[position];
    }

    @Override
    public void setWeightAt(int position, double value) {
        weights[position] = value;
    }

    public NetworkLayer getLayerAt(int position) {
        return layers[position];
    }

    public int getLayerCount() {
        return layers.length;
    }

    @Override
    public double[] compute(double[] features) {
        double[] results = features;
        for (NetworkLayer layer : layers) {
            results = layer.computeLayer(results);
        }

        return results;
    }

    protected double[] computeLayer(int layerIndex, double[] features) {
        return layers[layerIndex].computeLayer(features);
    }

    public static Network buildRandomSizeNetwork(
            int inputFeatures, int output, MinMax neuronPerLayer, MinMax networkLength, Function hiddenLayerF, Function outputLayerF) {

        if (neuronPerLayer.min < 2) throw new IllegalArgumentException(
                "A network layer must have at least two neurons! (min=" + neuronPerLayer.min + ")"
        );
        if (networkLength.min < 2) throw new IllegalArgumentException(
                "A neural network must have at least two layers! min=(" + networkLength.min + ")"
        );
        if (inputFeatures < 1) throw new IllegalArgumentException(
                "Input features must be at least one inputFeatures=" + inputFeatures
        );
        if (output < 1) throw new IllegalArgumentException(
                "Output must be at least one output=" + output
        );

        int tempNeuronsPerlLayer = neuronPerLayer.randomBetween();
        NetworkLayer[] networkLayers = new NetworkLayer[networkLength.randomBetween()];

        if (hiddenLayerF != null) {
            networkLayers[0] = new NetworkLayer(tempNeuronsPerlLayer, inputFeatures, hiddenLayerF);

            for (int i = 1; i < networkLayers.length - 1; i++) {
                networkLayers[i] = new NetworkLayer(neuronPerLayer.randomBetween(), tempNeuronsPerlLayer, hiddenLayerF);
                tempNeuronsPerlLayer = networkLayers[i].getNeuronsCount();
            }
        }
        else {
            networkLayers[0] = new NetworkLayer(tempNeuronsPerlLayer, inputFeatures);

            for (int i = 1; i < networkLayers.length - 1; i++) {
                networkLayers[i] = new NetworkLayer(neuronPerLayer.randomBetween(), tempNeuronsPerlLayer);
                tempNeuronsPerlLayer = networkLayers[i].getNeuronsCount();
            }
        }

        if (outputLayerF != null) {
            networkLayers[networkLayers.length - 1] = new NetworkLayer(output, tempNeuronsPerlLayer, outputLayerF);
        }
        else {
            networkLayers[networkLayers.length - 1] = new NetworkLayer(output, tempNeuronsPerlLayer);
        }
        NeuralNetwork returnedNetwork = new NeuralNetwork(networkLayers, 1);


        System.out.println(returnedNetwork);
        return returnedNetwork;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();

        int layersCount = layers.length;
        for (int i = 0; i < layersCount; i++) {
            output.append("Layer ").append(i + 1).append(": neurons(").append(layers[i].getNeuronsCount()).append(")\n");
        }

        return output + "\n";
    }
}
