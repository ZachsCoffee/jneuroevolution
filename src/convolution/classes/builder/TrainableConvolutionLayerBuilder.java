package builder;

import core.builder.AbstractChainableBuilder;
import core.builder.TrainableBuilder;
import layers.trainable.TrainableConvolutionLayer;

public class TrainableConvolutionLayerBuilder<T> extends AbstractChainableBuilder<T> {

    public static <T> TrainableConvolutionLayerBuilder<T> initialize(int inputChannelsCount) {
        return new TrainableConvolutionLayerBuilder<>(inputChannelsCount);
    }

    private final int inputChannelsCount;
    private int kernelsPerChannel = 1;
    private boolean sumKernels = false;
    private int stride = 1;
    private boolean keepSize = false;

    private TrainableConvolutionLayerBuilder(int inputChannelsCount) {
        if (inputChannelsCount < 1) throw new IllegalArgumentException(
            "Need at least one channel. Given: " + inputChannelsCount
        );

        this.inputChannelsCount = inputChannelsCount;
    }

    public int getKernelsPerChannel() {
        return kernelsPerChannel;
    }

    public TrainableConvolutionLayerBuilder<T> setKernelsPerChannel(int kernelsPerChannel) {
        if (kernelsPerChannel < 1) throw new IllegalArgumentException(
            "Kernels per channel can't be less than one."
        );

        this.kernelsPerChannel = kernelsPerChannel;
        return this;
    }

    public boolean isSumKernels() {
        return sumKernels;
    }

    public TrainableConvolutionLayerBuilder<T> setSumKernels(boolean sumKernels) {
        this.sumKernels = sumKernels;
        return this;
    }

    public int getStride() {
        return stride;
    }

    public TrainableConvolutionLayerBuilder<T> setStride(int stride) {
        if (stride < 1) throw new IllegalArgumentException(
            "Stride can't be smaller than 1. Given: " + stride
        );

        this.stride = stride;
        return this;
    }

    public boolean isKeepSize() {
        return keepSize;
    }

    public TrainableConvolutionLayerBuilder<T> setKeepSize(boolean keepSize) {
        this.keepSize = keepSize;
        return this;
    }

    public TrainableConvolutionLayer build() {
        return new TrainableConvolutionLayer(
            inputChannelsCount,
            kernelsPerChannel,
            sumKernels,
            stride,
            keepSize
        );
    }
}
