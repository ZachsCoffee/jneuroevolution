package builder;

import executors.common.TrainableConvolution;
import layer.TrainableLayer;

import java.util.LinkedList;
import java.util.List;

public class ConvolutionBuilder {

    public static ConvolutionBuilder getInstance(int inputChannelsCount) {
        return new ConvolutionBuilder(inputChannelsCount);
    }

    private final int inputChannelsCount;
    private final List<TrainableLayer> layers = new LinkedList<>();
    private TrainableConvolutionLayerBuilder lastLayerBuilder = null;

    private ConvolutionBuilder(int inputChannelsCount) {

        this.inputChannelsCount = inputChannelsCount;
    }

    public TrainableConvolutionLayerBuilder addLayer() {
        int channelsCount;

        if (lastLayerBuilder == null) {
            channelsCount = inputChannelsCount;
        }
        else {
            TrainableLayer layer = lastLayerBuilder.build();
            layers.add(layer);
            channelsCount = layer.getOutputChannelsCount();
        }

        lastLayerBuilder = new TrainableConvolutionLayerBuilder(this, channelsCount);

        return lastLayerBuilder;
    }

    public TrainableConvolution build() {
        if (lastLayerBuilder == null) throw new IllegalStateException(
            "Need at least one layer in order to build"
        );

        layers.add(lastLayerBuilder.build());

        return new executors.TrainableConvolution(
            layers
        );
    }
}
