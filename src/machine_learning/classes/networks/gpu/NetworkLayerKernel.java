package networks.gpu;

import com.amd.aparapi.Kernel;

import java.util.Objects;

public class NetworkLayerKernel extends Kernel {

    @Constant()
    protected final float[] weights;
    protected final float[] output;
    @Constant()
    private int weightOffset;
    @Constant()
    private int lastNeuronsCount;
    @Constant()
    private int lastOutputOffset;

    protected float[] productCache;

    public NetworkLayerKernel(float[] weights, float[] output) {
        this.weights = Objects.requireNonNull(weights);
        this.output = Objects.requireNonNull(output);

        setExecutionMode(EXECUTION_MODE.GPU);
    }

    public float[] getWeights() {
        return weights;
    }

    public float[] getOutput() {
        return output;
    }

    public int getWeightOffset() {
        return weightOffset;
    }

    public NetworkLayerKernel setWeightOffset(int weightOffset) {
        this.weightOffset = weightOffset;
        return this;
    }

    public int getLastNeuronsCount() {
        return lastNeuronsCount;
    }

    public NetworkLayerKernel setLastNeuronsCount(int lastNeuronsCount) {
        this.lastNeuronsCount = lastNeuronsCount;
        return this;
    }

    public int getLastOutputOffset() {
        return lastOutputOffset;
    }

    public NetworkLayerKernel setLastOutputOffset(int lastOutputOffset) {
        this.lastOutputOffset = lastOutputOffset;
        return this;
    }

    public float[] getProductCache() {
        return productCache;
    }

    public NetworkLayerKernel setProductCache(float[] productCache) {
        this.productCache = productCache;
        return this;
    }

    @Override
    public void run() {
        int groupIndex = getGroupId();
        int localSize = getLocalSize();
        int localIndex = getLocalId();

        if (localIndex == localSize - 1) {
            productCache[localIndex] = weights[weightOffset + localIndex];
        }
        else {
            productCache[localIndex] = weights[weightOffset + localIndex] * output[lastOutputOffset + localIndex];
        }

        localBarrier();

        // start to sum the products

        int pivot = localSize;
        int lastPivot = localSize;

        if (localIndex >= pivot) {
            return;
        }

        float sum;
        int second;
        do {
            pivot = (int) ceil(pivot / 2.0f);

            sum = productCache[localIndex];
            second = pivot + localIndex;
            if (second < lastPivot) {
                sum += productCache[second];
            }

            productCache[localIndex] = sum;

            lastPivot = pivot;

            localBarrier();
        } while (pivot > 1);

        output[lastOutputOffset + lastNeuronsCount + groupIndex] = productCache[0];
    }
}
