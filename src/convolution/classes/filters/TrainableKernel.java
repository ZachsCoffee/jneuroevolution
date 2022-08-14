package filters;

import core.layer.MatrixReader;
import maths.Function;

import java.util.Objects;

public class TrainableKernel implements Kernel {

    private final int EXTRA_WEIGHTS = 1;
    private final int startIndex, endIndex, biasIndex;
    private final int kernelTotalWeightsCount, kernelWeightsCount;
    private final int kernelSize;
    private final Function function;
    private final boolean withBias;
    private final boolean withFunction;
    private double[] globalWeights;

    public TrainableKernel(
        int startIndex,
        int kernelSize,
        Function function,
        boolean withBias
    ) {
        if (startIndex < 0) throw new IllegalArgumentException(
            "Start index can't be negative number"
        );

        if (kernelSize < 2) throw new IllegalArgumentException(
            "Rows must be greater than 2. Given: " + kernelSize
        );

        this.kernelWeightsCount = kernelSize * kernelSize;

        if (withBias) {
            endIndex = startIndex + kernelWeightsCount + EXTRA_WEIGHTS;
            this.biasIndex = endIndex - 1;
        }
        else {
            endIndex = startIndex + kernelWeightsCount;
            this.biasIndex = -1;
        }

        kernelTotalWeightsCount = endIndex - startIndex;

        if (kernelTotalWeightsCount == 0) throw new IllegalArgumentException(
            "The given indexes are equal."
        );


        this.kernelSize = kernelSize;
        this.withBias = withBias;
        this.startIndex = startIndex;
        this.function = function;

        this.withFunction = function != null;
    }

    public void setGlobalWeights(double[] globalWeights) {
        Objects.requireNonNull(globalWeights);

        if (globalWeights.length == 0) throw new IllegalArgumentException(
            "The given global weights are zero."
        );

        if (kernelTotalWeightsCount > globalWeights.length) throw new IllegalArgumentException(
            "The given indexes creates more weighs than the given global weighs."
        );

        if (startIndex >= globalWeights.length) throw new IndexOutOfBoundsException(
            "The start index is out of bounds. Start index: " + startIndex + " global weights length: " + globalWeights.length
        );

        if (endIndex >= globalWeights.length) throw new IndexOutOfBoundsException(
            "The end is out of bounds. Start index: " + endIndex + " global weights length: " + globalWeights.length
        );

        this.globalWeights = globalWeights;
    }

    public int getKernelTotalWeightsCount() {
        return kernelTotalWeightsCount;
    }

    public int getKernelWeightsCount() {
        return kernelWeightsCount;
    }

    public boolean isWithBias() {
        return withBias;
    }

    public double compute(MatrixReader data, int startRowIndex, int startColumnIndex) {
        if (globalWeights == null) throw new IllegalStateException(
            "Need the global weights in order to compute"
        );

        int endRowIndex = startRowIndex + kernelSize;
        int endColumnIndex = startColumnIndex + kernelSize;

        double result = 0;

        int kernelIndex = startIndex;

        for (int i = startRowIndex; i < endRowIndex; i++) {
            for (int j = startColumnIndex; j < endColumnIndex; j++) {
                result += globalWeights[kernelIndex] * data.valueAt(i, j);
                kernelIndex++;
            }
        }

        if (withBias) {
            result += globalWeights[biasIndex];
        }

        if (withFunction) {
            result = function.compute(result);
        }

        return result;
    }

    @Override
    public int getKernelSize() {
        return kernelSize;
    }
}
