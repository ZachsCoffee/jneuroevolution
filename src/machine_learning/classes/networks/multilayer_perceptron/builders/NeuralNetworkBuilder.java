package networks.multilayer_perceptron.builders;

import core.builder.AbstractChainableBuilder;
import functions.ActivationFunction;
import maths.Function;
import networks.multilayer_perceptron.network.NetworkLayer;
import networks.multilayer_perceptron.network.NeuralNetwork;

import java.util.LinkedList;
import java.util.List;

public class NeuralNetworkBuilder<T> extends AbstractChainableBuilder<T> {

    public static NeuralNetwork buildFrom(NetworkModel networkModel) {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder(
            networkModel.getFeatures(),
            networkModel.getLayers()[0].neurons,
            networkModel.getLayers()[0].activationFunction != null
                ? networkModel.getLayers()[0].activationFunction.getFunction()
                : null
        );

        for (int i = 1; i < networkModel.getLayers().length; i++) {
            builder.addLayer(
                networkModel.getLayers()[i].neurons,
                networkModel.getLayers()[i].activationFunction != null
                    ? networkModel.getLayers()[i].activationFunction.getFunction()
                    : null
            );
        }

        return builder.build(networkModel.getWeights());
    }

    public static <T> NeuralNetworkBuilder<T> initialize(int featureLength) {
        return new NeuralNetworkBuilder<>(featureLength);
    }

    public static NeuralNetworkBuilder initialize(int featureLength, int firstLayerNeurons) {
        return new NeuralNetworkBuilder(featureLength, firstLayerNeurons, null);
    }

    public static NeuralNetworkBuilder initialize(int featureLength, int firstLayerNeurons, Function function) {
        return new NeuralNetworkBuilder(featureLength, firstLayerNeurons, function);
    }

    private final List<LayerSchema> layerSchemas = new LinkedList<>();
    private final int initialFeatureLength;

    private NeuralNetworkBuilder(int featureLength) {
        initialFeatureLength = featureLength;
    }

    private NeuralNetworkBuilder(int featureLength, int firstLayerNeurons, Function function) {
        initialFeatureLength = featureLength;
        layerSchemas.add(new LayerSchema(firstLayerNeurons, function));
    }

    public NeuralNetworkBuilder addLayer(int neurons) {
        layerSchemas.add(new LayerSchema(neurons, null));

        return this;
    }

    public NeuralNetworkBuilder addLayer(int neurons, Function function) {
        layerSchemas.add(new LayerSchema(neurons, function));

        return this;
    }

    public NeuralNetwork build() {
        return new NeuralNetwork(buildLayers());
    }

    public NeuralNetwork build(int maxWeightValue) {
        return new NeuralNetwork(buildLayers(), maxWeightValue);
    }

    public NeuralNetwork build(double[] weights) {
        return new NeuralNetwork(weights, buildLayers());
    }

    private NetworkLayer[] buildLayers() {
        if (layerSchemas.isEmpty()) throw new IllegalStateException(
            "Need at least one layer in order to build the network."
        );

        NetworkLayer[] layers = new NetworkLayer[layerSchemas.size()];

        int i = 0;
        int pastLayerNeurons = initialFeatureLength;
        for (LayerSchema layerSchema : layerSchemas) {
            layers[i++] = new NetworkLayer(
                layerSchema.neurons,
                pastLayerNeurons,
                layerSchema.activationFunction != null
                    ? layerSchema.activationFunction.getFunction()
                    : null
            );
            pastLayerNeurons = layerSchema.neurons;
        }

        return layers;
    }

    public static class LayerSchema {

        int neurons;
        ActivationFunction activationFunction;

        public LayerSchema(int neurons, Function function) {
            this.neurons = neurons;
            this.activationFunction = ActivationFunction.getByFunction(function);
        }

        private LayerSchema() {

        }

        public int getNeurons() {
            return neurons;
        }

        public ActivationFunction getActivationFunction() {
            return activationFunction;
        }
    }
}
