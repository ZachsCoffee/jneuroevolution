package common;

import builder.TrainableConvolutionLayerBuilder;
import core.builder.AbstractChainableBuilder;
import core.builder.TrainableBuilder;
import core.layer.Layer;
import core.layer.MatrixSchema;
import core.schema.LayerSchema;
import executors.TrainableSystem;
import core.layer.TrainableLayer;
import layers.flatten.FlatLayer;
import layers.pool.PoolFunction;
import layers.pool.PoolLayer;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TrainableConvolutionSystemBuilder<T> extends AbstractChainableBuilder<T> {

    public static <T> TrainableConvolutionSystemBuilder<T> getInstance(int inputChannelsCount, int rowsCount, int columnsCount) {
        return new TrainableConvolutionSystemBuilder<>(inputChannelsCount, rowsCount, columnsCount);
    }

    private final int inputChannelsCount;
    private final int inputRows;
    private final int inputColumns;
    private final ArrayList<Layer> layers = new ArrayList<>();
    private MatrixSchema[] lastLayerOutputSchema;
    private TrainableBuilder<?> lastLayerBuilder = null;

    private TrainableConvolutionSystemBuilder(int inputChannelsCount, int inputRows, int inputColumns) {
        if (inputRows < 1) throw new IllegalArgumentException(
            "Need at least one input row."
        );

        if (inputColumns < 1) throw new IllegalArgumentException(
            "Need at least one input column."
        );

        this.inputChannelsCount = inputChannelsCount;
        this.inputRows = inputRows;
        this.inputColumns = inputColumns;

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
            layers.add(lastLayerBuilder.build());
        }

        if (!layers.isEmpty()) {
            Layer lastLayer = layers.get(layers.size() -1);

            lastLayerOutputSchema = lastLayer.getSchema(lastLayerOutputSchema);
        }

        return builder;
    }

    public NeuralNetworkBuilder<TrainableConvolutionSystemBuilder<T>> addNeuralNetworkLayer() {
        FlatLayer flatLayer = new FlatLayer();

        if (lastLayerBuilder != null) {
            TrainableLayer layer = lastLayerBuilder.build();
            layers.add(layer);
            lastLayerOutputSchema = layer.getSchema(lastLayerOutputSchema);
        }

        NeuralNetworkBuilder<TrainableConvolutionSystemBuilder<T>> builder = NeuralNetworkBuilder.initialize(
            flatLayer.computeOutputSize(flatLayer.getSchema(lastLayerOutputSchema))
        );

        builder.setParentBuilder(this);

        lastLayerBuilder = builder;
        layers.add(flatLayer);

        return builder;
    }

    public TrainableConvolutionSystemBuilder<T> addPoolingLayer(PoolFunction poolFunction, int sampleSize, int stride) {

        layers.add(new PoolLayer(poolFunction, sampleSize, stride));

        return this;
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
            TrainableLayer layer = lastLayerBuilder.build();

            channelsCount = layer.getOutputChannelsCount();
        }

        return channelsCount;
    }

    private void buildLastBuilderLayer() {
        if (lastLayerBuilder != null) {
            layers.add(lastLayerBuilder.build());
        }
    }
}
