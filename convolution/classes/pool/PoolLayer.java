package pool;

import convolution.ConvolutionUtils;
import convolution.Layer;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixReader2D;

import java.util.Objects;

public class PoolLayer implements Layer {

    public static final int MIN_SAMPLE_SIZE = 2;

    private final PoolFunction poolFunction;
    private final int sampleSize;
    private final int stride;
    private double[][] output;

    public PoolLayer(PoolFunction poolFunction, int sampleSize, int stride) {

        if (sampleSize < MIN_SAMPLE_SIZE) throw new IllegalArgumentException(
                "Sample size must be at least: "+MIN_SAMPLE_SIZE+" and not: "+sampleSize
        );
        if (stride < 1) throw new IllegalArgumentException(
                "Stride must be at least 1, and not: "+stride
        );

        this.poolFunction = Objects.requireNonNull(poolFunction);
        this.sampleSize = sampleSize;
        this.stride = stride;

    }

    public MatrixReader computeLayer(MatrixReader input) {
        Objects.requireNonNull(input);

        int[] dimensions = ConvolutionUtils.outputDimensions(
                input.getRowCount(),
                input.getColumnCount(),
                sampleSize,
                0,
                stride
        );

        output = new double[dimensions[0]][dimensions[1]];

        int rowsCount = input.getRowCount();
        int columnsCount = input.getColumnCount();

        for (int i=0, outI=0; i<rowsCount - sampleSize; i += stride, outI++) {
            for (int j=0, outJ=0; j<columnsCount - sampleSize; j += stride, outJ++) {
                output[outI][outJ] = computeSample(input, i, j);
            }
        }

        return new MatrixReader2D(output);
    }

    private double computeSample(MatrixReader input, int rowIndex, int columnIndex) {
        int sampleRowEnd = rowIndex + sampleSize;
        int sampleColumnEnd = columnIndex + sampleSize;

        PoolFunction.Function function = poolFunction.getFunction();

        for (int i=rowIndex; i<sampleRowEnd; i++) {
            for (int j=columnIndex; j<sampleColumnEnd; j++) {
                function.compute(input.valueAt(rowIndex, columnIndex));
            }
        }

        return function.getResult();
    }
}
