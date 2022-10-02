package builder;

import core.builder.AbstractChainableBuilder;
import core.layer.Layer;
import layers.pool.PoolFunction;
import layers.pool.PoolLayer;

public class PoolingLayerBuilder<T> extends AbstractChainableBuilder<T> {

    public static <T> PoolingLayerBuilder<T> initialize(int inputChannelsCount) {
        return new PoolingLayerBuilder<>(inputChannelsCount);
    }

    private final int inputChannelsCount;
    private PoolFunction poolFunction = PoolFunction.MAX;
    private int sampleSize = 3;
    private int stride = 1;

    private PoolingLayerBuilder(int inputChannelsCount) {
        this.inputChannelsCount = inputChannelsCount;
    }

    public PoolFunction getPoolFunction() {
        return poolFunction;
    }

    public PoolingLayerBuilder<T> setPoolFunction(PoolFunction poolFunction) {
        this.poolFunction = poolFunction;
        return this;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public PoolingLayerBuilder<T> setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
        return this;
    }

    public int getStride() {
        return stride;
    }

    public PoolingLayerBuilder<T> setStride(int stride) {
        this.stride = stride;
        return this;
    }

    @Override
    public Layer build() {
        return new PoolLayer(inputChannelsCount, poolFunction, sampleSize, stride);
    }
}
