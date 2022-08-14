package common;

import builder.TrainableConvolutionLayerBuilder;
import core.builder.AbstractChainableBuilder;
import core.builder.TrainableBuilder;
import executors.TrainableSystem;
import core.layer.TrainableLayer;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;

import java.util.LinkedList;
import java.util.List;

public class TrainableSystemBuilder extends AbstractChainableBuilder {

    public static TrainableSystemBuilder getInstance(int inputChannelsCount) {
        return new TrainableSystemBuilder(inputChannelsCount);
    }

    private final int inputChannelsCount;
    private final List<TrainableLayer> layers = new LinkedList<>();
    private TrainableBuilder lastLayerBuilder = null;

    private TrainableSystemBuilder(int inputChannelsCount) {
        this.inputChannelsCount = inputChannelsCount;
    }

    public TrainableConvolutionLayerBuilder addConvolutionLayer() {
        TrainableConvolutionLayerBuilder builder = TrainableConvolutionLayerBuilder.initialize(getNextLayerChannelsCount());
        builder.setParentBuilder(this);

        return (TrainableConvolutionLayerBuilder) (lastLayerBuilder = builder);
    }

    public NeuralNetworkBuilder addNeuralNetworkLayer() {
        NeuralNetworkBuilder builder = NeuralNetworkBuilder.initialize(getNextLayerChannelsCount());
        builder.setParentBuilder(this);

        return (NeuralNetworkBuilder) (lastLayerBuilder = builder);
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
