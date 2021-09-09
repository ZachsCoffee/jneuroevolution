package networks.multilayer_perceptron;

import maths.Function;

import java.util.LinkedList;
import java.util.List;

public class NeuralNetworkBuilder {

    public static NeuralNetworkBuilder initialize(int featureLength, int firstLayerNeurons) {
        return new NeuralNetworkBuilder(featureLength, firstLayerNeurons, null);
    }

    public static NeuralNetworkBuilder initialize(int featureLength, int firstLayerNeurons, Function function) {
        return new NeuralNetworkBuilder(featureLength, firstLayerNeurons, function);
    }

    private final List<LayerSchema> layerSchemas = new LinkedList<>();
    private final int initialFeatureLength;

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

    private NetworkLayer[] buildLayers() {
        NetworkLayer[] layers = new NetworkLayer[layerSchemas.size()];

        int i = 0;
        int pastLayerNeurons = initialFeatureLength;
        for (LayerSchema layerSchema : layerSchemas) {
            layers[i++] = new NetworkLayer(layerSchema.neurons, pastLayerNeurons, layerSchema.function);
            pastLayerNeurons = layerSchema.neurons;
        }

        return layers;
    }

    private static class LayerSchema {
        final int neurons;
        final Function function;

        LayerSchema(int neurons, Function function) {
            this.neurons = neurons;
            this.function = function;
        }
    }
}
