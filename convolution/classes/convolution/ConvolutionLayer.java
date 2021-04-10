package convolution;

import filters.Filter;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixReader2D;
import maths.matrix.MatrixSchema;
import maths.matrix.QubeSchema;
import schema.ConvolutionSchema;

import java.util.Objects;

public class ConvolutionLayer implements Layer {
    private final Filter[] filters;
    private final int stride;
    private final boolean keepSize;
//    private double[][] output;

    public ConvolutionLayer(Filter[] filters, int stride, boolean keepSize) {
        this.filters = Objects.requireNonNull(filters);

        if (filters.length == 0) {
            throw new RuntimeException("Need at least one filter");
        }
        if (stride < 1) {
            throw new IllegalArgumentException("Stride can't be smaller than 1.");
        }

        this.stride = stride;
        this.keepSize = keepSize;
    }

    public MatrixReader[] computeLayer(MatrixReader[] input) {
        Objects.requireNonNull(input);
        if (input.length == 0) {
            throw new IllegalArgumentException("Need at least one matrix reader!");
        }

        MatrixReader[] output = new MatrixReader[filters.length * input.length];
        Schema schema = new Schema();
        for (int i=0; i<input.length; i++) {
            for (int j=0; j<filters.length; j++) {
                output[i + j] = computeForFilter(input[i], filters[j], schema);
            }
        }

        return output;
    }

    @Override
    public MatrixReader[] computeLayer(MatrixReader[] input, QubeSchema qubeSchema) {
        Objects.requireNonNull(qubeSchema);
        Objects.requireNonNull(input);

        int outputDepth = filters.length * input.length;

        if (outputDepth != qubeSchema.getDepth()) throw new RuntimeException(
                "I can't create output of schema: " + qubeSchema + " with output depth "+outputDepth
        );



        return new MatrixReader[0];
    }

    @Override
    public MatrixSchema[] toString(MatrixSchema[] input, ConvolutionSchema convolutionSchema) {
        Schema schema = new Schema();

        MatrixSchema[] matrixSchemas = new MatrixSchema[input.length * filters.length];
        for (int i=0; i<input.length; i++) {
            for (int j=0; j<filters.length; j++) {
                schema.compute(input[i].getRowCount(), input[i].getColumnCount(), filters[j].getKernelSize());
                matrixSchemas[i * filters.length + j] = new LayerSchema(schema.rowsCount, schema.columnsCount);
            }
        }

        convolutionSchema.addRow(
                "Convolution",
                input.length,
                filters.length,
                "-",
                stride,
                schema.paddingRows + "x" + schema.paddingColumns,
                input.length + "x" + filters.length + "x" + schema.rowsCount + "x" + schema.columnsCount
        );

        return matrixSchemas;
    }

    private MatrixReader computeForFilter(MatrixReader matrixReader, Filter filter, Schema schema) {
        Objects.requireNonNull(matrixReader);

        int inputRows = matrixReader.getRowCount();
        int inputColumns = matrixReader.getColumnCount();
        int kernelSize = filter.getKernelSize();

        schema.compute(matrixReader.getRowCount(), matrixReader.getColumnCount(), filter.getKernelSize());

        double[][] output = new double[schema.rowsCount][schema.columnsCount];

        for (int i= - schema.paddingRows, outI = 0; i < inputRows + schema.paddingRows - kernelSize; i += stride, outI++) {
            for (int j= - schema.paddingColumns, outJ = 0; j < inputColumns + schema.paddingColumns - kernelSize; j += stride, outJ++) {
                output[outI][outJ] = filter.compute(i, j, matrixReader);
            }
        }

        return new MatrixReader2D(output);
    }

    private class Schema {
        int paddingRows, paddingColumns;
        int rowsCount, columnsCount;

        void compute(int inputRows, int inputColumns, int kernelSize) {
            if (keepSize) {
                paddingRows = ConvolutionUtils.padding(inputRows, kernelSize, stride);
                paddingColumns = ConvolutionUtils.padding(inputColumns, kernelSize, stride);
            }
            else {
                paddingRows = paddingColumns = 0;
            }

            rowsCount = ConvolutionUtils.outputDimension(inputRows, kernelSize, paddingRows, stride);
            columnsCount = ConvolutionUtils.outputDimension(inputColumns, kernelSize, paddingColumns, stride);
        }
    }
}
