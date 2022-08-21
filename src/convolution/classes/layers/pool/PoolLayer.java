package layers.pool;

import core.layer.ConvolutionSchemaPrinter;
import core.layer.Layer;
import core.layer.MatrixReader;
import core.layer.MatrixSchema;
import core.schema.SchemaRow;
import layers.convolution.ConvolutionUtils;
import core.schema.LayerSchema;
import maths.matrix.Matrix2D;

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

    public MatrixReader[] execute(MatrixReader[] inputChannels) {
        Objects.requireNonNull(inputChannels);
        if (inputChannels.length == 0) {
            throw new IllegalArgumentException("Need at least one core.matrix reader!");
        }

        MatrixReader[] output = new MatrixReader[inputChannels.length];

        for (int i=0; i<output.length; i++) {
            output[i] = computeMatrix(inputChannels[i]);
        }

        return output;
    }

    @Override
    public MatrixSchema[] getSchema(MatrixSchema[] inputChannels) {
        if (inputChannels.length == 0) throw new IllegalArgumentException(
            "Need at least one input channel."
        );

        MatrixSchema[] matrixSchemas = new MatrixSchema[inputChannels.length];
        int[] dimensions = null;
        for (int i = 0; i< inputChannels.length; i++) {
            dimensions = ConvolutionUtils.outputDimensions(
                inputChannels[i].getRowsCount(),
                inputChannels[i].getColumnsCount(),
                sampleSize,
                0,
                stride
            );

            matrixSchemas[i] = new LayerSchema(dimensions[0], dimensions[1]);
        }


        return matrixSchemas;
    }

    @Override
    public SchemaRow getSchemaRow(MatrixSchema[] inputSchema) {
        int[] dimensions = ConvolutionUtils.outputDimensions(
            inputSchema[0].getRowsCount(),
            inputSchema[0].getColumnsCount(),
            sampleSize,
            0,
            stride
        );

        return new SchemaRow()
            .setLayerType("Pool")
            .setChannelsCount(inputSchema.length)
            .setStride(stride+"x"+stride)
            .setSampleSize(sampleSize)
            .setOutput(inputSchema.length+"x"+dimensions[0]+"x"+dimensions[1]);
    }

    @Override
    public Layer copy() {
        return new PoolLayer(poolFunction, sampleSize, stride);
    }

    private MatrixReader computeMatrix(MatrixReader input) {
        int[] dimensions = ConvolutionUtils.outputDimensions(
                input.getRowsCount(),
                input.getColumnsCount(),
                sampleSize,
                0,
                stride
        );

        double[][] output = new double[dimensions[0]][dimensions[1]];

        int rowsCount = input.getRowsCount();
        int columnsCount = input.getColumnsCount();

        for (int i=0, outI=0; i<rowsCount - sampleSize; i += stride, outI++) {
            for (int j=0, outJ=0; j<columnsCount - sampleSize; j += stride, outJ++) {
                output[outI][outJ] = computeSample(input, i, j);
            }
        }

        return new Matrix2D(output);
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
