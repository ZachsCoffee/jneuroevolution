/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron.network;

import core.layer.MatrixReader;
import core.layer.MatrixSchema;
import core.layer.TrainableLayer;
import core.schema.LayerSchema;
import maths.Function;
import maths.matrix.Matrix2D;
import networks.interfaces.Network;
import maths.MinMax;
import core.schema.SchemaRow;
import networks.interfaces.PartialNetwork;

import java.util.Arrays;

/**
 * @author main
 */
public class NeuralNetwork implements PartialNetwork {

    public static Network buildRandomSizeNetwork(
        int inputFeatures,
        int output,
        MinMax neuronPerLayer,
        MinMax networkLength,
        Function hiddenLayerF,
        Function outputLayerF
    ) {

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

    private static boolean[] createArrayWithTrueValues(int length) {
        boolean[] array = new boolean[length];

        Arrays.fill(array, true);

        return array;
    }

    public final int MAX_START_VALUE;
    protected final NetworkLayer[] layers;
    protected final double[] weights;
    protected final boolean[] weightStatuses;

    public NeuralNetwork(double[] weights, NetworkLayer[] layers) {
        this(weights, createArrayWithTrueValues(weights.length), layers);
    }

    public NeuralNetwork(double[] weights, boolean[] weightStatuses, NetworkLayer[] layers) {
        this(layers);

        if (weights.length != this.weights.length) throw new IllegalArgumentException(
            "Given weights length is different from the actual needed to build the network. Given: " + weights.length + " needed: " + this.weights.length
        );

        System.arraycopy(weights, 0, this.weights, 0, this.weights.length);
        System.arraycopy(weightStatuses, 0, this.weightStatuses, 0, this.weightStatuses.length);
    }

    public NeuralNetwork(NetworkLayer[] layers) {
        this(layers, 1);
    }

    public NeuralNetwork(NetworkLayer[] layers, int maxStartValue) {
        if (layers == null) {
            throw new IllegalArgumentException("Layers not null.");
        }

        if (layers.length == 0) {
            throw new IllegalArgumentException("Layers length, must be greater than zero");
        }

        this.layers = layers;

        int sumOfWeights = 0;
        for (int i = 0; i < layers.length; i++) {
            sumOfWeights += layers[i].getNeuronsCount() * layers[i].totalWeightsCount;
        }

        weights = new double[sumOfWeights];
        weightStatuses = createArrayWithTrueValues(sumOfWeights);

        MAX_START_VALUE = maxStartValue;

        for (int i = 0; i<weights.length; i++) {
            weights[i] = Math.random() * MAX_START_VALUE * 2;
        }

        int startPoint = 0;
        for (int i = 0; i < layers.length; i++) {
            layers[i].maxStartValue = MAX_START_VALUE;
            layers[i].buildNeurons(weights, weightStatuses, startPoint);
            startPoint += layers[i].getNeuronsCount() * layers[i].totalWeightsCount;
        }
    }

    @Override
    public int getTotalWeights() {
        return weights.length;
    }

    @Override
    public int getOutputChannelsCount() {
        return 1;
    }

    public int getLayerCount() {
        return layers.length;
    }

    @Override
    public double getWeightAt(int index) {
        return weights[index];
    }

    @Override
    public void setWeightAt(int index, double value) {
        weights[index] = value;
    }

    public NetworkLayer getLayerAt(int position) {
        return layers[position];
    }

    @Override
    public double[] compute(double[] features) {
        double[] results = features;
        for (NetworkLayer layer : layers) {
            results = layer.computeLayer(results);
        }

        return results;
    }

    @Override
    public TrainableLayer copy() {
        double[] weights = Arrays.copyOf(this.weights, this.weights.length);

        NetworkLayer[] layers = new NetworkLayer[this.layers.length];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = this.layers[i].copy();
        }

        return new NeuralNetwork(weights, layers);
    }

    @Override
    public MatrixReader[] execute(MatrixReader[] inputChannels) {
        if (inputChannels.length != 1) throw new IllegalArgumentException(
            "Need exactly one channel as input. Found: " + inputChannels.length
        );

        if (inputChannels[0].getRowsCount() != 1) throw new IllegalArgumentException(
            "The input core.matrix must be flat, exactly one row needed as input. Found: " + inputChannels[0].getRowsCount()
        );

        if (inputChannels[0].getColumnsCount() < 1) throw new IllegalArgumentException(
            "Need at least one column as input. Found: " + inputChannels[0].getColumnsCount()
        );

        // TODO optimize with a simpler structure for a MatrixReader[]

        return new Matrix2D[]{
            new Matrix2D(
                new double[][]{
                    compute(inputChannels[0].getRow(0))
                }
            )
        };
    }

    @Override
    public MatrixSchema[] getSchema(
        MatrixSchema[] inputChannels
    ) {
        int output = layers[layers.length - 1].getNeuronsCount();

        return new MatrixSchema[]{
            new LayerSchema(1, output)
        };
    }

    @Override
    public SchemaRow getSchemaRow(MatrixSchema[] inputSchema) {
        int output = layers[layers.length - 1].getNeuronsCount();

        return new SchemaRow()
            .setLayerType("Dense")
            .setChannelsCount(inputSchema.length)
            .setOutput("1x" + output)
            .setTrainableWeights(weights.length);
    }

    public String toString() {
        StringBuilder output = new StringBuilder();

        int layersCount = layers.length;
        for (int i = 0; i < layersCount; i++) {
            output.append("Layer ").append(i + 1).append(": neurons(").append(layers[i].getNeuronsCount()).append(")\n");
        }

        return output + "\n";
    }

    @Override
    public void setWeightStatus(int index, boolean status) {
        weightStatuses[index] = status;
    }

    @Override
    public boolean getWeightStatus(int index) {
        return weightStatuses[index];
    }

    @Override
    public void reverseWeightStatus(int index) {
        weightStatuses[index] = !weightStatuses[index];
    }

    protected double[] computeLayer(int layerIndex, double[] features) {
        return layers[layerIndex].computeLayer(features);
    }
}
