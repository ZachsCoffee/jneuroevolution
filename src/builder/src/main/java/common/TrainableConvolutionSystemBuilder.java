package common;

import builder.PoolingLayerBuilder;
import builder.TrainableConvolutionLayerBuilder;
import core.builder.AbstractChainableBuilder;
import core.builder.LayerBuilder;
import core.layer.CountableOutput;
import core.layer.Layer;
import core.layer.MatrixSchema;
import core.schema.LayerSchema;
import executors.TrainableSystem;
import layers.flatten.FlatLayer;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;

import java.util.ArrayList;

public class TrainableConvolutionSystemBuilder<T> extends AbstractChainableBuilder<T> {

    public static <T> TrainableConvolutionSystemBuilder<T> getInstance(int inputChannelsCount, int rowsCount, int columnsCount) {
        return new TrainableConvolutionSystemBuilder<>(inputChannelsCount, rowsCount, columnsCount);
    }

    private final int inputChannelsCount;
    private final ArrayList<Layer> layers = new ArrayList<>();
    private MatrixSchema[] lastLayerOutputSchema;
    private LayerBuilder<?> lastLayerBuilder = null;

    private TrainableConvolutionSystemBuilder(int inputChannelsCount, int inputRows, int inputColumns) {
        if (inputRows < 1) throw new IllegalArgumentException(
            "Need at least one input row."
        );

        if (inputColumns < 1) throw new IllegalArgumentException(
            "Need at least one input column."
        );

        this.inputChannelsCount = inputChannelsCount;

        LayerSchema layerSchema = new LayerSchema(inputRows, inputColumns);
        lastLayerOutputSchema = new MatrixSchema[inputChannelsCount];
        for (int i=0; i<inputChannelsCount; i++) {
            lastLayerOutputSchema[i] = layerSchema;
        }
    }

    public TrainableConvolutionLayerBuilder<TrainableConvolutionSystemBuilder<T>> addConvolutionLayer() {
        TrainableConvolutionLayerBuilder<TrainableConvolutionSystemBuilder<T>> builder = TrainableConvolutionLayerBuilder.initialize(getNextLayerChannelsCount());

        builder.setParentBuilder(this);

        if (lastLayerBuilder != null) {
            Layer layer = lastLayerBuilder.build();
            layers.add(layer);
            lastLayerOutputSchema = layer.getSchema(lastLayerOutputSchema);
        }

        lastLayerBuilder = builder;

        return builder;
    }

    public NeuralNetworkBuilder<TrainableConvolutionSystemBuilder<T>> addNeuralNetworkLayer() {
        FlatLayer flatLayer = new FlatLayer();

        if (lastLayerBuilder != null) {
            Layer layer = lastLayerBuilder.build();
            layers.add(layer);
            lastLayerOutputSchema = layer.getSchema(lastLayerOutputSchema);
            lastLayerOutputSchema = flatLayer.getSchema(lastLayerOutputSchema);
        }

        NeuralNetworkBuilder<TrainableConvolutionSystemBuilder<T>> builder = NeuralNetworkBuilder.initialize(
            flatLayer.computeOutputSize(flatLayer.getSchema(lastLayerOutputSchema))
        );

        builder.setParentBuilder(this);

        lastLayerBuilder = builder;
        layers.add(flatLayer);

        return builder;
    }

    public PoolingLayerBuilder<TrainableConvolutionSystemBuilder<T>> addPoolingLayer() {
        if (lastLayerBuilder != null) {
            Layer layer = lastLayerBuilder.build();
            layers.add(layer);
            lastLayerOutputSchema = layer.getSchema(lastLayerOutputSchema);
        }

        PoolingLayerBuilder<TrainableConvolutionSystemBuilder<T>> poolingLayerBuilder = PoolingLayerBuilder.initialize(lastLayerOutputSchema.length);

        poolingLayerBuilder.setParentBuilder(this);

        lastLayerBuilder = poolingLayerBuilder;

        return poolingLayerBuilder;
    }

    public TrainableSystem build() {
        if (lastLayerBuilder == null) throw new IllegalStateException(
            "Need at least one layer in order to build"
        );

        layers.add(lastLayerBuilder.build());

        return new TrainableSystem(
            layers
        );
    }

    private int getNextLayerChannelsCount() {
        int channelsCount;

        if (lastLayerBuilder == null) {
            channelsCount = inputChannelsCount;
        }
        else {
            Layer layer = lastLayerBuilder.build();

            if (layer instanceof CountableOutput) {
                channelsCount = ((CountableOutput)layer).getOutputChannelsCount();
            }
            else {
                throw new IllegalStateException("The previous layer must have countable output.");
            }
        }

        return channelsCount;
    }

    private void buildLastBuilderLayer() {
        if (lastLayerBuilder != null) {
            layers.add(lastLayerBuilder.build());
        }
    }
}
