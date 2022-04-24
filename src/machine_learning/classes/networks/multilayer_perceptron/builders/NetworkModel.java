package networks.multilayer_perceptron.builders;

import functions.ActivationFunction;
import networks.multilayer_perceptron.network.NeuralNetwork;

import java.util.Objects;

public class NetworkModel {
    public static final int VERSION = 1;

    public static NetworkModel from(NeuralNetwork neuralNetwork) {
        int weightsLength = neuralNetwork.getWeightsCount();
        double[] weights = new double[weightsLength];

        for (int i=0; i<weightsLength; i++) {
            weights[i] = neuralNetwork.getWeightAt(i);
        }

        NeuralNetworkBuilder.LayerSchema[] layerSchemas = new NeuralNetworkBuilder.LayerSchema[neuralNetwork.getLayerCount()];
        int layersLength = neuralNetwork.getLayerCount();
        for (int i=0; i<layersLength; i++) {
            layerSchemas[i] = new NeuralNetworkBuilder.LayerSchema(
                    neuralNetwork.getLayerAt(i).getNeuronsCount(),
                    neuralNetwork.getLayerAt(i).getFunction()
            );
        }

        return new NetworkModel(neuralNetwork.getLayerAt(0).getLayerInputCount(), weights, layerSchemas);
    }

    private int features;
    private double[] weights;
    private NeuralNetworkBuilder.LayerSchema[] layers;

    private NetworkModel() {

    }

    public NetworkModel(int features, double[] weights, NeuralNetworkBuilder.LayerSchema[] layers) {
        this.features = features;
        this.weights = Objects.requireNonNull(weights);
        this.layers = Objects.requireNonNull(layers);
    }

    public double[] getWeights() {
        return weights;
    }

    public NeuralNetworkBuilder.LayerSchema[] getLayers() {
        return layers;
    }

    public int getFeatures() {
        return features;
    }
}
