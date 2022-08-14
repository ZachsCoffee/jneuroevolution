package common;

import builder.TrainableConvolutionLayerBuilder;
import core.builder.AbstractChainableBuilder;
import core.builder.TrainableBuilder;
import core.layer.Layer;
import executors.TrainableSystem;
import core.layer.TrainableLayer;
import layers.flatten.FlatLayer;
import layers.pool.PoolFunction;
import layers.pool.PoolLayer;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;

import java.util.LinkedList;
import java.util.List;

public class TrainableSystemBuilder<T> extends AbstractChainableBuilder<T> {

    public static <T> TrainableSystemBuilder<T> getInstance(int inputChannelsCount) {
        return new TrainableSystemBuilder<>(inputChannelsCount);
    }

    private final int inputChannelsCount;
    private final List<Layer> layers = new LinkedList<>();
    private TrainableBuilder<?> lastLayerBuilder = null;

    private TrainableSystemBuilder(int inputChannelsCount) {
        this.inputChannelsCount = inputChannelsCount;
    }

    public TrainableConvolutionLayerBuilder<TrainableSystemBuilder<T>> addConvolutionLayer() {
        TrainableConvolutionLayerBuilder<TrainableSystemBuilder<T>> builder = TrainableConvolutionLayerBuilder.initialize(getNextLayerChannelsCount());
        builder.setParentBuilder(this);

        return (TrainableConvolutionLayerBuilder<TrainableSystemBuilder<T>>) (lastLayerBuilder = builder);
    }

    public NeuralNetworkBuilder<TrainableSystemBuilder<T>> addNeuralNetworkLayer() {
        layers.add(new FlatLayer());

        NeuralNetworkBuilder<TrainableSystemBuilder<T>> builder = NeuralNetworkBuilder.initialize(getNextLayerChannelsCount());
        builder.setParentBuilder(this);

        return (NeuralNetworkBuilder<TrainableSystemBuilder<T>>) (lastLayerBuilder = builder);
    }

    public TrainableSystemBuilder<T> addPoolingLayer(PoolFunction poolFunction, int sampleSize, int stride) {
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
            layers.add(layer);
            channelsCount = layer.getOutputChannelsCount();
        }

        return channelsCount;
    }
}
