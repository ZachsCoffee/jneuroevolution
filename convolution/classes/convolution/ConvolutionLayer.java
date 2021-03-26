package convolution;

import filters.Filter;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixReader2D;

import java.util.Objects;

public class ConvolutionLayer implements Layer {
    private final MatrixReader matrixReader;
    private final Filter filter;
    private final int inputRows, inputColumns;
    private final int paddingRows, paddingColumns;
    private final int stride;
    private final double[][] output;
    //    private final boolean keepSize;

    public ConvolutionLayer(MatrixReader matrixReader, Filter filter, int stride, boolean keepSize) {
        this.matrixReader = Objects.requireNonNull(matrixReader);
        this.filter = Objects.requireNonNull(filter);

        if (stride < 1) {
            throw new IllegalArgumentException("Stride can't be smaller than 1.");
        }

        this.stride = stride;
//        this.keepSize = keepSize;

        // initial basic fields
        inputRows = matrixReader.getRowCount();
        inputColumns = matrixReader.getColumnCount();

        int kernelSize = filter.getKernelSize();

        int paddingRows = 0, paddingColumns = 0;
        if (keepSize) {
            paddingRows = ConvolutionUtils.padding(inputRows, kernelSize, stride);
            paddingColumns = ConvolutionUtils.padding(inputColumns, kernelSize, stride);
        }

        this.paddingRows = paddingRows;
        this.paddingColumns = paddingColumns;

        int rowsCount = ConvolutionUtils.outputDimension(inputRows, kernelSize, paddingRows, stride);
        int columnsCount = ConvolutionUtils.outputDimension(inputColumns, kernelSize, paddingColumns, stride);

        output = new double[rowsCount][columnsCount];
    }


    public MatrixReader computeLayer() {
        int kernelSize = filter.getKernelSize();

        for (int i= - paddingRows, outI = 0; i < inputRows + paddingRows - kernelSize; i += stride, outI++) {
            for (int j= - paddingColumns, outJ = 0; j < inputColumns + paddingColumns - kernelSize; j += stride, outJ++) {
                output[outI][outJ] = filter.compute(i, j, matrixReader);
            }
        }

        return new MatrixReader2D(output);
    }
}
