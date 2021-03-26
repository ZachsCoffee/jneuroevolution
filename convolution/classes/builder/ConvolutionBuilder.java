package builder;

import convolution.Layer;
import input.ImageInput;
import maths.matrix.MatrixReader;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class ConvolutionBuilder {

    private final MatrixReader[] channels;

    public static ConvolutionBuilder initialize(ImageInput imageInput) {
        return new ConvolutionBuilder(imageInput.getChannels());
    }

    private final ArrayList<Layer>[] channelLayers;
    private final ArrayList<Layer> layers = new ArrayList<>();

    private ConvolutionBuilder(MatrixReader[] channels) {
        this.channels = Objects.requireNonNull(channels);

        if (channels.length == 0) {
            throw new IllegalArgumentException("Need at least one channel!");
        }

        channelLayers = (ArrayList<Layer>[]) new ArrayList[channels.length];
    }

    public ConvolutionBuilder addLayerForAllChannels(Layer layer) {
        for (ArrayList<Layer> channel : channelLayers) {
            channel.add(layer);
        }
        // TODO na ftiaksw mia private pou na kanei compute to channel
        // TODO na ftiaksw mia 2 public pou na kanoun compute ta channel mia me thread
        return this;
    }
}
