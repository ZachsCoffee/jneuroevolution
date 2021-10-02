package networks.multilayer_perceptron.builders;

import functions.ActivationFunction;
import maths.Function;
import networks.multilayer_perceptron.network.NetworkLayer;
import networks.multilayer_perceptron.network.NeuralNetwork;
import networks.multilayer_perceptron.serializers.NetworkJsonSerializer;

import java.util.LinkedList;
import java.util.List;

public class NeuralNetworkBuilder {

    public static NeuralNetwork buildFrom(NetworkModel networkModel) {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder(
                networkModel.getFeatures(),
                networkModel.getLayers()[0].neurons,
                networkModel.getLayers()[0].activationFunction.getFunction()
        );

        for (int i=1; i<networkModel.getLayers().length; i++) {
            builder.addLayer(networkModel.getLayers()[i].neurons, networkModel.getLayers()[i].activationFunction.getFunction());
        }

        return builder.build(networkModel.getWeights());
    }

    public static NeuralNetworkBuilder initialize(int featureLength, int firstLayerNeurons) {
        return new NeuralNetworkBuilder(featureLength, firstLayerNeurons, null);
    }

    public static NeuralNetworkBuilder initialize(int featureLength, int firstLayerNeurons, Function function) {
        return new NeuralNetworkBuilder(featureLength, firstLayerNeurons, function);
    }

    private final List<LayerSchema> layerSchemas = new LinkedList<>();
    private int initialFeatureLength;

    private NeuralNetworkBuilder(int featureLength, int firstLayerNeurons, Function function) {
        initialFeatureLength = featureLength;
        layerSchemas.add(new LayerSchema(firstLayerNeurons, function));
    }

    private NeuralNetworkBuilder() {

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
        NetworkLayer[] layers = new NetworkLayer[layerSchemas.size()];

        int i = 0;
        int pastLayerNeurons = initialFeatureLength;
        for (LayerSchema layerSchema : layerSchemas) {
            layers[i++] = new NetworkLayer(layerSchema.neurons, pastLayerNeurons, layerSchema.activationFunction.getFunction());
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
