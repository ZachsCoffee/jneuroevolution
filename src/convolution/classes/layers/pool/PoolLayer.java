package layers.pool;

import layers.convolution.ConvolutionUtils;
import layers.Layer;
import schema.LayerSchema;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixReader2D;
import maths.matrix.MatrixSchema;
import schema.ConvolutionSchemaPrinter;

import java.util.Objects;

public class PoolLayer implements Layer {

    public static final int MIN_SAMPLE_SIZE = 2;

    private final PoolFunction poolFunction;
    private final int sampleSize;
    private final int stride;

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

    public MatrixReader[] computeLayer(MatrixReader[] channel) {
        Objects.requireNonNull(channel);
        if (channel.length == 0) {
            throw new IllegalArgumentException("Need at least one matrix reader!");
        }

        MatrixReader[] output = new MatrixReader[channel.length];

        for (int i=0; i<output.length; i++) {
            output[i] = computeMatrix(channel[i]);
        }

        return output;
    }

    @Override
    public MatrixSchema[] getSchema(MatrixSchema[] channel, ConvolutionSchemaPrinter convolutionSchemaPrinter) {

        MatrixSchema[] matrixSchemas = new MatrixSchema[channel.length];
        int[] dimensions = null;
        for (int i = 0; i< channel.length; i++) {
            dimensions = ConvolutionUtils.outputDimensions(
                    channel[i].getRowCount(),
                    channel[i].getColumnCount(),
                    sampleSize,
                    0,
                    stride
            );

            matrixSchemas[i] = new LayerSchema(dimensions[0], dimensions[1]);
        }

        convolutionSchemaPrinter.addRow(
                "Pool",
                channel.length,
                "-",
                sampleSize + "x" + sampleSize,
                stride,
                0,
                channel.length + "x" + dimensions[0] + "x" + dimensions[1]
        );

        return matrixSchemas;
    }

    private MatrixReader computeMatrix(MatrixReader input) {
        int[] dimensions = ConvolutionUtils.outputDimensions(
                input.getRowCount(),
                input.getColumnCount(),
                sampleSize,
                0,
                stride
        );

        double[][] output = new double[dimensions[0]][dimensions[1]];

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
