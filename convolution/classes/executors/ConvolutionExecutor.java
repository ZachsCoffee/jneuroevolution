package executors;

import convolution.ConvolutionLayer;
import convolution.Layer;
import filters.Filter;
import filters.Kernel;
import functions.ActivationFunctions;
import input.HsbInput;
import input.ImageInput;
import maths.matrix.MatrixReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvolutionExecutor {

    public static ConvolutionExecutor initialize(ImageInput imageInput) {
        return new ConvolutionExecutor(imageInput.getChannels());
    }

    protected final MatrixReader[] channels;
    private final ArrayList<Layer>[] channelLayers;
    private MatrixReader[] output;

    protected ConvolutionExecutor(MatrixReader[] channels) {
        this.channels = Objects.requireNonNull(channels);

        if (channels.length == 0) {
            throw new IllegalArgumentException("Need at least one channel!");
        }

        channelLayers = (ArrayList<Layer>[]) new ArrayList[channels.length];
        for (int i=0; i<channelLayers.length; i++) {
            channelLayers[i] = new ArrayList<>();
        }

        output = new MatrixReader[channels.length];
    }

    public ConvolutionExecutor addLayerForAllChannels(Layer layer) {
        for (ArrayList<Layer> channel : channelLayers) {
            channel.add(layer);
        }
        return this;
    }

    public void execute() {
        for (int i=0; i<channels.length; i++) {
            output[i] = computeChannel(i);
        }
    }

    protected MatrixReader computeChannel(int channelIndex) {
        MatrixReader previousMatrixReader = channels[channelIndex];
        for (Layer layer : channelLayers[channelIndex]) {
            previousMatrixReader = layer.computeLayer(previousMatrixReader);
        }

        return previousMatrixReader;
    }



    public static void main(String[] args) {
        ConvolutionExecutor.initialize(new HsbInput(new File("/home/zachs/Downloads/ΟΔ 5396.jpg")))
                .addLayerForAllChannels(new ConvolutionLayer(new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()), 1, true))
                .executeParallel(readers -> System.out.println("Done!"));

    }
}
